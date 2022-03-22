package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.PointerEvent
import dev.fastriver.fluko.common.PointerEventPhase
import dev.fastriver.fluko.framework.gesture.HitTestEntry

typealias PointerEventListener = (PointerEvent) -> Unit

class RenderPointerListener(
    var onPointerDown: PointerEventListener?,
    var onPointerMove: PointerEventListener?,
    var onPointerUp: PointerEventListener?,
    var onPointerCancel: PointerEventListener?
): RenderProxyBox() {
    override fun handleEvent(event: PointerEvent, entry: HitTestEntry) {
        when(event.phase) {
            PointerEventPhase.Down -> onPointerDown?.invoke(event)
            PointerEventPhase.Move -> onPointerMove?.invoke(event)
            PointerEventPhase.Up -> onPointerUp?.invoke(event)
            PointerEventPhase.Cancel -> onPointerCancel?.invoke(event)
        }
    }
}