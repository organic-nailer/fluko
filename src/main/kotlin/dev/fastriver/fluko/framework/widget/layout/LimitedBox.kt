package dev.fastriver.fluko.framework.widget.layout

import dev.fastriver.fluko.framework.render.RenderLimitedBox
import dev.fastriver.fluko.framework.widget.primitive.SingleChildRenderObjectWidget
import dev.fastriver.fluko.framework.widget.primitive.Widget

class LimitedBox(
    val maxWidth: Double = Double.MAX_VALUE,
    val maxHeight: Double = Double.MAX_VALUE,
    child: Widget? = null
): SingleChildRenderObjectWidget<RenderLimitedBox>(child) {
    override fun createRenderObject(): RenderLimitedBox {
        return RenderLimitedBox(
            maxWidth, maxHeight
        )
    }

    override fun updateRenderObject(renderObject: RenderLimitedBox) {
        renderObject.let {
            it.maxWidth = maxWidth
            it.maxHeight = maxHeight
        }
    }
}