package dev.fastriver.fluko.framework.render.shifted

import dev.fastriver.fluko.framework.geometrics.Alignment
import dev.fastriver.fluko.framework.render.BoxParentData
import dev.fastriver.fluko.framework.render.MarkLayoutProperty
import dev.fastriver.fluko.framework.render.RenderBox

abstract class RenderAligningShiftedBox(
    alignment: Alignment = Alignment.center
): RenderShiftedBox() {
    var alignment: Alignment by MarkLayoutProperty(alignment)

    protected fun alignChild() {
        val childParentData = child!!.parentData as BoxParentData
        childParentData.offset = alignment.computeOffset(size, child!!.size)
    }
}