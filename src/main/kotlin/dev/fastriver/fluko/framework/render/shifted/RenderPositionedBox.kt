package dev.fastriver.fluko.framework.render.shifted

import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.framework.geometrics.Alignment
import dev.fastriver.fluko.framework.render.MarkLayoutProperty
import dev.fastriver.fluko.framework.render.RenderBox

class RenderPositionedBox(
    widthFactor: Double? = null,
    heightFactor: Double? = null,
    alignment: Alignment = Alignment.center
) : RenderAligningShiftedBox(alignment) {
    var widthFactor: Double? by MarkLayoutProperty(widthFactor)
    var heightFactor: Double? by MarkLayoutProperty(heightFactor)

    override fun performLayout() {
        val shrinkWrapWidth = widthFactor != null || constraints.maxWidth == Double.POSITIVE_INFINITY
        val shrinkWrapHeight = heightFactor != null || constraints.maxHeight == Double.POSITIVE_INFINITY

        if(child != null) {
            child!!.layout(constraints.loosen(), parentUsesSize = true)
            size = constraints.constrain(
                Size(
                    if(shrinkWrapWidth) child!!.size.width * (widthFactor ?: 0.0) else Double.POSITIVE_INFINITY,
                    if(shrinkWrapHeight) child!!.size.height * (heightFactor ?: 0.0) else Double.POSITIVE_INFINITY,
                )
            )
            alignChild()
        } else {
            size = constraints.constrain(
                Size(
                    if(shrinkWrapWidth) 0.0 else Double.POSITIVE_INFINITY,
                    if(shrinkWrapHeight) 0.0 else Double.POSITIVE_INFINITY
                )
            )
        }
    }
}