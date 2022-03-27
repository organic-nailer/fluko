package dev.fastriver.fluko.engine

import dev.fastriver.fluko.common.PointerEvent
import dev.fastriver.fluko.common.layer.Layer
import dev.fastriver.fluko.common.layer.LayerTree
import dev.fastriver.fluko.framework.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class Shell(
    val taskRunners: TaskRunners,
    var rasterizer: Rasterizer?,
    val width: Int, val height: Int
): Engine, GLView.GLViewDelegate {
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

    override val viewConfiguration: ViewConfiguration = ViewConfiguration(width, height)

    override fun render(rootLayer: Layer) {
        val layerTree = LayerTree().apply {
            this.rootLayer = rootLayer.clone()
        }
        taskRunners.rasterTaskRunner.postTask {
            rasterizer!!.drawToSurface(layerTree)
            glView.swapBuffers()
        }
    }

    override fun onPointerEvent(event: PointerEvent) {
        binding.handlePointerEvent(event)
    }

    fun run(appMain: () -> Unit) {
        taskRunners.uiTaskRunner.postTask {
            appMain()
        }
    }

    fun onVsync() {
        val elapsedTime = timingMeasurer.getElapsedTime()
        vsyncCallback?.invoke(elapsedTime)
        vsyncCallback = null
    }
}