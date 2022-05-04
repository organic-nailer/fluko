package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import org.jetbrains.skia.Rect

interface ParentData

open class BoxParentData(
    var offset: Offset = Offset.zero
) : ParentData

class StackParentData(
    var top: Double? = null,
    var right: Double? = null,
    var bottom: Double? = null,
    var left: Double? = null,
    var width: Double? = null,
    var height: Double? = null,
    offset: Offset = Offset.zero
): BoxParentData(offset) {
    var rect: Rect
        get() = Rect.makeLTRB(left!!.toFloat(), top!!.toFloat(), right!!.toFloat(), bottom!!.toFloat())
        set(value) {
            top = value.top.toDouble()
            right = value.right.toDouble()
            bottom = value.bottom.toDouble()
            left = value.left.toDouble()
        }

    val isPositioned: Boolean
        get() = top != null || right != null || bottom != null || left != null || width != null || height != null
}