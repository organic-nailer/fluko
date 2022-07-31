package dev.fastriver.fluko.framework.render.clip

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.contains
import dev.fastriver.fluko.common.layer.Clip
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.gesture.HitTestResult
import dev.fastriver.fluko.framework.layer.ClipRRectFrameworkLayer
import dev.fastriver.fluko.framework.painting.BorderRadius
import org.jetbrains.skia.RRect

class RenderClipRRect(
    borderRadius: BorderRadius,
    clipper: CustomClipper<RRect>?,
    clipBehavior: Clip = Clip.AntiAlias
): RenderCustomClip<RRect>(clipper, clipBehavior) {
    var borderRadius: BorderRadius = borderRadius
        set(value) {
            if(field == value) return
            field = value
            markNeedsClip()
        }

    override val defaultClip: RRect
        get() = borderRadius.toRRect(size.and(Offset.zero))

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
            layer = context.pushClipRRect(
                offset, clip!!, clip!!, { c, o -> super.paint(c, o) }, clipBehavior, layer as ClipRRectFrameworkLayer?
            )
        } else {
            layer = null
        }
    }
}