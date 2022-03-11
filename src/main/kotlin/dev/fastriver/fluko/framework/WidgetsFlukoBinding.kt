package dev.fastriver.fluko.framework

import dev.fastriver.fluko.common.layer.ContainerLayer
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
        pipeline = RenderPipeline(
            onNeedVisualUpdate = {
                ensureVisualUpdate()
            }
        ).apply {
            renderView = RenderView(configuration.width.toDouble(), configuration.height.toDouble())
            renderView!!.prepareInitialFrame()
        }
    }

    fun ensureVisualUpdate() {
        //TODO: schedulerPhase
        engine.scheduleFrame()
    }

    fun attachRootWidget(rootWidget: Widget) {
        val isBootstrapFrame = renderViewElement == null
        renderViewElement = RenderObjectToWidgetAdapter(
            rootWidget, pipeline.renderView!!
        ).attachToRenderTree()
        if(isBootstrapFrame) {
            ensureVisualUpdate()
        }
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
        engine.render(pipeline.renderView!!.layer as ContainerLayer)
    }
}