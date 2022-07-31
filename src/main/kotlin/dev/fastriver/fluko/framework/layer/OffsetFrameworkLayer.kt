package dev.fastriver.fluko.framework.layer

import dev.fastriver.fluko.common.Offset

open class OffsetFrameworkLayer(offset: Offset = Offset.zero) : ContainerFrameworkLayer() {
    var offset by MarkAddToSceneProperty(offset)

    override fun addToScene(builder: UISceneBuilder) {
        engineLayer = builder.pushOffset(
            offset
        )
        addChildrenToScene(builder)
        builder.pop()
    }
}