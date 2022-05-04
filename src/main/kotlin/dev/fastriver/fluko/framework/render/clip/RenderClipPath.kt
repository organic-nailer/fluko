package dev.fastriver.fluko.framework.render.clip

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.layer.Clip
import dev.fastriver.fluko.common.layer.ClipPathLayer
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.gesture.HitTestResult
import dev.fastriver.fluko.framework.render.RenderBox
import org.jetbrains.skia.Path

class RenderClipPath(
    clipper: CustomClipper<Path>? = null, clipBehavior: Clip = Clip.AntiAlias
) : RenderCustomClip<Path>(clipper, clipBehavior) {

    override val defaultClip: Path
        get() = Path().apply {
            addRect(size.and(Offset.zero))
        }

    override fun hitTest(result: HitTestResult, position: Offset): Boolean {
        if(clipper != null) {
            updateClip()
            if(!clip!!.contains(position.dx.toFloat(), position.dy.toFloat())) {
                return false
            }
        }
        return super.hitTest(result, position)
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        if(child != null) {
            updateClip()
            layer = context.pushClipPath(
                offset,
                size.and(Offset.zero),
                clip!!,
                { c, o -> super.paint(c, o) },
                clipBehavior,
                layer as ClipPathLayer?
            )
        } else {
            layer = null
        }
    }
}