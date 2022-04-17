package dev.fastriver.fluko.common

class EdgeInsets(
    val left: Double = 0.0,
    val top: Double = 0.0,
    val right: Double = 0.0,
    val bottom: Double = 0.0
) {
    companion object {
        fun all(value: Double) = EdgeInsets(
            value, value, value, value
        )

        fun symmetric(vertical: Double = 0.0, horizontal: Double = 0.0) = EdgeInsets(
            horizontal, vertical, horizontal, vertical
        )

        val zero: EdgeInsets = all(0.0)
    }
}