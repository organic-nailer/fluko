package dev.fastriver.fluko.framework.geometrics

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.Size

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

    /// 制約内に修正したサイズを返す
    fun constrain(size: Size): Size {
        return Size(constrainWidth(size.width), constrainHeight(size.height))
    }

    /// 最大大きさのみ指定
    fun loosen() = BoxConstraints(maxWidth = maxWidth, maxHeight = maxHeight)

    val smallest: Size = Size(constrainWidth(0.0), constrainHeight(0.0))
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

    private fun alongOffset(other: Offset): Offset {
        val centerX = other.dx / 2.0
        val centerY = other.dy / 2.0
        return Offset((1 + x) * centerX, (1 + y) * centerY)
    }

    fun computeOffset(parentSize: Size, childSize: Size): Offset {
        val offsetIfCenter = (parentSize - childSize) as Offset
        return alongOffset(offsetIfCenter)
    }
}
