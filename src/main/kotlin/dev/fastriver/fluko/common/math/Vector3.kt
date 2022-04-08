package dev.fastriver.fluko.common.math

class Vector3(
    val vector: List<Float>
) {
    companion object {
        val zero = Vector3(listOf(0f, 0f, 0f))
    }

    init {
        assert(vector.size == 3)
    }

    fun expandToVector4(): Vector4 {
        return Vector4(
            listOf(
                vector[0], vector[1], vector[2], 1f
            )
        )
    }
}

class Vector4(
    val vector: List<Float>
) {
    companion object {
        val zero = Vector4(listOf(0f, 0f, 0f, 0f))
    }

    init {
        assert(vector.size == 3)
    }

    fun contractToVector3(): Vector3 {
        return Vector3(
            listOf(
                vector[0] / vector[3], vector[1] / vector[3], vector[2] / vector[3]
            )
        )
    }
}