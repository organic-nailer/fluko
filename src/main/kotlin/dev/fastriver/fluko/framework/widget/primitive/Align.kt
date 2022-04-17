package dev.fastriver.fluko.framework.widget.primitive

import dev.fastriver.fluko.framework.geometrics.Alignment
import dev.fastriver.fluko.framework.render.shifted.RenderPositionedBox

open class Align(
    child: Widget?,
    val alignment: Alignment = Alignment.center,
    val widthFactor: Double? = null,
    val heightFactor: Double? = null,
) : SingleChildRenderObjectWidget<RenderPositionedBox>(child) {
    override fun createRenderObject(): RenderPositionedBox {
        return RenderPositionedBox(
            alignment = alignment, widthFactor = widthFactor, heightFactor = heightFactor
        )
    }
}