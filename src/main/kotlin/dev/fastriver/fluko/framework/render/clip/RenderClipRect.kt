package dev.fastriver.fluko.framework.render.clip

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.contains
import dev.fastriver.fluko.common.layer.Clip
import dev.fastriver.fluko.common.layer.ClipRectLayer
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.gesture.HitTestResult
import dev.fastriver.fluko.framework.render.RenderBox
import org.jetbrains.skia.Rect

class RenderClipRect(
    child: RenderBox? = null, clipper: CustomClipper<Rect>? = null, clipBehavior: Clip = Clip.AntiAlias
) : RenderCustomClip<Rect>(child, clipper, clipBehavior) {
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
                offset, clip!!, { c, o -> super.paint(c, o) }, clipBehavior, layer as ClipRectLayer?
            )
        } else {
            layer = null
        }
    }
}