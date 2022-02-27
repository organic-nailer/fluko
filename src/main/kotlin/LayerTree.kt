import org.jetbrains.skija.Canvas
import org.jetbrains.skija.DirectContext
import org.jetbrains.skija.Picture
import org.jetbrains.skija.Rect

class LayerTree {
    var rootLayer: Layer? = null

    fun paint(context: PaintContext) {
        rootLayer?.paint(context)
    }
}

abstract class Layer {
    abstract fun paint(context: PaintContext)
}

class ContainerLayer: Layer() {
    val children: MutableList<Layer> = mutableListOf()

    override fun paint(context: PaintContext) {
        for(child in children) {
            child.paint(context)
        }
    }
}

class PictureLayer(
    val canvasBounds: Rect
): Layer() {
    var picture: Picture? = null

    override fun paint(context: PaintContext) {
        picture?.playback(context.canvas)
    }
}

data class PaintContext(
    val canvas: Canvas,
    val context: DirectContext
)