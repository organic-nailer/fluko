package dev.fastriver.fluko.engine.layer

import dev.fastriver.fluko.common.layer.Clip
import org.jetbrains.skia.Matrix33
import org.jetbrains.skia.Path
import org.jetbrains.skia.Rect

class ClipPathEngineLayer(
    var clipPath: Path, var clipBehavior: Clip = Clip.AntiAlias
) : ContainerEngineLayer() {
    override fun preroll(context: PrerollContext, matrix: Matrix33) {
        val previousCullRect = context.cullRect
        val clipPathBounds = clipPath.bounds
        if(context.cullRect.intersect(clipPathBounds) == null) {
            context.cullRect = Rect.makeWH(0f, 0f)
        }
        val childPaintBounds = prerollChildren(context, matrix)
        if(childPaintBounds.intersect(clipPathBounds) != null) {
            paintBounds = childPaintBounds
        }

        context.cullRect = previousCullRect
    }

    override fun paint(context: PaintContext) {
        context.canvas.save()
        context.canvas.clipPath(clipPath, clipBehavior != Clip.HardEdge)

        super.paint(context)
        context.canvas.restore()
    }
}