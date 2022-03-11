package dev.fastriver.fluko.framework

import dev.fastriver.fluko.common.layer.Layer

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

    fun beginFrame()
}