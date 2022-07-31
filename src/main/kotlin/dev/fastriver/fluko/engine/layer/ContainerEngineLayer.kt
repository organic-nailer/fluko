package dev.fastriver.fluko.engine.layer

import dev.fastriver.fluko.common.join
import dev.fastriver.fluko.common.kEmptyRect
import org.jetbrains.skia.Matrix33
import org.jetbrains.skia.Rect

open class ContainerEngineLayer : EngineLayer() {
    private val childrenInternal: MutableList<EngineLayer> = mutableListOf()
    val children: List<EngineLayer>
        get() = childrenInternal

    fun add(layer: EngineLayer) {
        childrenInternal.add(layer)
    }

    override fun preroll(context: PrerollContext, matrix: Matrix33) {
        paintBounds = prerollChildren(context, matrix)
    }

    /**
     * 子の矩形を計算しその和を返す
     */
    protected fun prerollChildren(context: PrerollContext, childMatrix: Matrix33): Rect {
        var bounds = kEmptyRect
        for(child in children) {
            val rect = child.preroll(context, childMatrix)
            bounds = bounds.join(child.paintBounds)
        }
        return bounds
    }

    override fun paint(context: PaintContext) {
        for(child in children) {
            child.paint(context)
        }
    }
}