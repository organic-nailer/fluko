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

//    fun drawFrame() {
//        renderPipeline.flushLayout()
//        renderPipeline.flushPaint()
//        render()
//    }

    override val viewConfiguration: ViewConfiguration = ViewConfiguration(width, height)

    override fun render(rootLayer: Layer) {
        val layerTree = LayerTree().apply {
            this.rootLayer = rootLayer.clone()
        }
        taskRunners.rasterTaskRunner.postTask {
            rasterizer!!.drawToSurface(layerTree)
        }
    }

    fun run(appMain: () -> Unit) {
        taskRunners.uiTaskRunner.postTask {
            appMain()
        }
    }

    fun onVsync() {
        taskRunners.uiTaskRunner.postTask {
            binding.beginFrame()
        }
    }
}