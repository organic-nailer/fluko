package dev.fastriver.fluko.framework

import dev.fastriver.fluko.common.KeyEvent
import dev.fastriver.fluko.common.PointerEvent
import dev.fastriver.fluko.common.layer.Layer
import dev.fastriver.fluko.framework.widget.primitive.Widget
import kotlin.time.Duration

fun runApp(app: Widget) {
    WidgetsFlukoBinding.apply {
        ensureInitialized()
        attachRootWidget(app)
        // drawFrame()
    }
}

interface Engine {
    val viewConfiguration: ViewConfiguration

    fun render(rootLayer: Layer)

    fun scheduleFrame()
}

interface WidgetsBinding {
    fun connectToEngine(engine: Engine)

    fun beginFrame(elapsedTime: Duration)

    fun handlePointerEvent(event: PointerEvent)

    fun handleKeyEvent(event: KeyEvent)
}