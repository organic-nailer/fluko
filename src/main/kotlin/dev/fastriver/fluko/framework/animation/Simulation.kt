package dev.fastriver.fluko.framework.animation

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

/**
 * 時間を渡したときにどの状態にあるのか、というものを定義するクラス
 */
interface Simulation {
    fun x(time: Double): Double
    fun dx(time: Double): Double
    fun isDone(time: Double): Boolean
}

class InterpolationSimulation(
    private val begin: Double,
    private val end: Double,
    duration: Duration,
    private val curve: Curve
): Simulation {
    private val durationInSeconds: Double = duration.toDouble(DurationUnit.SECONDS)

    override fun x(timeInSeconds: Double): Double {
        val t = (timeInSeconds / durationInSeconds).coerceIn(0.0, 1.0)
        if(t == 0.0) return begin
        if(t == 1.0) return end
        return begin + (end - begin) * curve.transform(t)
    }

    override fun dx(timeInSeconds: Double): Double {
        val epsilon = 1e-4
        return (x(timeInSeconds + epsilon) - x(timeInSeconds - epsilon)) / (2 * epsilon)
    }

    override fun isDone(timeInSeconds: Double): Boolean = timeInSeconds > durationInSeconds
}
