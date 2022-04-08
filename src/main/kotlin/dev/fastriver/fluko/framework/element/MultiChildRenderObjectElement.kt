package dev.fastriver.fluko.framework.element

import dev.fastriver.fluko.framework.MultiChildRenderObjectWidget
import dev.fastriver.fluko.framework.Widget
import dev.fastriver.fluko.framework.render.ContainerRenderObject
import dev.fastriver.fluko.framework.render.RenderObject

class MultiChildRenderObjectElement(widget: MultiChildRenderObjectWidget) : RenderObjectElement(widget) {
    val widgetCasted: MultiChildRenderObjectWidget
        get() = widget as MultiChildRenderObjectWidget
    private var children: List<Element> = listOf()

    override fun visitChildren(visitor: ElementVisitor) {
        children.forEach(visitor)
    }

    override fun mount(parent: Element?) {
        super.mount(parent)
        children = widgetCasted.children.map {
            inflateWidget(it)
        }
    }

    override fun update(newWidget: Widget) {
        super.update(newWidget)
        //TODO: forgottenChildren
        children = updateChildren(children, widgetCasted.children)
    }

    override fun insertRenderObjectChild(child: RenderObject) {
        (renderObject as ContainerRenderObject<RenderObject>).insert(child)
    }

    override fun removeRenderObjectChild(child: RenderObject) {
        (renderObject as ContainerRenderObject<RenderObject>).remove(child)
    }
}