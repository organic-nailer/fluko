package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.framework.geometrics.BoxConstraints

class RenderAspectRatio(
    aspectRatio: Double
) : RenderProxyBox() {
    var aspectRatio: Double by MarkLayoutProperty(aspectRatio)

    private fun applyAspectRatio(constraints: BoxConstraints): Size {
        if(constraints.isTight) return constraints.smallest

        var width = constraints.maxWidth
        var height: Double

        if(width.isFinite()) {
            height = width / aspectRatio
        } else {
            height = constraints.maxHeight
            width = height * aspectRatio
        }

        if(width > constraints.maxWidth) {
            width = constraints.maxWidth
            height = width / aspectRatio
        }

        if(height > constraints.maxHeight) {
            height = constraints.maxHeight
            width = height * aspectRatio
        }

        if(width < constraints.minWidth) {
            width = constraints.minWidth
            height = width / aspectRatio
        }

        if(height < constraints.minHeight) {
            height = constraints.minHeight
            width = height * aspectRatio
        }

        return constraints.constrain(Size(width, height))
    }

    override fun performLayout() {
        size = applyAspectRatio(constraints)
        child?.layout(BoxConstraints.tight(size))
    }
}