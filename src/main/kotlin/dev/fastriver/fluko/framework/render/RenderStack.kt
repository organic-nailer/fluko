package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.common.layer.Clip
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.geometrics.Alignment
import dev.fastriver.fluko.framework.geometrics.BoxConstraints
import dev.fastriver.fluko.framework.gesture.HitTestResult
import dev.fastriver.fluko.framework.layer.ClipRectFrameworkLayer
import kotlin.math.max

enum class StackFit {
    Loose, Expand, PassThrough
}

class RenderStack(
    alignment: Alignment,
    fit: StackFit = StackFit.Loose,
    clipBehavior: Clip = Clip.HardEdge
): RenderBox(), ContainerRenderObject<RenderBox> {
    companion object {
        private fun layoutPositionedChild(child: RenderBox, childParentData: StackParentData, size: Size, alignment: Alignment): Boolean {
            var hasVisualOverflow = false
            var childConstraints = BoxConstraints()

            if(childParentData.left != null && childParentData.right != null) {
                childConstraints = childConstraints.tighten(width = size.width - childParentData.right!! - childParentData.left!!)
            }
            else if(childParentData.width != null) {
                childConstraints = childConstraints.tighten(width = childParentData.width)
            }

            if(childParentData.top != null && childParentData.bottom != null) {
                childConstraints = childConstraints.tighten(height = size.height - childParentData.top!! - childParentData.bottom!!)
            }
            else if(childParentData.height != null) {
                childConstraints = childConstraints.tighten(height = childParentData.height)
            }

            child.layout(childConstraints, parentUsesSize = true)

            val x = when {
                childParentData.left != null -> childParentData.left!!
                childParentData.right != null -> size.width - childParentData.right!! - child.size.width
                else -> alignment.alongOffset((size - child.size) as Offset).dx
            }

            if(x < 0.0 || x + child.size.width > size.width) hasVisualOverflow = true

            val y = when {
                childParentData.top != null -> childParentData.top!!
                childParentData.bottom != null -> size.height - childParentData.bottom!! - child.size.height
                else -> alignment.alongOffset((size - child.size) as Offset).dy
            }

            if(y < 0.0 || y + child.size.height > size.height) hasVisualOverflow = true

            childParentData.offset = Offset(x,y)

            return hasVisualOverflow
        }
    }

    var alignment: Alignment by MarkLayoutProperty(alignment)
    var fit: StackFit by MarkLayoutProperty(fit)
    var clipBehavior: Clip by MarkPaintProperty(clipBehavior)
    private var hasVisualOverflow = false
    private var clipRectLayer: ClipRectFrameworkLayer? = null

    override val thisRef: RenderObject = this
    override val children: MutableList<RenderBox> = mutableListOf()

    override fun visitChildren(visitor: RenderObjectVisitor) {
        super<ContainerRenderObject>.visitChildren(visitor)
    }

    override fun redepthChildren() {
        super<ContainerRenderObject>.redepthChildren { redepthChild(it) }
    }

    override fun setupParentData(child: RenderObject) {
        if (child.parentData !is StackParentData) {
            child.parentData = StackParentData()
        }
    }

    private fun computeSize(constraints: BoxConstraints): Size {
        var hasNonPositionedChildren = false
        if (children.isEmpty()) {
            return constraints.biggest
        }

        var width = constraints.minWidth
        var height = constraints.minHeight

        val nonPositionedConstraints = when(fit) {
            StackFit.Loose -> constraints.loosen()
            StackFit.Expand -> BoxConstraints.tight(constraints.biggest)
            StackFit.PassThrough -> constraints
        }

        for(child in children) {
            val childParentData = child.parentData as StackParentData
            if(!childParentData.isPositioned) {
                hasNonPositionedChildren = true

                child.layout(nonPositionedConstraints, parentUsesSize = true)
                val childSize = child.size

                width = max(width, childSize.width)
                height = max(height, childSize.height)
            }
        }

        return if(hasNonPositionedChildren) {
            Size(width, height)
        } else {
            constraints.biggest
        }
    }

    override fun performLayout() {
        val constraints = this.constraints
        hasVisualOverflow = false

        size = computeSize(constraints)

        for(child in children) {
            val childParentData = child.parentData as StackParentData

            if(!childParentData.isPositioned) {
                childParentData.offset = alignment.alongOffset((size - child.size) as Offset)
            } else {
                hasVisualOverflow = layoutPositionedChild(child, childParentData, size, alignment) || hasVisualOverflow
            }
        }
    }

    override fun hitTestChildren(result: HitTestResult, position: Offset): Boolean {
        return defaultHitTestChildren(result, position)
    }

    private fun paintStack(context: PaintingContext, offset: Offset) {
        for(child in children) {
            val childParentData = child.parentData as StackParentData
            context.paintChild(child, childParentData.offset + offset)
        }
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        if(clipBehavior != Clip.None && hasVisualOverflow) {
            clipRectLayer = context.pushClipRect(
                offset, size.and(Offset.zero), { c, o -> paintStack(c,o) },
                clipBehavior, oldLayer = clipRectLayer
            )
        }
        else {
            clipRectLayer = null
            paintStack(context, offset)
        }
    }
}