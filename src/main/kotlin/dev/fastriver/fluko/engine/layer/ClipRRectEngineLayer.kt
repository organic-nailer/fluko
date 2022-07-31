package dev.fastriver.fluko.engine.layer

import dev.fastriver.fluko.common.layer.Clip
import org.jetbrains.skia.Matrix33
import org.jetbrains.skia.RRect
import org.jetbrains.skia.Rect

class ClipRRectEngineLayer(
    var clipRRect: RRect, var clipBehavior: Clip = Clip.AntiAlias
) : ContainerEngineLayer() {
    override fun preroll(context: PrerollContext, matrix: Matrix33) {
        val previousCullRect = context.cullRect
        if(context.cullRect.intersect(clipRRect) == null) {
            context.cullRect = Rect.makeWH(0f, 0f)
        }
        val childPaintBounds = prerollChildren(context, matrix)
        if(childPaintBounds.intersect(clipRRect) != null) {
            paintBounds = childPaintBounds
        }

        context.cullRect = previousCullRect
    }

    override fun paint(context: PaintContext) {
        context.canvas.save()
        context.canvas.clipRRect(clipRRect, clipBehavior != Clip.HardEdge)

        super.paint(context)
        context.canvas.restore()
    }
}