package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.geometrics.BoxConstraints

abstract class RenderObject {
    var parentData: ParentData? = null

    abstract var size: Size

    abstract fun layout(constraints: BoxConstraints)

    abstract fun paint(context: PaintingContext, offset: Offset)
}

interface RenderObjectWithChildMixin<ChildType: RenderObject> {
    var child: ChildType?

    fun setRenderObjectChild(child: ChildType) {
        this.child = child
        this.child!!.parentData = BoxParentData()
    }
}

interface ContainerRenderObjectMixin<ChildType: RenderObject> {
    val children: MutableList<ChildType>

    fun insertChild(child: ChildType) {
        children.add(child)
        child.parentData = BoxParentData()
    }
}
