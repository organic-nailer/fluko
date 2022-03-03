package dev.fastriver.fluko.engine

import dev.fastriver.fluko.common.layer.Layer
import dev.fastriver.fluko.common.layer.LayerTree
import dev.fastriver.fluko.framework.Engine
import dev.fastriver.fluko.framework.RenderPipeline
import dev.fastriver.fluko.framework.ViewConfiguration

class Shell(
    val taskRunners: TaskRunners,
    var glView: GLView,
    var rasterizer: Rasterizer?,
    val width: Int, val height: Int
): Engine {
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
            this.rootLayer = rootLayer
        }
        taskRunners.rasterTaskRunner.postTask {
            rasterizer!!.drawToSurface(layerTree)
            glView.swapBuffers()
        }
    }
}