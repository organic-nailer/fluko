package dev.fastriver.fluko.framework.widget.paint

import dev.fastriver.fluko.common.layer.Clip
import dev.fastriver.fluko.framework.render.clip.CustomClipper
import dev.fastriver.fluko.framework.render.clip.RenderClipRect
import dev.fastriver.fluko.framework.widget.primitive.SingleChildRenderObjectWidget
import dev.fastriver.fluko.framework.widget.primitive.Widget
import org.jetbrains.skia.Rect

class ClipRect(
    val clipper: CustomClipper<Rect>? = null,
    val clipBehavior: Clip = Clip.AntiAlias,
    child: Widget? = null
): SingleChildRenderObjectWidget<RenderClipRect>(child) {
    override fun createRenderObject(): RenderClipRect {
        return RenderClipRect(
            clipper = clipper,
            clipBehavior = clipBehavior
        )
    }

    override fun updateRenderObject(renderObject: RenderClipRect) {
        renderObject.let {
            it.clipper = clipper
            it.clipBehavior = clipBehavior
        }
    }
}