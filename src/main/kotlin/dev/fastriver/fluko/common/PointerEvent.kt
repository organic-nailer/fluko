package dev.fastriver.fluko.common

enum class PointerEventPhase {
    Cancel, Up, Down, Move, Add, Remove, Hover
}

data class PointerEvent(
    val pointerId: Int,
    val phase: PointerEventPhase, val x: Double, val y: Double
)