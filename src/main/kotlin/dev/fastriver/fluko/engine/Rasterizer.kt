package dev.fastriver.fluko.engine

import dev.fastriver.fluko.engine.layer.LayerTree
import dev.fastriver.fluko.engine.layer.PaintContext
import org.jetbrains.skia.*
import org.lwjgl.opengl.GL11

class Rasterizer(width: Int, height: Int, val context: DirectContext) {
    private lateinit var surface: Surface
    private val fbId: Int = GL11.glGetInteger(0x8CA6)

    init {
        updateMetrics(width, height)
    }

    fun updateMetrics(width: Int, height: Int) {
        val renderTarget = BackendRenderTarget.makeGL(
            width, height, 0, 8, fbId, FramebufferFormat.GR_GL_RGBA8
        )

        surface = Surface.makeFromBackendRenderTarget(
            context, renderTarget, SurfaceOrigin.BOTTOM_LEFT, SurfaceColorFormat.RGBA_8888, ColorSpace.sRGB
        )!!
    }

    fun drawToSurface(layerTree: LayerTree) {
        // println("draw")
        layerTree.preroll()

        surface.canvas.clear(0xFFFFFFFF.toInt())
        layerTree.paint(
            PaintContext(
                surface.canvas, context
            )
        )
        context.flush()
    }
}