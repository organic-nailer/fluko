package dev.fastriver.fluko.framework

import dev.fastriver.fluko.framework.element.Element
import dev.fastriver.fluko.framework.render.RenderView

object WidgetsFlukoBinding {
    lateinit var pipeline: RenderPipeline
    lateinit var engine: Engine
    var renderViewElement: Element? = null
    var initialized = false
    fun ensureInitialized(engine: Engine) {
        if(initialized) return
        initialized = true
        this.engine = engine
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

    fun drawFrame() {
        pipeline.flushLayout()
        pipeline.flushPaint()
        engine.render(pipeline.renderView!!.layer)
    }
}