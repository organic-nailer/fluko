package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.common.layer.ContainerLayer
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.RenderPipeline
import dev.fastriver.fluko.framework.geometrics.BoxConstraints
import dev.fastriver.fluko.framework.gesture.HitTestResult
import dev.fastriver.fluko.framework.gesture.HitTestTarget
import javax.swing.Box
import kotlin.reflect.KProperty

abstract class RenderObject: HitTestTarget {
    companion object {
        private val cleanChildRelayoutBoundary: RenderObjectVisitor = {
            it.cleanRelayoutBounary()
        }
    }

    var parentData: ParentData? = null
    var parent: RenderObject? = null
    var needsPaint = true
    open val isRepaintBoundary: Boolean = false
    var owner: RenderPipeline? = null
    val constraints: BoxConstraints
        get() = constraintsInternal!!
    private var constraintsInternal: BoxConstraints? = null
    var needsLayout = true
    var relayoutBoundary: RenderObject? = null
    open val sizedByParent: Boolean = false
    var depth: Int = 0

    fun redepthChild(child: RenderObject) {
        if(child.depth <= depth) {
            child.depth = depth + 1
            child.redepthChildren()
        }
    }

    open fun redepthChildren() {}

    /**
     * 同じ階層のLayerを保持する
     *
     * [isRepaintBoundary] == true のときしか使われない。
     * [RenderView]ならばscheduleInitialPaintで、その他はrepaintCompositedChildで代入される
     */
    var layer: ContainerLayer? = null

    val attached: Boolean
        get() = owner != null

    abstract var size: Size

    abstract fun performLayout()

    abstract fun paint(context: PaintingContext, offset: Offset)

    fun layout(constraints: BoxConstraints, parentUsesSize: Boolean = false) { // relayoutBoundaryの再計算
        val relayoutBoundary = if(!parentUsesSize || sizedByParent || constraints.isTight || parent == null) {
            this
        } else {
            parent!!.relayoutBoundary
        }
        if(!needsLayout && this.constraints == constraints && this.relayoutBoundary == relayoutBoundary) { // 制約とrelayoutBoundaryに変化がなく再レイアウト要求も無ければなにもしない
            return
        }
        this.constraintsInternal = constraints
        if(this.relayoutBoundary != null && relayoutBoundary != this.relayoutBoundary) { // relayoutBoundaryに更新があった場合、子のrelayoutBoundaryを一旦リセットする
            visitChildren(cleanChildRelayoutBoundary)
        }
        this.relayoutBoundary = relayoutBoundary // FlutterではsizedByParentでの分岐が存在するが、
        // 簡略化のためperformResizeの機能はperformLayout内に移動する
        performLayout()

        needsLayout = false
        markNeedsPaint()
    }

    /**
     * constraintsをそのままlayoutする
     */
    fun layoutWithoutResize() {
        assert(constraintsInternal != null)
        performLayout()

        needsLayout = false
        markNeedsPaint()
    }

    fun cleanRelayoutBounary() {
        if(relayoutBoundary != this) {
            relayoutBoundary = null
            needsLayout = true
            visitChildren(cleanChildRelayoutBoundary)
        }
    }

    fun markNeedsPaint() {
        if(needsPaint) return
        needsPaint = true
        if(isRepaintBoundary) {
            owner?.let {
                it.nodesNeedingPaint.add(this)
                it.requestVisualUpdate()
            }
        } else if(parent is RenderObject) {
            parent!!.markNeedsPaint()
        } else { // root node
            owner?.requestVisualUpdate()
        }
    }

    fun markNeedsLayout() {
        if(needsLayout) return
        if(relayoutBoundary != this) {
            markParentNeedsLayout()
        } else {
            needsLayout = true
            owner?.let {
                it.nodesNeedingLayout.add(this)
                it.requestVisualUpdate()
            }
        }
    }

    fun markParentNeedsLayout() {
        needsLayout = true
        parent!!.markNeedsLayout()
    }

    open fun attach(owner: RenderPipeline) {
        this.owner = owner
        if(needsLayout && relayoutBoundary != null) {
            needsLayout = false
            markNeedsLayout()
        }
        if(needsPaint && layer != null) {
            needsPaint = false
            markNeedsPaint()
        }
    }

    open fun detach() {
        this.owner = null
    }

    open fun setupParentData(child: RenderObject) {

    }

    /**
     * 子へのアクセスの抽象化。
     *
     * 子が存在する場合、その子の数だけコールバックが呼ばれる
     */
    open fun visitChildren(visitor: RenderObjectVisitor) {

    }

