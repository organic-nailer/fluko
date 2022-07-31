package dev.fastriver.fluko.framework.layer

import dev.fastriver.fluko.common.layer.Clip
import org.jetbrains.skia.RRect

class ClipRRectFrameworkLayer(
    clipRRect: RRect? = null, clipBehavior: Clip = Clip.HardEdge
) : ContainerFrameworkLayer() {
    var clipRRect by MarkAddToSceneProperty(clipRRect)
    var clipBehavior by MarkAddToSceneProperty(clipBehavior)

    override fun addToScene(builder: UISceneBuilder) {
        engineLayer = builder.pushClipRRect(
            clipRRect!!, clipBehavior
        )
        addChildrenToScene(builder)
        builder.pop()
    }
}