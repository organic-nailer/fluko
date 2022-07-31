package dev.fastriver.fluko.engine.layer

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.makeOffset
import dev.fastriver.fluko.common.roundOut
import dev.fastriver.fluko.common.transform
import org.jetbrains.skia.Matrix33
import org.jetbrains.skia.Paint

class OpacityEngineLayer(
    val alpha: Int? = null, val offset: Offset = Offset.zero
) : ContainerEngineLayer() {
    override fun preroll(context: PrerollContext, matrix: Matrix33) {
        val childMatrix = matrix.transform(offset)

        context.cullRect = context.cullRect.makeOffset(-offset.dx.toFloat(), -offset.dy.toFloat())

        super.preroll(context, matrix)

        paintBounds = paintBounds.makeOffset(offset.dx.toFloat(), offset.dy.toFloat())

        context.cullRect = context.cullRect.makeOffset(offset.dx.toFloat(), offset.dy.toFloat())
    }

    override fun paint(context: PaintContext) {
        val paint = Paint()
        if(alpha != null) {
            paint.alpha = alpha
        }
        context.canvas.save()
        context.canvas.translate(offset.dx.toFloat(), offset.dy.toFloat())
        val saveLayerBounds = paintBounds.makeOffset(-offset.dx.toFloat(), -offset.dy.toFloat()).roundOut()
        context.canvas.saveLayer(saveLayerBounds, paint)
        super.paint(context)
        context.canvas.restore()
        context.canvas.restore()
    }
}