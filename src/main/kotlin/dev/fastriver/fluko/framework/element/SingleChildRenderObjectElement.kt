package dev.fastriver.fluko.framework.element

import dev.fastriver.fluko.framework.SingleChildRenderObjectWidget
import dev.fastriver.fluko.framework.Widget
import dev.fastriver.fluko.framework.render.RenderObject
import dev.fastriver.fluko.framework.render.RenderObjectWithChild

class SingleChildRenderObjectElement(widget: SingleChildRenderObjectWidget) : RenderObjectElement(widget) {
    val widgetCasted: SingleChildRenderObjectWidget
        get() = widget as SingleChildRenderObjectWidget
    private var child: Element? = null

    override fun visitChildren(visitor: ElementVisitor) {
        child?.let(visitor)
    }

    override fun mount(parent: Element?) {
        super.mount(parent)
        child = updateChild(child, widgetCasted.child)
    }

    override fun update(newWidget: Widget) {
        super.update(newWidget)
        child = updateChild(child, widgetCasted.child)
    }

    override fun insertRenderObjectChild(child: RenderObject) {
        (renderObject as RenderObjectWithChild<RenderObject>).child = child
    }

    override fun removeRenderObjectChild(child: RenderObject) {
        (renderObject as RenderObjectWithChild<RenderObject>).child = null
    }
}