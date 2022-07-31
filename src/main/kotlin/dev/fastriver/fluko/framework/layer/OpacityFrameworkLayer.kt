package dev.fastriver.fluko.framework.layer

import dev.fastriver.fluko.common.Offset

class OpacityFrameworkLayer(
    alpha: Int? = null, offset: Offset = Offset.zero
) : OffsetFrameworkLayer(offset) {
    var alpha by MarkAddToSceneProperty(alpha)

    override fun addToScene(builder: UISceneBuilder) {
        engineLayer = builder.pushOpacity(
            alpha!!, offset
        )
        addChildrenToScene(builder)
        builder.pop()
    }
}