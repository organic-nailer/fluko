package dev.fastriver.fluko.engine.layer

import dev.fastriver.fluko.common.Offset
import org.jetbrains.skia.Matrix33
import org.jetbrains.skia.Picture

class PictureEngineLayer(
    val offset: Offset, private val picture: Picture
) : EngineLayer() {

    override fun preroll(context: PrerollContext, matrix: Matrix33) {
        paintBounds = picture.cullRect
    }

    override fun paint(context: PaintContext) {
        picture.playback(context.canvas)
    }
}