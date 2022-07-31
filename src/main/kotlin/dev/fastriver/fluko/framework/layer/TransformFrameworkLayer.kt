package dev.fastriver.fluko.framework.layer

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.math.Matrix4

class TransformFrameworkLayer(
    transform: Matrix4? = null, offset: Offset = Offset.zero
) : OffsetFrameworkLayer(offset) {
    var transform by MarkAddToSceneProperty(transform)

    override fun addToScene(builder: UISceneBuilder) {
        var lastEffectiveTransform = transform!!
        if(offset != Offset.zero) {
            lastEffectiveTransform = lastEffectiveTransform.leftTranslate(offset.dx.toFloat(), offset.dy.toFloat(), 0f)
        }
        engineLayer = builder.pushTransform(
            lastEffectiveTransform
        )
        addChildrenToScene(builder)
        builder.pop()
    }
}