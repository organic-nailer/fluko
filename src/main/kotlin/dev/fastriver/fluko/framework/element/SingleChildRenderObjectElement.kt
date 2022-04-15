package dev.fastriver.fluko.framework.element

import dev.fastriver.fluko.framework.widget.primitive.SingleChildRenderObjectWidget
import dev.fastriver.fluko.framework.widget.primitive.Widget
import dev.fastriver.fluko.framework.render.RenderObject
import dev.fastriver.fluko.framework.render.RenderObjectWithChild

class SingleChildRenderObjectElement<T: RenderObject>(widget: SingleChildRenderObjectWidget<T>) : RenderObjectElement<T>(widget) {
    val widgetCasted: SingleChildRenderObjectWidget<T>
        get() = widget as SingleChildRenderObjectWidget<T>
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