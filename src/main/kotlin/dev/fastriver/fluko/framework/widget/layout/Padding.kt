package dev.fastriver.fluko.framework.widget.layout

import dev.fastriver.fluko.common.EdgeInsets
import dev.fastriver.fluko.framework.render.shifted.RenderPadding
import dev.fastriver.fluko.framework.widget.primitive.SingleChildRenderObjectWidget
import dev.fastriver.fluko.framework.widget.primitive.Widget

class Padding(
    val padding: EdgeInsets,
    child: Widget? = null
): SingleChildRenderObjectWidget<RenderPadding>(child) {
    override fun createRenderObject(): RenderPadding {
        return RenderPadding(
            padding
        )
    }

    override fun updateRenderObject(renderObject: RenderPadding) {
        renderObject.padding = padding
    }
}