package dev.fastriver.fluko.framework.geometrics

import dev.fastriver.fluko.common.EdgeInsets
import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.Size
import org.jetbrains.skia.Rect
import kotlin.math.max

enum class Axis {
    Horizontal {
        override fun flip() = Vertical
    },
    Vertical {
        override fun flip(): Axis = Horizontal
    };

    abstract fun flip(): Axis
}

enum class MainAxisAlignment {
    Start, End, Center, SpaceBetween, SpaceAround, SpaceEvenly
}

enum class MainAxisSize { Max, Min }

enum class CrossAxisAlignment { Start, End, Center, Stretch, Baseline }

enum class VerticalDirection { Up, Down }

class BoxConstraints(
    val minWidth: Double = 0.0,
    val maxWidth: Double = Double.POSITIVE_INFINITY,
    val minHeight: Double = 0.0,
    val maxHeight: Double = Double.POSITIVE_INFINITY
) {
    companion object {
        fun tight(size: Size): BoxConstraints {
            return BoxConstraints(size.width, size.width, size.height, size.height)
        }

        fun tightFor(width: Double? = null, height: Double? = null): BoxConstraints {
            return BoxConstraints(
                width ?: 0.0, width ?: Double.POSITIVE_INFINITY, height ?: 0.0, height ?: Double.POSITIVE_INFINITY
            )
        }
    }

    /// 与えられた制約を満たすよう現在の制約を修正する
    fun enforce(constraints: BoxConstraints): BoxConstraints {
        return BoxConstraints(
            minWidth.coerceIn(constraints.minWidth..constraints.maxWidth),
            maxWidth.coerceIn(constraints.minWidth..constraints.maxWidth),
            minHeight.coerceIn(constraints.minHeight..constraints.maxHeight),
            maxHeight.coerceIn(constraints.minHeight..constraints.maxHeight),
        )
    }

    fun constrainWidth(width: Double): Double {
        return width.coerceIn(minWidth..maxWidth)
    }

    fun constrainHeight(height: Double): Double {
        return height.coerceIn(minHeight..maxHeight)
    }

    /**
     * 制約内に修正したサイズを返す
     */
    fun constrain(size: Size): Size {
        return Size(constrainWidth(size.width), constrainHeight(size.height))
    }

    /**
     * 最大大きさのみを残す
     */
    fun loosen() = BoxConstraints(maxWidth = maxWidth, maxHeight = maxHeight)

    /**
     * 現在の制約内で、指定された幅で強い制約にする
     */
    fun tighten(width: Double? = null, height: Double? = null): BoxConstraints {
        return BoxConstraints(
            minWidth = width?.coerceIn(minWidth, maxWidth) ?: minWidth,
            maxWidth = width?.coerceIn(minWidth, maxWidth) ?: maxWidth,
            minHeight = height?.coerceIn(minHeight, maxHeight) ?: minHeight,
            maxHeight = height?.coerceIn(minHeight, maxHeight) ?: maxHeight
        )
    }

    /**
     * edges分だけ周囲を小さくした制約を返す
     */
    fun deflate(edges: EdgeInsets): BoxConstraints {
        val horizontal = edges.left + edges.right
        val vertical = edges.top + edges.bottom
        val deflatedMinWidth = max(0.0, minWidth - horizontal)
        val deflatedMinHeight = max(0.0, minHeight - vertical)
        return BoxConstraints(
            deflatedMinWidth,
            max(deflatedMinWidth, maxWidth - horizontal),
            deflatedMinHeight,
            max(deflatedMinHeight, maxHeight - vertical)
        )
    }

    /**
     * 与えられたsizeの縦横比を保ったまま制約内で最大のサイズを計算する
     */
    fun constrainSizeAndAttemptToPreserveAspectRatio(size: Size): Size {
        if(isTight) {
            return smallest
        }
        var width = size.width
        var height = size.height
        val aspectRatio = size.width / size.height

        // 最大幅を超えていれば縮小する
        if(width > maxWidth) {
            width = maxWidth
            height = width / aspectRatio
        }
        if(height > maxHeight) {
            height = maxHeight
            width = height * aspectRatio
        }

        // 最小幅を下回っていれば拡大する
        if(width < minWidth) {
            width = minWidth
            height = width / aspectRatio
        }
        if(height < minHeight) {
            height = minHeight
            width = height * aspectRatio
        }

        return Size(constrainWidth(width), constrainHeight(height))
    }

    val biggest: Size = Size(constrainWidth(Double.POSITIVE_INFINITY), constrainHeight(Double.POSITIVE_INFINITY))
    val smallest: Size = Size(constrainWidth(0.0), constrainHeight(0.0))

    val hasTightWidth: Boolean = minWidth >= maxWidth
    val hasTightHeight: Boolean = minHeight >= maxHeight
    val isTight: Boolean = hasTightWidth && hasTightHeight
}

class Alignment(val x: Double, val y: Double) {
    companion object {
        val topLeft = Alignment(-1.0, -1.0)
        val topCenter = Alignment(0.0, -1.0)
        val topRight = Alignment(1.0, -1.0)
        val centerLeft = Alignment(-1.0, 0.0)
        val center = Alignment(0.0, 0.0)
        val centerRight = Alignment(1.0, 0.0)
        val bottomLeft = Alignment(-1.0, 1.0)
        val bottomCenter = Alignment(0.0, 1.0)
        val bottomRight = Alignment(1.0, 1.0)
    }

    fun alongOffset(other: Offset): Offset {
        val centerX = other.dx / 2.0
        val centerY = other.dy / 2.0
        return Offset((1 + x) * centerX, (1 + y) * centerY)
    }

    fun computeOffset(parentSize: Size, childSize: Size): Offset {
        val offsetIfCenter = (parentSize - childSize) as Offset
        return alongOffset(offsetIfCenter)
    }

    /**
     * [rect]内に大きさが[size]の矩形を自身のAlignmentで配置したときのRectを返す
     */
    fun inscribe(size: Size, rect: Rect): Rect {
        val halfWidthDelta = (rect.width - size.width) / 2
        val halfHeightDelta = (rect.height - size.height) / 2
        return Rect.makeXYWH(
            (rect.left + halfWidthDelta + x * halfWidthDelta).toFloat(),
            (rect.top + halfHeightDelta + y * halfHeightDelta).toFloat(),
            size.width.toFloat(),
            size.height.toFloat()
        )
    }
}
