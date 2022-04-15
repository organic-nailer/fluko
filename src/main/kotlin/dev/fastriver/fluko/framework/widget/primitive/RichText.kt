package dev.fastriver.fluko.framework.widget.primitive

import dev.fastriver.fluko.framework.render.RenderParagraph
import dev.fastriver.fluko.framework.render.TextSpan

class RichText(
    val text: TextSpan,
) : MultiChildRenderObjectWidget<RenderParagraph>(listOf()) {
    override fun createRenderObject(): RenderParagraph {
        return RenderParagraph(
            text
        )
    }

    override fun updateRenderObject(renderObject: RenderParagraph) {
        renderObject.let {
            it.text = text
        }
    }
}