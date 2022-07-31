package dev.fastriver.fluko.engine.layer

import dev.fastriver.fluko.common.invert
import dev.fastriver.fluko.common.kGiantRect
import dev.fastriver.fluko.common.mapRect
import org.jetbrains.skia.Matrix33

class TransformEngineLayer(
    val transform: Matrix33 = Matrix33.IDENTITY
) : ContainerEngineLayer() {
    override fun preroll(context: PrerollContext, matrix: Matrix33) {
        val childMatrix = matrix.makeConcat(transform)
        val previousCullRect = context.cullRect

        val inverseTransform = transform.invert()
        if(inverseTransform != null) {
            context.cullRect = inverseTransform.mapRect(context.cullRect)
        } else {
            context.cullRect = kGiantRect
        }

        val childPaintBounds = prerollChildren(context, childMatrix)
        paintBounds = transform.mapRect(childPaintBounds)

        context.cullRect = previousCullRect
    }
}