package dev.fastriver.fluko.framework

import dev.fastriver.fluko.framework.element.Element
import dev.fastriver.fluko.framework.render.RenderView

object WidgetsFlukoBinding: WidgetsBinding {
    lateinit var pipeline: RenderPipeline
    lateinit var engine: Engine
    var renderViewElement: Element? = null
    var initialized = false
    var engineConnected = false
    override fun connectToEngine(engine: Engine) {
        this.engine = engine
        engineConnected = true
    }

    fun ensureInitialized() {
        if(!engineConnected) {
            throw Exception("tried to initialize before connecting to engine.")
        }
        if(initialized) return
        initialized = true
        val configuration = engine.viewConfiguration
        pipeline = RenderPipeline().apply {
            renderView = RenderView(configuration.width.toDouble(), configuration.height.toDouble())
        }
    }

    fun attachRootWidget(rootWidget: Widget) {
        renderViewElement = RenderObjectToWidgetAdapter(
            rootWidget, pipeline.renderView!!
        ).attachToRenderTree()
    }

    fun scheduleWarmUpFrame() {

    }

    override fun beginFrame() {
        if(!initialized) return
        drawFrame()
    }

    fun drawFrame() {
        pipeline.flushLayout()
        pipeline.flushPaint()
        engine.render(pipeline.renderView!!.layer)
    }
}