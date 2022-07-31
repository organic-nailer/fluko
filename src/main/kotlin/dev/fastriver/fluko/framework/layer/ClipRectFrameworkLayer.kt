package dev.fastriver.fluko.framework.layer

import dev.fastriver.fluko.common.layer.Clip
import org.jetbrains.skia.Rect

class ClipRectFrameworkLayer(
    clipRect: Rect? = null, clipBehavior: Clip = Clip.HardEdge
) : ContainerFrameworkLayer() {
    var clipRect by MarkAddToSceneProperty(clipRect)
    var clipBehavior by MarkAddToSceneProperty(clipBehavior)

    override fun addToScene(builder: UISceneBuilder) {
        engineLayer = builder.pushClipRect(
            clipRect!!, clipBehavior
        )
        addChildrenToScene(builder)
        builder.pop()
    }
}