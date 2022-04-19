package dev.fastriver.fluko.common.math

import dev.fastriver.fluko.common.Offset
import org.jetbrains.skia.Matrix44
import kotlin.math.abs


/**
 * 4x4の3次元座標変換行列
 *
 * 右手系(xは右方向、yは下方向、zは画面の奥方向)
 *
 * @param mat
 * 行列を展開したもの。右上がmat\[3\]で左下がmat\[12\]
 */
class Matrix4(
    private val mat: List<Float>
) {
    companion object {
        val identity = Matrix4(
            listOf(
                1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f
            )
        )
        val zero = Matrix4(
            listOf(
                0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f
            )
        )

        fun translationValues(x: Float, y: Float, z: Float) = identity.put(
            mapOf(
                3 to x, 7 to y, 11 to z
            )
        )

        fun scaleValues(sx: Float, sy: Float, sz: Float) = identity.put(
            mapOf(
                0 to sx, 5 to sy, 10 to sz
            )
        )
    }

    init {
        assert(mat.size == 16)
    }

    fun put(data: Map<Int, Float>): Matrix4 {
        val mutable = mat.toMutableList()
        for(datum in data) {
            mutable[datum.key] = datum.value
        }
        return Matrix4(mutable)
    }

    fun toMatrix44(): Matrix44 {
        return Matrix44(
            *mat.toFloatArray()
        )
    }

    /**
     * 平行移動する
     *
     * <pre>
     * 1 0 0 x
     * 0 1 0 y
     * 0 0 1 z
     * 0 0 0 1
     * </pre>
     */
    fun leftTranslate(x: Float, y: Float = 0f, z: Float = 0f): Matrix4 {
        val translator = translationValues(x, y, z)
        return translator * this
    }

    fun scale(x: Float, y: Float? = null, z: Float? = null): Matrix4 {
        val scale = scaleValues(x, y ?: x, z ?: x)
        return scale * this
    }

    fun perspectiveTransform(rightHand: Vector3): Vector3 {
        return (this * rightHand.expandToVector4()).contractToVector3()
    }

    /**
     * Z方向の変換を無効にする
     *
     * Z方向の変換はHitTestでは考慮しない
     *
     * x x 0 x
     * x x 0 x
     * 0 0 1 0
     * x x 0 x
     */
    fun removeZTransform(): Matrix4 {
        return this.put(
            mapOf(
                2 to 0f, 6 to 0f, 8 to 0f, 9 to 0f, 10 to 1f, 11 to 0f, 14 to 0f
            )
        )
    }

    val isTranslation: Boolean =
        mat[0] == 1f && mat[1] == 0f && mat[2] == 0f && mat[4] == 0f && mat[5] == 1f && mat[6] == 0f && mat[8] == 0f && mat[9] == 0f && mat[10] == 1f && mat[11] == 0f && mat[12] == 0f && mat[13] == 0f && mat[14] == 0f && mat[15] == 1f

    fun getAsTranslation(): Offset? = if(isTranslation) Offset(mat[3].toDouble(), mat[7].toDouble()) else null

    /**
     * 逆行列を計算する
     *
     * https://risalc.info/src/inverse-cofactor-ex4.html
     */
    fun tryInvert(): Matrix4? {
        val argStorage = mat
        val a00 = argStorage[0]
        val a01 = argStorage[1]
        val a02 = argStorage[2]
        val a03 = argStorage[3]
        val a10 = argStorage[4]
        val a11 = argStorage[5]
        val a12 = argStorage[6]
        val a13 = argStorage[7]
        val a20 = argStorage[8]
        val a21 = argStorage[9]
        val a22 = argStorage[10]
        val a23 = argStorage[11]
        val a30 = argStorage[12]
        val a31 = argStorage[13]
        val a32 = argStorage[14]
        val a33 = argStorage[15]
        val b00 = a00 * a11 - a01 * a10
        val b01 = a00 * a12 - a02 * a10
        val b02 = a00 * a13 - a03 * a10
        val b03 = a01 * a12 - a02 * a11
        val b04 = a01 * a13 - a03 * a11
        val b05 = a02 * a13 - a03 * a12
        val b06 = a20 * a31 - a21 * a30
        val b07 = a20 * a32 - a22 * a30
        val b08 = a20 * a33 - a23 * a30
        val b09 = a21 * a32 - a22 * a31
        val b10 = a21 * a33 - a23 * a31
        val b11 = a22 * a33 - a23 * a32
        val det = b00 * b11 - b01 * b10 + b02 * b09 + b03 * b08 - b04 * b07 + b05 * b06
        if(abs(det) <= 0.001f) {
            return null
        }
        val invDet = 1f / det
        return Matrix4(
            listOf(
                (a11 * a22 * a33 + a12 * a23 * a31 + a13 * a21 * a32 - a13 * a22 * a31 - a12 * a21 * a33 - a11 * a23 * a32) * invDet, // 00
                -(a01 * a22 * a33 + a02 * a23 * a31 + a03 * a21 * a32 - a03 * a22 * a31 - a02 * a21 * a33 - a01 * a23 * a32) * invDet, // 01
                (a01 * a12 * a33 + a02 * a13 * a31 + a03 * a11 * a32 - a03 * a12 * a31 - a02 * a11 * a33 - a01 * a13 * a32) * invDet, // 02
                -(a01 * a12 * a23 + a02 * a13 * a21 + a03 * a11 * a22 - a03 * a12 * a21 - a02 * a11 * a23 - a01 * a13 * a22) * invDet, // 03
                -(a10 * a22 * a33 + a12 * a23 * a30 + a13 * a20 * a32 - a13 * a22 * a30 - a12 * a20 * a33 - a10 * a23 * a32) * invDet, // 10
                (a00 * a22 * a33 + a02 * a23 * a30 + a03 * a20 * a32 - a03 * a22 * a30 - a02 * a20 * a33 - a00 * a23 * a32) * invDet, // 11
                -(a00 * a12 * a33 + a02 * a13 * a30 + a03 * a10 * a32 - a03 * a12 * a30 - a02 * a10 * a33 - a00 * a13 * a32) * invDet, // 12
                (a00 * a12 * a23 + a02 * a13 * a20 + a03 * a10 * a22 - a03 * a12 * a20 - a02 * a10 * a23 - a00 * a13 * a22) * invDet, // 13
                (a10 * a21 * a33 + a11 * a23 * a30 + a13 * a20 * a31 - a13 * a21 * a30 - a11 * a20 * a33 - a10 * a23 * a31) * invDet, // 20
                -(a00 * a21 * a33 + a01 * a23 * a30 + a03 * a20 * a31 - a03 * a21 * a30 - a01 * a20 * a33 - a00 * a23 * a31) * invDet, // 21
                (a00 * a11 * a33 + a01 * a13 * a30 + a03 * a10 * a31 - a03 * a11 * a30 - a01 * a10 * a33 - a00 * a13 * a31) * invDet, // 22
                -(a00 * a11 * a23 + a01 * a13 * a20 + a03 * a10 * a21 - a03 * a11 * a20 - a01 * a10 * a23 - a00 * a13 * a21) * invDet, // 23
                -(a10 * a21 * a32 + a11 * a22 * a30 + a12 * a20 * a31 - a12 * a21 * a30 - a11 * a20 * a32 - a10 * a22 * a31) * invDet, // 30
                (a00 * a21 * a32 + a01 * a22 * a30 + a02 * a20 * a31 - a02 * a21 * a30 - a01 * a20 * a32 - a00 * a22 * a31) * invDet, // 31
                -(a00 * a11 * a32 + a01 * a12 * a30 + a02 * a10 * a31 - a02 * a11 * a30 - a01 * a10 * a32 - a00 * a12 * a31) * invDet, // 32
                (a00 * a11 * a22 + a01 * a12 * a20 + a02 * a10 * a21 - a02 * a11 * a20 - a01 * a10 * a22 - a00 * a12 * a21) * invDet, // 33
            )
        )
    }

//    fun rotate() {
//        Matrix44
//    }

    operator fun times(rightHand: Matrix4): Matrix4 {
        val m00 = mat[0]
        val m01 = mat[1]
        val m02 = mat[2]
        val m03 = mat[3]
        val m10 = mat[4]
        val m11 = mat[5]
        val m12 = mat[6]
        val m13 = mat[7]
        val m20 = mat[8]
        val m21 = mat[9]
        val m22 = mat[10]
        val m23 = mat[11]
        val m30 = mat[12]
        val m31 = mat[13]
        val m32 = mat[14]
        val m33 = mat[15]
        val argStorage = rightHand.mat
        val n00 = argStorage[0]
        val n01 = argStorage[1]
        val n02 = argStorage[2]
        val n03 = argStorage[3]
        val n10 = argStorage[4]
        val n11 = argStorage[5]
        val n12 = argStorage[6]
        val n13 = argStorage[7]
        val n20 = argStorage[8]
        val n21 = argStorage[9]
        val n22 = argStorage[10]
        val n23 = argStorage[11]
        val n30 = argStorage[12]
        val n31 = argStorage[13]
        val n32 = argStorage[14]
        val n33 = argStorage[15]
        return Matrix4(
            listOf(
                (m00 * n00) + (m01 * n10) + (m02 * n20) + (m03 * n30),
                (m00 * n01) + (m01 * n11) + (m02 * n21) + (m03 * n31),
                (m00 * n02) + (m01 * n12) + (m02 * n22) + (m03 * n32),
                (m00 * n03) + (m01 * n13) + (m02 * n23) + (m03 * n33),
                (m10 * n00) + (m11 * n10) + (m12 * n20) + (m13 * n30),
                (m10 * n01) + (m11 * n11) + (m12 * n21) + (m13 * n31),
                (m10 * n02) + (m11 * n12) + (m12 * n22) + (m13 * n32),
                (m10 * n03) + (m11 * n13) + (m12 * n23) + (m13 * n33),
                (m20 * n00) + (m21 * n10) + (m22 * n20) + (m23 * n30),
                (m20 * n01) + (m21 * n11) + (m22 * n21) + (m23 * n31),
                (m20 * n02) + (m21 * n12) + (m22 * n22) + (m23 * n32),
                (m20 * n03) + (m21 * n13) + (m22 * n23) + (m23 * n33),
                (m30 * n00) + (m31 * n10) + (m32 * n20) + (m33 * n30),
                (m30 * n01) + (m31 * n11) + (m32 * n21) + (m33 * n31),
                (m30 * n02) + (m31 * n12) + (m32 * n22) + (m33 * n32),
                (m30 * n03) + (m31 * n13) + (m32 * n23) + (m33 * n33)
            )
        )
    }

    operator fun times(rightHand: Vector4): Vector4 {
        val m00 = mat[0]
        val m01 = mat[1]
        val m02 = mat[2]
        val m03 = mat[3]
        val m10 = mat[4]
        val m11 = mat[5]
        val m12 = mat[6]
        val m13 = mat[7]
        val m20 = mat[8]
        val m21 = mat[9]
        val m22 = mat[10]
        val m23 = mat[11]
        val m30 = mat[12]
        val m31 = mat[13]
        val m32 = mat[14]
        val m33 = mat[15]
        val argStorage = rightHand.vector
        val n0 = argStorage[0]
        val n1 = argStorage[1]
        val n2 = argStorage[2]
        val n3 = argStorage[3]
        return Vector4(
            listOf(
                m00 * n0 + m01 * n1 + m02 * n2 + m03 * n3,
                m10 * n0 + m11 * n1 + m12 * n2 + m13 * n3,
                m20 * n0 + m21 * n1 + m22 * n2 + m23 * n3,
                m30 * n0 + m31 * n1 + m32 * n2 + m33 * n3
            )
        )
    }
}