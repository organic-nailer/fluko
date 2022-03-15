package dev.fastriver.fluko.framework

import dev.fastriver.fluko.common.layer.ContainerLayer
import dev.fastriver.fluko.framework.element.BuildOwner
import dev.fastriver.fluko.framework.element.Element
import dev.fastriver.fluko.framework.element.RenderObjectToWidgetElement
import dev.fastriver.fluko.framework.render.RenderView

object WidgetsFlukoBinding: WidgetsBinding {
    lateinit var pipeline: RenderPipeline
    lateinit var engine: Engine
    var renderViewElement: Element? = null
    var initialized = false
    var engineConnected = false
    var buildOwner: BuildOwner = BuildOwner { handleBuildScheduled() }
    private var needToReportFirstFrame = true
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

    private fun ensureVisualUpdate() {
        //TODO: schedulerPhase
        engine.scheduleFrame()
    }

    private fun handleBuildScheduled() {
        ensureVisualUpdate()
    }

    fun attachRootWidget(rootWidget: Widget) {
        val isBootstrapFrame = renderViewElement == null
        renderViewElement = RenderObjectToWidgetAdapter(
            rootWidget, pipeline.renderView!!
        ).attachToRenderTree(buildOwner, renderViewElement as RenderObjectToWidgetElement?)
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

    /**
     * 次フレームを描画する
     *
     * WidgetsBinding.drawFrame() -> RendererBinding.drawFrame()
     */
    fun drawFrame() {
        // WidgetsBinding.drawFrame
        if(renderViewElement != null) {
            buildOwner.buildScope()
        }

        // RendererBinding.drawFrame
        pipeline.flushLayout()
        pipeline.flushPaint()
        engine.render(pipeline.renderView!!.layer as ContainerLayer)
    }
}