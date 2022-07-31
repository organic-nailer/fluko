package dev.fastriver.fluko.framework.layer

import dev.fastriver.fluko.common.Offset
import org.jetbrains.skia.Picture
import org.jetbrains.skia.Rect

class PictureFrameworkLayer(
    val canvasBounds: Rect
) : FrameworkLayer() {
    var picture: Picture? = null
        set(value) {
            markNeedsAddToScene()
            field?.close()
            field = value
        }

    override fun addToScene(builder: UISceneBuilder) {
        builder.addPicture(Offset.zero, picture!!)
    }
}