import org.jetbrains.skija.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import kotlin.random.Random

class Rasterizer(private val width: Int, private val height: Int, private val context: DirectContext) {
    private val surface: Surface
    private val fbId: Int = GL11.glGetInteger(0x8CA6)

    init {
        val renderTarget = BackendRenderTarget.makeGL(
            width, height, 0, 8, fbId, FramebufferFormat.GR_GL_RGBA8
        )

        surface = Surface.makeFromBackendRenderTarget(
            context, renderTarget,
            SurfaceOrigin.BOTTOM_LEFT,
            SurfaceColorFormat.RGBA_8888,
            ColorSpace.getSRGB()
        )
    }

    fun drawToSurface(layerTree: LayerTree) {
        println("draw")
        surface.canvas.clear(0xFFFFFFFF.toInt())
        layerTree.paint(PaintContext(
            surface.canvas,
            context
        ))
//        val paint = Paint().apply { color = 0xFFFF0000.toInt() }
//
//        val randomX = Random.nextFloat() * width
//        val randomY = Random.nextFloat() * height
//
//        surface.canvas.clear(0xFFFFFFFF.toInt())
//        surface.canvas.drawCircle(randomX, randomY, 40f, paint)

        context.flush()
    }
}