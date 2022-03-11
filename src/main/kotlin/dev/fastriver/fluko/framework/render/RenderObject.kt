package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.common.layer.ContainerLayer
import dev.fastriver.fluko.common.layer.OffsetLayer
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.RenderPipeline
import dev.fastriver.fluko.framework.geometrics.BoxConstraints
import kotlin.reflect.KProperty

abstract class RenderObject {
    var parentData: ParentData? = null
    var parent: RenderObject? = null
    var needsPaint = true
    open val isRepaintBoundary: Boolean = false
    var owner: RenderPipeline? = null

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

    abstract fun performLayout(constraints: BoxConstraints)

    abstract fun paint(context: PaintingContext, offset: Offset)

    fun layout(constraints: BoxConstraints) {
        performLayout(constraints)

        markNeedsPaint()
    }

    fun markNeedsPaint() {
        if(needsPaint) return
        needsPaint = true
        if(isRepaintBoundary) {
            owner?.let {
                it.nodeNeedingPaint.add(this)
                it.requestVisualUpdate()
            }
        } else if(parent is RenderObject) {
            parent!!.markNeedsPaint()
        } else { // root node
            owner?.requestVisualUpdate()
        }
    }

    open fun attach(owner: RenderPipeline) {
        this.owner = owner //  TODO: call markNeedsXXX
    }

    open fun setupParentData(child: RenderObject) {

    }

    fun adoptChild(child: RenderObject) {
        setupParentData(child)
        child.parent = this
        if(attached) {
            child.attach(owner!!)
        }
    }
}

interface RenderObjectWithChild<ChildType : RenderObject> {
    var child: ChildType?

    //    fun setRenderObjectChild(child: ChildType) {
    //        this.child = child
    //        this.child!!.parentData = BoxParentData()
    //    }

    fun attachChild(owner: RenderPipeline) {
        child?.attach(owner)
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

    fun attachChildren(owner: RenderPipeline) {
        for(child in children) {
            child.attach(owner)
        }
    }
}
