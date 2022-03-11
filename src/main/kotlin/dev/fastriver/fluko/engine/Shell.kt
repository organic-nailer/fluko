package dev.fastriver.fluko.engine

import dev.fastriver.fluko.common.layer.Layer
import dev.fastriver.fluko.common.layer.LayerTree
import dev.fastriver.fluko.framework.*

class Shell(
    val taskRunners: TaskRunners,
    var glView: GLView,
    var rasterizer: Rasterizer?,
    val width: Int, val height: Int
): Engine {
    private var binding: WidgetsBinding = WidgetsFlukoBinding
    private var vsyncCallback: (() -> Unit)? = null

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
            vsyncCallback = {
                taskRunners.uiTaskRunner.postTask {
                    // Flutter Animatorだと分岐があるけど
                    // 基本的にlayerTreeは再利用されないようなので
                    // beginFrameのみを呼ぶ
                    binding.beginFrame()
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

    fun run(appMain: () -> Unit) {
        taskRunners.uiTaskRunner.postTask {
            appMain()
        }
    }

    fun onVsync() {
        vsyncCallback?.invoke()
        vsyncCallback = null
    }
}