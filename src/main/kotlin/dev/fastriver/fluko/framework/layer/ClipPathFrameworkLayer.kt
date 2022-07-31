package dev.fastriver.fluko.framework.layer

import dev.fastriver.fluko.common.layer.Clip
import org.jetbrains.skia.Path

class ClipPathFrameworkLayer(
    clipPath: Path? = null, clipBehavior: Clip = Clip.HardEdge
) : ContainerFrameworkLayer() {
    var clipPath by MarkAddToSceneProperty(clipPath)
    var clipBehavior by MarkAddToSceneProperty(clipBehavior)

    override fun addToScene(builder: UISceneBuilder) {
        engineLayer = builder.pushClipPath(
            clipPath!!, clipBehavior
        )
        addChildrenToScene(builder)
        builder.pop()
    }
}