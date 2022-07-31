package dev.fastriver.fluko.engine.layer

import dev.fastriver.fluko.common.kGiantRect
import org.jetbrains.skia.Matrix33

class LayerTree {
    var rootLayer: EngineLayer? = null

    fun preroll() {
        assert(rootLayer != null)

        val context = PrerollContext(
            kGiantRect
        )
        rootLayer!!.preroll(context, Matrix33.IDENTITY)
    }

    fun paint(context: PaintContext) {
        rootLayer?.paint(context)
    }
}