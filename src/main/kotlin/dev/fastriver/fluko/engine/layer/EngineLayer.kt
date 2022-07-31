package dev.fastriver.fluko.engine.layer

import dev.fastriver.fluko.framework.layer.FrameworkLayer
import org.jetbrains.skia.Matrix33
import org.jetbrains.skia.Rect

abstract class EngineLayer : FrameworkLayer.UIEngineLayer {
    var paintBounds: Rect = Rect.makeWH(0f, 0f)
    abstract fun paint(context: PaintContext)
    abstract fun preroll(context: PrerollContext, matrix: Matrix33)
}