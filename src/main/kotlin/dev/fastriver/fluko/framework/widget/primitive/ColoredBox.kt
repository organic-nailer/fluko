package dev.fastriver.fluko.framework.widget.primitive

import dev.fastriver.fluko.framework.render.RenderColoredBox

class ColoredBox(
    child: Widget? = null,
    val color: Int,
) : SingleChildRenderObjectWidget<RenderColoredBox>(child) {
    override fun createRenderObject() = RenderColoredBox(color)

    override fun updateRenderObject(renderObject: RenderColoredBox) {
        renderObject.color = color
    }
}