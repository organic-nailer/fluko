package dev.fastriver.fluko.common

import org.jetbrains.skija.Rect

abstract class OffsetBase(
    private val dx: Double,
    private val dy: Double
) {
    val isInfinite: Boolean
        get() = dx >= Double.POSITIVE_INFINITY || dy >= Double.POSITIVE_INFINITY

    val isFinite: Boolean
        get() = dx.isFinite() && dy.isFinite()
}

class Size(
    val width: Double,
    val height: Double
) : OffsetBase(width, height) {
    companion object {
        val zero = Size(0.0, 0.0)
    }

    val isEmpty = width <= 0.0 || height <= 0.0

    operator fun minus(other: OffsetBase): OffsetBase {
        if(other is Offset) {
            return Size(width - other.dx, height - other.dy)
        }
        if(other is Size) {
            return Offset(width - other.width, height - other.height)
        }
        throw IllegalArgumentException()
    }

    fun and(other: Offset): Rect {
        return Rect.makeXYWH(other.dx.toFloat(),other.dy.toFloat(),width.toFloat(),height.toFloat())
    }
}

class Offset(val dx: Double, val dy: Double): OffsetBase(dx,dy) {
    companion object {
        val zero = Offset(0.0,0.0)
    }

    operator fun plus(other: Offset): Offset {
        return Offset(dx+other.dx,dy+other.dy)
    }
}