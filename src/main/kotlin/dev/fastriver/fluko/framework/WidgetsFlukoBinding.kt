package dev.fastriver.fluko.framework

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.PointerEvent
import dev.fastriver.fluko.common.PointerEventPhase
import dev.fastriver.fluko.common.layer.ContainerLayer
import dev.fastriver.fluko.framework.element.BuildOwner
import dev.fastriver.fluko.framework.element.Element
import dev.fastriver.fluko.framework.element.RenderObjectToWidgetElement
import dev.fastriver.fluko.framework.gesture.HitTestEntry
import dev.fastriver.fluko.framework.gesture.HitTestResult
import dev.fastriver.fluko.framework.gesture.HitTestTarget
import dev.fastriver.fluko.framework.render.RenderView
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

typealias FrameCallback = (Duration) -> Unit

object WidgetsFlukoBinding : WidgetsBinding, HitTestTarget {
    lateinit var pipeline: RenderPipeline
    lateinit var engine: Engine
    var renderViewElement: Element? = null
    var initialized = false
    var engineConnected = false
    var buildOwner: BuildOwner = BuildOwner { handleBuildScheduled() }
    private var needToReportFirstFrame = true
    private val hitTests = mutableMapOf<Int, HitTestResult>()
    private var nextFrameCallbackId = 0
    private val transientCallbacks = mutableMapOf<Int, FrameCallback>()
    private var hasScheduledFrame = false

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
        pipeline = RenderPipeline(onNeedVisualUpdate = {
            ensureVisualUpdate()
        }).apply {
            renderView = RenderView(configuration.width.toDouble(), configuration.height.toDouble())
            renderView!!.prepareInitialFrame()
        }
    }

    private fun ensureVisualUpdate() {
        //TODO: schedulerPhase
        scheduleFrame()
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

    override fun beginFrame(elapsedTime: Duration) {
        if(!initialized) return
        hasScheduledFrame = false
        // transient frame callbacks
        val callbacks = transientCallbacks.values.toList()
        transientCallbacks.clear()
        for(callback in callbacks) {
            callback(elapsedTime)
        }

        // persistent frame callbacks
        drawFrame()

        // post frame callbacks
    }

    /**
     * 次フレームを描画する
     *
     * WidgetsBinding.drawFrame() -> RendererBinding.drawFrame()
     */
    private fun drawFrame() {
        // WidgetsBinding.drawFrame
        if(renderViewElement != null) {
            buildOwner.buildScope()
        }

        // RendererBinding.drawFrame
        pipeline.flushLayout()
        pipeline.flushPaint()
        engine.render(pipeline.renderView!!.layer as ContainerLayer)
    }

    override fun handlePointerEvent(event: PointerEvent) {
        // GestureBinding._handlePointerEventImmediately()
        var hitTestResult: HitTestResult? = null
        if(event.phase == PointerEventPhase.Down) {
            hitTestResult = HitTestResult()
            hitTest(hitTestResult, event.position)
            hitTests[event.pointerId] = hitTestResult
        } else if(event.phase == PointerEventPhase.Up || event.phase == PointerEventPhase.Cancel) {
            hitTestResult = hitTests.remove(event.pointerId)
        } else if(event.phase == PointerEventPhase.Move) {
            hitTestResult = hitTests[event.pointerId]
        }
        if(hitTestResult != null || event.phase == PointerEventPhase.Add || event.phase == PointerEventPhase.Remove) {
            dispatchEvent(event, hitTestResult)
        }
    }

    private fun hitTest(hitTestResult: HitTestResult, position: Offset) {
        // RendererBinding.hitTest()
        pipeline.renderView!!.hitTest(hitTestResult, position)
        // GestureBinding.hitTest()
        hitTestResult.add(HitTestEntry(this))
    }

    override fun handleEvent(event: PointerEvent, entry: HitTestEntry) {
        // TODO: Gesture handling
    }

    private fun dispatchEvent(event: PointerEvent, hitTestResult: HitTestResult?) {
        //TODO: MouseTrackerEvent

        if(hitTestResult == null) {
            assert(event.phase == PointerEventPhase.Add || event.phase == PointerEventPhase.Remove)
            // pointerRouter.route(event)
            return
        }
        for(entry in hitTestResult.path) {
            entry.target.handleEvent(event.apply { transform = entry.transform }, entry)
        }
    }

    // SchedulerBinding
    fun scheduleFrameCallback(callback: FrameCallback): Int {
        scheduleFrame()
        nextFrameCallbackId++
        transientCallbacks[nextFrameCallbackId] = callback
        return nextFrameCallbackId
    }

    fun cancelFrameCallback(id: Int) {
        transientCallbacks.remove(id)
    }

    fun scheduleFrame() {
        if(hasScheduledFrame) return
        engine.scheduleFrame()
        hasScheduledFrame = true
    }
}