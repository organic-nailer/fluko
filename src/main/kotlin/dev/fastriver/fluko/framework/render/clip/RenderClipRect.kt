package dev.fastriver.fluko.framework.render.clip

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.contains
import dev.fastriver.fluko.common.layer.Clip
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.gesture.HitTestResult
import dev.fastriver.fluko.framework.layer.ClipRectFrameworkLayer
import org.jetbrains.skia.Rect

class RenderClipRect(
    clipper: CustomClipper<Rect>? = null, clipBehavior: Clip = Clip.AntiAlias
) : RenderCustomClip<Rect>(clipper, clipBehavior) {
    override val defaultClip: Rect
        get() = size.and(Offset.zero)

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
            layer = context.pushClipRect(
                offset, clip!!, { c, o -> super.paint(c, o) }, clipBehavior, layer as ClipRectFrameworkLayer?
            )
        } else {
            layer = null
        }
    }
}