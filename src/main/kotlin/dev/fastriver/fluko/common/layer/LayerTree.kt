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
    var parent: ContainerLayer? = null

    abstract fun paint(context: PaintContext)
    abstract fun clone(): Layer

    fun remove() {
        parent?.removeChild(this)
    }

    // AbstractNode
    fun dropChild(child: Layer) {
        child.parent = null
    }

    fun adoptChild(child: Layer) {
        child.parent = this as ContainerLayer
    }
}

open class ContainerLayer : Layer() {
    protected val childrenInternal: MutableList<Layer> = mutableListOf()
    val children: List<Layer>
        get() = childrenInternal

    override fun paint(context: PaintContext) {
        for(child in children) {
            child.paint(context)
        }
    }

    override fun clone(): Layer {
        val cloned = ContainerLayer()
        for(child in children) {
            cloned.append(child.clone())
        }
        return cloned
    }

    fun append(child: Layer) {
        adoptChild(child)
        childrenInternal.add(child)
    }

    fun removeChild(child: Layer) {
        childrenInternal.remove(child)
        child.parent = null
    }

    fun removeAllChildren() {
        for(layer in childrenInternal) {
            dropChild(layer)
        }
        childrenInternal.clear()
    }
}

open class OffsetLayer(
    open var offset: Offset = Offset.zero
) : ContainerLayer() {}

class TransformLayer(
    val transform: Matrix44? = null, offset: Offset
) : OffsetLayer(offset) {
    override fun clone(): Layer {
        val cloned = TransformLayer(transform, offset)
        for(child in children) {
            cloned.append(child.clone())
        }
        return cloned
    }
}

class PictureLayer(
    val canvasBounds: Rect
) : Layer() {
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
    val canvas: Canvas, val context: DirectContext
)