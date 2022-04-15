package dev.fastriver.fluko.framework.widget.primitive

import dev.fastriver.fluko.framework.animation.AnimationController
import dev.fastriver.fluko.framework.render.RenderAnimatedOpacity

class FadeTransition(
    val opacity: AnimationController, child: Widget? = null
) : SingleChildRenderObjectWidget<RenderAnimatedOpacity>(child) {
    override fun createRenderObject(): RenderAnimatedOpacity {
        return RenderAnimatedOpacity(
            opacity = opacity
        )
    }

    override fun updateRenderObject(renderObject: RenderAnimatedOpacity) {
        renderObject.let {
            it.opacity = opacity
        }
    }
}
