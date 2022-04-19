package dev.fastriver.fluko.framework.widget.layout

import dev.fastriver.fluko.common.layer.Clip
import dev.fastriver.fluko.framework.geometrics.Alignment
import dev.fastriver.fluko.framework.painting.BoxFit
import dev.fastriver.fluko.framework.render.RenderFittedBox
import dev.fastriver.fluko.framework.widget.primitive.SingleChildRenderObjectWidget
import dev.fastriver.fluko.framework.widget.primitive.Widget

class FittedBox(
    val fit: BoxFit = BoxFit.Contain,
    val alignment: Alignment = Alignment.center,
    val clipBehavior: Clip = Clip.None,
    child: Widget? = null
): SingleChildRenderObjectWidget<RenderFittedBox>(child) {
    override fun createRenderObject(): RenderFittedBox {
        return RenderFittedBox(
            fit, alignment,
            clipBehavior = clipBehavior
        )
    }

    override fun updateRenderObject(renderObject: RenderFittedBox) {
        renderObject.let {
            it.fit = fit
            it.alignment = alignment
            it.clipBehavior = clipBehavior
        }
    }
}