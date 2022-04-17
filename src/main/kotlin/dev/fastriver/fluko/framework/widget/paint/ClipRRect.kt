package dev.fastriver.fluko.framework.widget.paint

import dev.fastriver.fluko.common.layer.Clip
import dev.fastriver.fluko.framework.painting.BorderRadius
import dev.fastriver.fluko.framework.render.clip.CustomClipper
import dev.fastriver.fluko.framework.render.clip.RenderClipRRect
import dev.fastriver.fluko.framework.widget.primitive.SingleChildRenderObjectWidget
import dev.fastriver.fluko.framework.widget.primitive.Widget
import org.jetbrains.skia.RRect

class ClipRRect(
    val borderRadius: BorderRadius = BorderRadius.zero,
    val clipper: CustomClipper<RRect>? = null,
    val clipBehavior: Clip = Clip.AntiAlias,
    child: Widget? = null
): SingleChildRenderObjectWidget<RenderClipRRect>(child) {
    override fun createRenderObject(): RenderClipRRect {
        return RenderClipRRect(
            borderRadius = borderRadius,
            clipper = clipper,
            clipBehavior = clipBehavior
        )
    }

    override fun updateRenderObject(renderObject: RenderClipRRect) {
        renderObject.let {
            it.borderRadius = borderRadius
            it.clipper = clipper
            it.clipBehavior = clipBehavior
        }
    }
}