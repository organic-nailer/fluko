package dev.fastriver.fluko.engine.layer

import dev.fastriver.fluko.common.layer.Clip
import org.jetbrains.skia.Matrix33
import org.jetbrains.skia.Rect

class ClipRectEngineLayer(
    var clipRect: Rect, var clipBehavior: Clip = Clip.AntiAlias
) : ContainerEngineLayer() {
    override fun preroll(context: PrerollContext, matrix: Matrix33) {
        val previousCullRect = context.cullRect
        if(context.cullRect.intersect(clipRect) == null) {
            context.cullRect = Rect.makeWH(0f, 0f)
        }
        val childPaintBounds = prerollChildren(context, matrix)
        if(childPaintBounds.intersect(clipRect) != null) {
            paintBounds = childPaintBounds
        }

        context.cullRect = previousCullRect
    }

    override fun paint(context: PaintContext) {
        context.canvas.save()
        context.canvas.clipRect(clipRect, clipBehavior != Clip.HardEdge)

        super.paint(context)
        context.canvas.restore()
    }
}