    fun adoptChild(child: RenderObject) {
        setupParentData(child)
        markNeedsLayout()
        child.parent = this
        if(attached) {
            child.attach(owner!!)
        }
        redepthChild(child)
    }

    fun dropChild(child: RenderObject) {
        child.cleanRelayoutBounary()
        child.parentData = null
        child.parent = null
        if (attached) {
            child.detach()
        }
        markNeedsLayout()
    }

    /**
     * RenderObjectを破棄する時に呼ぶ
     *
     * layerの参照を持っていれば捨てる
     */
    open fun dispose() {
        layer = null
    }
}

typealias RenderObjectVisitor = (child: RenderObject) -> Unit

interface RenderObjectWithChild<ChildType : RenderObject> {
    var child: ChildType?

    //    fun setRenderObjectChild(child: ChildType) {
    //        this.child = child
    //        this.child!!.parentData = BoxParentData()
    //    }

    /**
     * Implement先の[RenderObject.attach]で必ず呼ぶ
     */
    fun attachChild(owner: RenderPipeline) {
        child?.attach(owner)
    }

    /**
     * Implement先の[RenderObject.detach]で必ず呼ぶ
     */
    fun detachChild() {
        child?.detach()
    }

    /**
     * Implement先の[RenderObject.visitChildren]で必ず呼ぶ
     */
    fun visitChildren(visitor: RenderObjectVisitor) {
        child?.let(visitor)
    }

    /**
     * Implement先の[RenderObject.redepthChildren]で必ず呼ぶ
     */
    fun redepthChildren(callback: (child: RenderObject) -> Unit) {
        child?.let(callback)
    }

    class ChildDelegate<ChildType : RenderObject> {
        var child: ChildType? = null
        operator fun getValue(thisRef: RenderObject, property: KProperty<*>): ChildType? {
            return child
        }

        operator fun setValue(thisRef: RenderObject, property: KProperty<*>, value: ChildType?) {
            if(child != null) {
                thisRef.dropChild(child!!)
            }
            child = value
            child?.let {
                thisRef.adoptChild(it)
            }
        }
    }
}

interface ContainerRenderObject<ChildType : RenderObject> {
    val children: MutableList<ChildType>
    val thisRef: RenderObject

    fun insert(child: ChildType) {
        thisRef.adoptChild(child)
        children.add(child) // child.parentData = BoxParentData()
    }

    fun remove(child: ChildType) {
        children.remove(child)
        thisRef.dropChild(child)
    }

    /**
     * Implement先の[RenderObject.attach]で必ず呼ぶ
     */
    fun attachChildren(owner: RenderPipeline) {
        for(child in children) {
            child.attach(owner)
        }
    }

    /**
     * Implement先の[RenderObject.detach]で必ず呼ぶ
     */
    fun detachChildren() {
        for(child in children) {
            child.detach()
        }
    }

    /**
     * Implement先の[RenderObject.visitChildren]で必ず呼ぶ
     */
    fun visitChildren(visitor: RenderObjectVisitor) {
        for(child in children) {
            visitor(child)
        }
    }

    /**
     * Implement先の[RenderObject.redepthChildren]で必ず呼ぶ
     */
    fun redepthChildren(callback: (child: RenderObject) -> Unit) {
        for(child in children) {
            child.let(callback)
        }
    }

    /**
     * 複数の子を持つ場合の標準のHitTest
     *
     * [ChildType] is [RenderBox] のときのみ呼び出し可
     *
     * どれかの子に判定があれば即終了する
     */
    fun defaultHitTestChildren(result: HitTestResult, position: Offset): Boolean {
        for(child in children) {
            val childParentData = child.parentData as BoxParentData
            val isHit = result.addWithPaintOffset(
                offset = childParentData.offset,
                position = position,
                hitTest = { result, transformed ->
                    (child as RenderBox).hitTest(result, transformed)
                }
            )
            if(isHit) {
                return true
            }
        }
        return false
    }
}

class MarkPaintProperty<T>(initialValue: T) {
    var child: T = initialValue
    operator fun getValue(thisRef: RenderObject, property: KProperty<*>): T {
        return child
    }

    operator fun setValue(thisRef: RenderObject, property: KProperty<*>, value: T) {
        if(child == value) return
        child = value
        thisRef.markNeedsPaint()
    }
}

class MarkLayoutProperty<T>(initialValue: T) {
    var child: T = initialValue
    operator fun getValue(thisRef: RenderObject, property: KProperty<*>): T {
        return child
    }

    operator fun setValue(thisRef: RenderObject, property: KProperty<*>, value: T) {
        if(child == value) return
        child = value
        thisRef.markNeedsLayout()
    }
}
