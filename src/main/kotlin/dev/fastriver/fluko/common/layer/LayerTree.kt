package dev.fastriver.fluko.common.layer

import dev.fastriver.fluko.common.Offset
import org.jetbrains.skija.*

class LayerTree {
    var rootLayer: Layer? = null

    fun paint(context: PaintContext) {
        rootLayer?.paint(context)
    }
}

abstract class Layer {
    abstract fun paint(context: PaintContext)

    abstract fun clone(): Layer
}

open class ContainerLayer: Layer() {
    val children: MutableList<Layer> = mutableListOf()

    override fun paint(context: PaintContext) {
        for(child in children) {
            child.paint(context)
        }
    }

    override fun clone(): Layer {
        val cloned = ContainerLayer()
        for(child in children) {
            cloned.children.add(child.clone())
        }
        return cloned
    }
}

class TransformLayer(
    val transform: Matrix44? = null,
    val offset: Offset? = null
): ContainerLayer() {
    override fun clone(): Layer {
        val cloned = TransformLayer(transform, offset)
        for(child in children) {
            cloned.children.add(child.clone())
        }
        return cloned
    }
}

class PictureLayer(
    val canvasBounds: Rect
): Layer() {
    var picture: Picture? = null

    override fun paint(context: PaintContext) {
        picture?.playback(context.canvas)
    }

    override fun clone(): Layer {
        val cloned = PictureLayer(canvasBounds)
        cloned.picture = picture
        return cloned
    }
}

data class PaintContext(
    val canvas: Canvas,
    val context: DirectContext
)