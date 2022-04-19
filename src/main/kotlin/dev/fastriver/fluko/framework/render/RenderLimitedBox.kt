package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.framework.geometrics.BoxConstraints

class RenderLimitedBox(
    maxWidth: Double = Double.MAX_VALUE,
    maxHeight: Double = Double.MAX_VALUE
): RenderProxyBox() {
    var maxWidth: Double by MarkLayoutProperty(maxWidth)
    var maxHeight: Double by MarkLayoutProperty(maxHeight)

    private fun limitConstraints(constraints: BoxConstraints): BoxConstraints {
        return BoxConstraints(
            minWidth = constraints.minWidth,
            maxWidth = if(constraints.maxWidth < Double.MAX_VALUE) constraints.maxWidth else constraints.constrainWidth(maxWidth),
            minHeight = constraints.minHeight,
            maxHeight = if(constraints.maxHeight < Double.MAX_VALUE) constraints.maxHeight else constraints.constrainHeight(maxHeight)
        )
    }

    override fun performLayout() {
        if(child != null) {
            child!!.layout(limitConstraints(constraints), parentUsesSize = true)
            val childSize = child!!.size
            size = constraints.constrain(childSize)
        }
        else {
            size = limitConstraints(constraints).constrain(Size.zero)
        }
    }
}