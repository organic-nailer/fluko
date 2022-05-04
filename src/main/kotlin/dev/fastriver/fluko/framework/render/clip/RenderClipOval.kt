package dev.fastriver.fluko.framework.render.clip

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.center
import dev.fastriver.fluko.common.layer.Clip
import dev.fastriver.fluko.common.layer.ClipPathLayer
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.gesture.HitTestResult
import dev.fastriver.fluko.framework.render.RenderBox
import org.jetbrains.skia.Path
import org.jetbrains.skia.Rect

class RenderClipOval(
    clipper: CustomClipper<Rect>? = null, clipBehavior: Clip = Clip.AntiAlias
) : RenderCustomClip<Rect>(clipper, clipBehavior) {
    override val defaultClip: Rect
        get() = size.and(Offset.zero)

    override fun hitTest(result: HitTestResult, position: Offset): Boolean {
        updateClip()
        val center = clip!!.center
        val offset = Offset(
            (position.dx - center.dx) / clip!!.width, (position.dy - center.dy) / clip!!.height
        )
        if(offset.dx * offset.dx + offset.dy * offset.dy > 0.25) {
            return false
        }
        return super.hitTest(result, position)
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        if(child != null) {
            updateClip()
            layer = context.pushClipPath(
                offset,
                clip!!,
                Path().apply { addOval(clip!!) },
                { c, o -> super.paint(c, o) },
                clipBehavior,
                layer as ClipPathLayer?
            )
        }
    }
}