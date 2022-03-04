package dev.fastriver.fluko.framework.element

import dev.fastriver.fluko.framework.MultiChildRenderObjectWidget
import dev.fastriver.fluko.framework.render.ContainerRenderObjectMixin
import dev.fastriver.fluko.framework.render.RenderObject

class MultiChildRenderObjectElement(widget: MultiChildRenderObjectWidget) : RenderObjectElement(widget) {
    val widgetCasted: MultiChildRenderObjectWidget = widget
    private var children: List<Element> = listOf()

    override fun mount(parent: Element?) {
        super.mount(parent)
        children = widgetCasted.children.map {
            inflateWidget(it)
        }
    }

    override fun insertRenderObjectChild(child: RenderObject) {
        (renderObject as ContainerRenderObjectMixin<RenderObject>).insertChild(child)
    }
}