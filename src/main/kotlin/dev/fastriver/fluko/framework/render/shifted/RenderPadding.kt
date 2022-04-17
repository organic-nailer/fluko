package dev.fastriver.fluko.framework.render.shifted

import dev.fastriver.fluko.common.EdgeInsets
import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.framework.render.BoxParentData
import dev.fastriver.fluko.framework.render.MarkLayoutProperty
import dev.fastriver.fluko.framework.render.RenderBox

class RenderPadding(
    padding: EdgeInsets,
    child: RenderBox? = null
): RenderShiftedBox(child) {
    var padding: EdgeInsets by MarkLayoutProperty(padding)

    override fun performLayout() {
        if (child == null) {
            size = constraints.constrain(Size(
                padding.left + padding.right,
                padding.top + padding.bottom
            ))
            return
        }
        val innerConstraints = constraints.deflate(padding)
        child!!.layout(innerConstraints, parentUsesSize = true)
        val childParentData = child!!.parentData as BoxParentData
        childParentData.offset = Offset(padding.left, padding.top)
        size = constraints.constrain(Size(
            padding.left + child!!.size.width + padding.right,
            padding.top + child!!.size.height + padding.bottom
        ))
    }
}