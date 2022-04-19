package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.layer.Clip
import dev.fastriver.fluko.common.layer.ClipRectLayer
import dev.fastriver.fluko.common.layer.TransformLayer
import dev.fastriver.fluko.common.math.Matrix4
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.geometrics.Alignment
import dev.fastriver.fluko.framework.geometrics.BoxConstraints
import dev.fastriver.fluko.framework.gesture.HitTestResult
import dev.fastriver.fluko.framework.painting.BoxFit

class RenderFittedBox(
    fit: BoxFit = BoxFit.Contain,
    alignment: Alignment = Alignment.center,
    child: RenderBox? = null,
    clipBehavior: Clip = Clip.None
) : RenderProxyBox() {
    var fit: BoxFit by MarkLayoutProperty(fit)
    var alignment: Alignment by MarkLayoutProperty(alignment)
    var clipBehavior: Clip by MarkPaintProperty(clipBehavior)

    private var hasVisualOverflow: Boolean? = null
    private var transform: Matrix4? = null

    private fun clearPaintData() {
        hasVisualOverflow = null
        transform = null
    }

    private fun updatePaintData() {
        if(transform != null) return
        if(child == null) {
            hasVisualOverflow = false
            transform = Matrix4.identity
        } else {
            val childSize = child!!.size
            val sizes = BoxFit.applyBoxFit(fit, childSize, size)
            val scaleX = sizes.destination.width / sizes.source.width
            val scaleY = sizes.destination.height / sizes.source.height
            val sourceRect = alignment.inscribe(sizes.source, childSize.and(Offset.zero))
            val destinationRect = alignment.inscribe(sizes.destination, size.and(Offset.zero))
            hasVisualOverflow = sourceRect.width < childSize.width || sourceRect.height < childSize.height
            transform = Matrix4.translationValues(sourceRect.left, sourceRect.top, 0f)
                .scale(scaleX.toFloat(), scaleY.toFloat())
                .leftTranslate(destinationRect.left, destinationRect.top, 0f)
        }
    }

    override fun performLayout() {
        child?.let {
            it.layout(BoxConstraints(), parentUsesSize = true)
            when(fit) {
                BoxFit.ScaleDown -> {
                    val sizeConstraints = constraints.loosen()
                    val unconstrainedSize = sizeConstraints.constrainSizeAndAttemptToPreserveAspectRatio(it.size)
                    size = constraints.constrain(unconstrainedSize)
                }
                else -> {
                    size = constraints.constrainSizeAndAttemptToPreserveAspectRatio(it.size)
                }
            }
            clearPaintData()
        } ?: kotlin.run {
            size = constraints.smallest
        }
    }

    private fun paintChildWithTransform(context: PaintingContext, offset: Offset): TransformLayer? {
        val childOffset = transform!!.getAsTranslation()
        if(childOffset == null) {
            return context.pushTransform(
                offset,
                transform!!,
                { c, o -> super.paint(c, o) },
                oldLayer = if(layer is TransformLayer) layer as TransformLayer else null
            )
        } else {
            super.paint(context, offset + childOffset)
        }
        return null
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        if(size.isEmpty || child!!.size.isEmpty) {
            return
        }
        updatePaintData()
        if(hasVisualOverflow!! && clipBehavior != Clip.None) {
            layer = context.pushClipRect(
                offset,
                size.and(Offset.zero),
                { c, o -> paintChildWithTransform(c, o) },
                oldLayer = if(layer is ClipRectLayer) layer as ClipRectLayer else null,
                clipBehavior = clipBehavior
            )
        } else {
            layer = paintChildWithTransform(context, offset)
        }
    }

    override fun hitTestChildren(result: HitTestResult, position: Offset): Boolean {
        if(size.isEmpty || child?.size?.isEmpty == true) {
            return false
        }
        updatePaintData()
        return result.addWithPaintTransform(transform, position, hitTest = { r, p ->
            super.hitTestChildren(r, p)
        })
    }
}