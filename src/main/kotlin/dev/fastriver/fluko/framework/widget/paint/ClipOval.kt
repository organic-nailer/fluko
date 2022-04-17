package dev.fastriver.fluko.framework.widget.paint

import dev.fastriver.fluko.common.layer.Clip
import dev.fastriver.fluko.framework.render.clip.CustomClipper
import dev.fastriver.fluko.framework.render.clip.RenderClipOval
import dev.fastriver.fluko.framework.widget.primitive.SingleChildRenderObjectWidget
import dev.fastriver.fluko.framework.widget.primitive.Widget
import org.jetbrains.skia.Rect

class ClipOval(
    val clipper: CustomClipper<Rect>? = null,
    val clipBehavior: Clip = Clip.AntiAlias,
    child: Widget? = null
): SingleChildRenderObjectWidget<RenderClipOval>(child) {
    override fun createRenderObject(): RenderClipOval {
        return RenderClipOval(
            clipper = clipper,
            clipBehavior = clipBehavior
        )
    }

    override fun updateRenderObject(renderObject: RenderClipOval) {
        renderObject.let {
            it.clipper = clipper
            it.clipBehavior = clipBehavior
        }
    }
}