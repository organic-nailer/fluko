package dev.fastriver.fluko.framework.widget.primitive

import dev.fastriver.fluko.framework.render.PointerEventListener
import dev.fastriver.fluko.framework.render.RenderPointerListener

class Listener(
    child: Widget? = null,
    val onPointerDown: PointerEventListener? = null,
    val onPointerMove: PointerEventListener? = null,
    val onPointerUp: PointerEventListener? = null,
    val onPointerCancel: PointerEventListener? = null,
) : SingleChildRenderObjectWidget<RenderPointerListener>(child) {
    override fun createRenderObject(): RenderPointerListener {
        return RenderPointerListener(
            onPointerDown, onPointerMove, onPointerUp, onPointerCancel
        )
    }

    override fun updateRenderObject(renderObject: RenderPointerListener) {
        renderObject.let {
            it.onPointerDown = onPointerDown
            it.onPointerMove = onPointerMove
            it.onPointerUp = onPointerUp
            it.onPointerCancel = onPointerCancel
        }
    }
}