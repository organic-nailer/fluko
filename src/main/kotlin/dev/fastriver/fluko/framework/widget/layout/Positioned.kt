package dev.fastriver.fluko.framework.widget.layout

import dev.fastriver.fluko.framework.render.RenderObject
import dev.fastriver.fluko.framework.render.StackParentData
import dev.fastriver.fluko.framework.widget.primitive.ParentDataWidget
import dev.fastriver.fluko.framework.widget.primitive.Widget
import org.jetbrains.skia.Rect

class Positioned(
    child: Widget,
    val left: Double? = null,
    val top: Double? = null,
    val right: Double? = null,
    val bottom: Double? = null,
    val width: Double? = null,
    val height: Double? = null,
): ParentDataWidget<StackParentData>(child) {
    companion object {
        fun fromRect(rect: Rect, child: Widget): Positioned {
            return Positioned(
                left = rect.left.toDouble(),
                top = rect.top.toDouble(),
                width = rect.width.toDouble(),
                height = rect.height.toDouble(),
                child = child
            )
        }

        fun fill(left: Double? = 0.0, top: Double? = 0.0, right: Double? = 0.0, bottom: Double? = 0.0, child: Widget): Positioned {
            return Positioned(
                child, left, top, right, bottom
            )
        }
    }

    init {
        assert(left == null || right == null || width == null)
        assert(top == null || bottom == null || height == null)
    }

    override fun applyParentData(renderObject: RenderObject) {
        val parentData = renderObject.parentData as StackParentData
        var needsLayout = false

        if(parentData.left != left) {
            parentData.left = left
            needsLayout = true
        }

        if(parentData.top != top) {
            parentData.top = top
            needsLayout = true
        }

        if(parentData.right != right) {
            parentData.right = right
            needsLayout = true
        }

        if(parentData.bottom != bottom) {
            parentData.bottom = bottom
            needsLayout = true
        }

        if(parentData.width != width) {
            parentData.width = width
            needsLayout = true
        }

        if(parentData.height != height) {
            parentData.height = height
            needsLayout = true
        }

        if(needsLayout) {
            if(renderObject.parent is RenderObject) {
                renderObject.parent!!.markNeedsLayout()
            }
        }
    }
}