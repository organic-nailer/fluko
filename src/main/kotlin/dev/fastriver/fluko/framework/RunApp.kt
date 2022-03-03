package dev.fastriver.fluko.framework

import dev.fastriver.fluko.common.layer.Layer

fun runApp(engine: Engine, app: Widget) {
    WidgetsFlukoBinding.apply {
        ensureInitialized(engine)
        attachRootWidget(app)
        drawFrame()
    }
}

interface Engine {
    val viewConfiguration: ViewConfiguration

    fun render(rootLayer: Layer)
}