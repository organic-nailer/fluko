package dev.fastriver.fluko.engine

import dev.fastriver.fluko.common.KeyEvent
import dev.fastriver.fluko.common.PointerEvent
import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.common.layer.Layer
import dev.fastriver.fluko.common.layer.LayerTree
import dev.fastriver.fluko.framework.Engine
import dev.fastriver.fluko.framework.ViewConfiguration
import dev.fastriver.fluko.framework.WidgetsBinding
import dev.fastriver.fluko.framework.WidgetsFlukoBinding
import kotlin.time.Duration

class Shell(
    private val taskRunners: TaskRunners, var rasterizer: Rasterizer?, val width: Int, val height: Int
) : Engine, GLView.GLViewDelegate {
    var glView: GLView = GLView(width, height, this)
    private var binding: WidgetsBinding = WidgetsFlukoBinding
    private var vsyncCallback: ((Duration) -> Unit)? = null
    private val timingMeasurer = TimingMeasurer()

    init {
        binding.connectToEngine(this)
    }

    fun initRasterThread() {
        taskRunners.rasterTaskRunner.postTask {
            println("in rasterThread")
            val context = glView.createContext()
            rasterizer = Rasterizer(width, height, context)
        }
    }

    override fun scheduleFrame() {
        if(vsyncCallback == null) {
            vsyncCallback = { elapsedTime ->
                taskRunners.uiTaskRunner.postTask {
                    // Flutter Animatorだと分岐があるけど
                    // 基本的にlayerTreeは再利用されないようなので
                    // beginFrameのみを呼ぶ
                    binding.beginFrame(elapsedTime)
                }
            }
        }
    }

    override var viewConfiguration: ViewConfiguration = ViewConfiguration(Size(width.toDouble(),height.toDouble()))

    override fun render(rootLayer: Layer) {
        val layerTree = LayerTree().apply {
            this.rootLayer = rootLayer.clone()
        }
        taskRunners.rasterTaskRunner.postTask {
            rasterizer!!.drawToSurface(layerTree)
            glView.swapBuffers()
        }
    }

    /**
    * クリックイベントが流れてくる関数
    */
    override fun onPointerEvent(event: PointerEvent) {
        binding.handlePointerEvent(event)
    }

    /**
    * キーボードイベントが流れてくる関数
    */
    override fun onKeyEvent(event: KeyEvent) {
        binding.handleKeyEvent(event)
    }

    /**
    * ウィンドウサイズが変更された場合に呼ばれる関数
    */
    override fun onWindowMetricsChanged(width: Int, height: Int) {
        viewConfiguration = ViewConfiguration(Size(width.toDouble(),height.toDouble()))
        rasterizer!!.updateMetrics(width, height)
        binding.handleMetricsChanged()
    }

    /**
    * UIスレッドの開始
    */
    fun run(appMain: () -> Unit) {
        taskRunners.uiTaskRunner.postTask {
            appMain()
        }
    }

    /**
    * 垂直同期信号
    */
    fun onVsync() {
        val elapsedTime = timingMeasurer.getElapsedTime()
        vsyncCallback?.invoke(elapsedTime)
        vsyncCallback = null
    }
}
