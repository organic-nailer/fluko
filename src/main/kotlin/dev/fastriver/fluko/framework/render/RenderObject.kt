package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.common.layer.ContainerLayer
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.RenderPipeline
import dev.fastriver.fluko.framework.geometrics.BoxConstraints
import javax.swing.Box
import kotlin.reflect.KProperty

abstract class RenderObject {
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

    fun layout(constraints: BoxConstraints, parentUsesSize: Boolean = false) {
        // relayoutBoundaryの再計算
        val relayoutBoundary = if(!parentUsesSize || sizedByParent || constraints.isTight || parent == null) {
            this
        }
        else {
            parent!!.relayoutBoundary
        }
        if(!needsLayout && this.constraints == constraints && this.relayoutBoundary == relayoutBoundary) {
            // 制約とrelayoutBoundaryに変化がなく再レイアウト要求も無ければなにもしない
            return
        }
        this.constraintsInternal = constraints
        if(this.relayoutBoundary != null && relayoutBoundary != this.relayoutBoundary) {
            // relayoutBoundaryに更新があった場合、子のrelayoutBoundaryを一旦リセットする
            visitChildren(cleanChildRelayoutBoundary)
        }
        this.relayoutBoundary = relayoutBoundary
        // FlutterではsizedByParentでの分岐が存在するが、
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
        }
        else {
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
        this.owner = owner //  TODO: call markNeedsXXX
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
        child.parent = this
        if(attached) {
            child.attach(owner!!)
        }
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
     * Implement先の[RenderObject.visitChildren]で必ず呼ぶ
     */
    fun visitChildren(visitor: RenderObjectVisitor) {
        child?.let(visitor)
    }

    class ChildDelegate<ChildType : RenderObject> {
        var child: ChildType? = null
        operator fun getValue(thisRef: RenderObject, property: KProperty<*>): ChildType? {
            return child
        }

        operator fun setValue(thisRef: RenderObject, property: KProperty<*>, value: ChildType?) {
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

    /**
     * Implement先の[RenderObject.attach]で必ず呼ぶ
     */
    fun attachChildren(owner: RenderPipeline) {
        for(child in children) {
            child.attach(owner)
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
}
