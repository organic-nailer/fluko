package dev.fastriver.fluko.framework.widget.paint

import dev.fastriver.fluko.common.layer.Clip
import dev.fastriver.fluko.framework.render.clip.CustomClipper
import dev.fastriver.fluko.framework.render.clip.RenderClipPath
import dev.fastriver.fluko.framework.widget.primitive.SingleChildRenderObjectWidget
import dev.fastriver.fluko.framework.widget.primitive.Widget
import org.jetbrains.skia.Path

class ClipPath(
    val clipper: CustomClipper<Path>? = null,
    val clipBehavior: Clip = Clip.AntiAlias,
    child: Widget? = null
): SingleChildRenderObjectWidget<RenderClipPath>(child) {
    override fun createRenderObject(): RenderClipPath {
        return RenderClipPath(
            clipper = clipper,
            clipBehavior = clipBehavior
        )
    }

    override fun updateRenderObject(renderObject: RenderClipPath) {
        renderObject.let {
            it.clipper = clipper
            it.clipBehavior = clipBehavior
        }
    }
}