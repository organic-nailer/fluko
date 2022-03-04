package dev.fastriver.fluko.framework.element

import dev.fastriver.fluko.framework.SingleChildRenderObjectWidget
import dev.fastriver.fluko.framework.render.RenderObject
import dev.fastriver.fluko.framework.render.RenderObjectWithChildMixin

class SingleChildRenderObjectElement(widget: SingleChildRenderObjectWidget) : RenderObjectElement(widget) {
    val widgetCasted: SingleChildRenderObjectWidget = widget
    private var child: Element? = null

    override fun mount(parent: Element?) {
        super.mount(parent)
        child = updateChild(child, widgetCasted.child)
    }

    override fun insertRenderObjectChild(child: RenderObject) {
        (renderObject as RenderObjectWithChildMixin<RenderObject>).setRenderObjectChild(child)
    }
}