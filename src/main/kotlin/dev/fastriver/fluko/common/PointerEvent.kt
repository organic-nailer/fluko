package dev.fastriver.fluko.common

import dev.fastriver.fluko.common.math.Matrix4
import dev.fastriver.fluko.common.math.Vector3

enum class PointerEventPhase {
    Cancel, Up, Down, Move, Add, Remove, Hover
}

data class PointerEvent(
    val pointerId: Int, val phase: PointerEventPhase, val x: Double, val y: Double
) {
    val position = Offset(x, y)

    var transform: Matrix4? = null
    val localPosition: Offset
        get() {
            if(transform == null) return position
            val position3 = Vector3(listOf(position.dx.toFloat(), position.dy.toFloat(), 0f))
            val transformed3 = transform!!.perspectiveTransform(position3)
            return Offset(transformed3.vector[0].toDouble(), transformed3.vector[1].toDouble())
        }

}