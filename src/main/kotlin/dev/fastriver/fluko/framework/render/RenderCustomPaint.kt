package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.gesture.HitTestResult
import org.jetbrains.skia.Canvas

abstract class CustomPainter {
    abstract fun paint(canvas: Canvas, size: Size)

    abstract fun shouldRepaint(oldDelegate: CustomPainter): Boolean

    open fun hitTest(position: Offset): Boolean? = null
}

class RenderCustomPaint(
    painter: CustomPainter? = null, foregroundPainter: CustomPainter? = null
) : RenderProxyBox() {
    var painter: CustomPainter? = painter
        set(value) {
            if(field == value) return
            val oldPainter = field
            field = value
            didUpdatePainter(field, oldPainter)
        }

    var foregroundPainter: CustomPainter? = foregroundPainter
        set(value) {
            if(field == value) return
            val oldPainter = field
            field = value
            didUpdatePainter(field, oldPainter)
        }

    private fun didUpdatePainter(newPainter: CustomPainter?, oldPainter: CustomPainter?) {
        if(newPainter == null) {
            markNeedsPaint()
        } else if(oldPainter == null || newPainter::class != oldPainter::class || newPainter.shouldRepaint(oldPainter)) {
            markNeedsPaint()
        }
    }

    override fun hitTestChildren(result: HitTestResult, position: Offset): Boolean {
        if(foregroundPainter?.hitTest(position) == true) return true
        return super.hitTestChildren(result, position)
    }

    override fun hitTestSelf(position: Offset): Boolean {
        return painter?.hitTest(position) == true
    }

    private fun paintWithPainter(canvas: Canvas, offset: Offset, painter: CustomPainter) {
        canvas.save()
        if(offset != Offset.zero) {
            canvas.translate(offset.dx.toFloat(), offset.dy.toFloat())
        }
        painter.paint(canvas, size)
        canvas.restore()
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        painter?.let {
            paintWithPainter(context.canvas, offset, it)
        }
        super.paint(context, offset)
        foregroundPainter?.let {
            paintWithPainter(context.canvas, offset, it)
        }
    }
}