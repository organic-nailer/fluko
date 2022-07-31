package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.layer.OpacityFrameworkLayer
import kotlin.math.roundToInt

class RenderOpacity(
    opacity: Double = 1.0,
): RenderProxyBox() {
    private var alpha: Int = calcAlpha(opacity)

    var opacity: Double = opacity
        set(value) {
            if(field == value) return
            field = value
            alpha = calcAlpha(value)
            markNeedsPaint()
        }

    private fun calcAlpha(opacity: Double): Int {
        return (opacity.coerceIn(0.0..1.0) * 255).roundToInt()
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        child?.let {
            if(alpha == 0) {
                layer = null
                return
            }
            layer = context.pushOpacity(offset, alpha, { c, o -> super.paint(c, o) }, oldLayer = layer as OpacityFrameworkLayer?)
        }
    }
}