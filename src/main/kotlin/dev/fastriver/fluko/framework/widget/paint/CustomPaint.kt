package dev.fastriver.fluko.framework.widget.paint

import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.framework.render.CustomPainter
import dev.fastriver.fluko.framework.render.RenderCustomPaint
import dev.fastriver.fluko.framework.widget.primitive.SingleChildRenderObjectWidget
import dev.fastriver.fluko.framework.widget.primitive.Widget

class CustomPaint(
    val painter: CustomPainter? = null,
    val foregroundPainter: CustomPainter? = null,
    child: Widget? = null
): SingleChildRenderObjectWidget<RenderCustomPaint>(child) {
    override fun createRenderObject(): RenderCustomPaint {
        return RenderCustomPaint(
            painter, foregroundPainter
        )
    }

    override fun updateRenderObject(renderObject: RenderCustomPaint) {
        renderObject.let {
            it.painter = painter
            it.foregroundPainter = foregroundPainter
        }
    }
}