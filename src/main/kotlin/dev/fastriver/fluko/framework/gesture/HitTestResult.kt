package dev.fastriver.fluko.framework.gesture

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.math.Matrix4
import dev.fastriver.fluko.common.math.Vector3

sealed interface TransformPart {
    fun multiply(rightHand: Matrix4): Matrix4
}

class MatrixTransformPart(
    val matrix: Matrix4
) : TransformPart {
    override fun multiply(rightHand: Matrix4): Matrix4 {
        return matrix * rightHand
    }
}

class OffsetTransformPart(
    val offset: Offset
) : TransformPart {
    override fun multiply(rightHand: Matrix4): Matrix4 {
        return rightHand.leftTranslate(offset.dx.toFloat(), offset.dy.toFloat())
    }
}

class HitTestResult {
    val path: List<HitTestEntry>
        get() = pathInternal
    private val pathInternal = mutableListOf<HitTestEntry>()
    private val transforms = mutableListOf(Matrix4.identity)
    private val localTransforms = mutableListOf<TransformPart>()

    private fun globalizeTransforms() {
        if(localTransforms.isEmpty()) return
        var last = transforms.last()
        for(part in localTransforms) {
            last = part.multiply(last) // 左から乗算する
            transforms.add(last)
        }
        localTransforms.clear()
    }

    val lastTransform: Matrix4
        get() {
            globalizeTransforms()
            return transforms.last()
        }

    fun add(entry: HitTestEntry) {
        entry.transform = lastTransform
        pathInternal.add(entry)
    }

    fun pushTransform(transform: Matrix4) {
        localTransforms.add(MatrixTransformPart(transform))
    }

    fun pushOffset(offset: Offset) {
        localTransforms.add(OffsetTransformPart(offset))
    }

    fun popTransform() {
        if(localTransforms.isNotEmpty()) {
            localTransforms.removeLast()
        } else {
            transforms.removeLast()
            assert(transforms.isNotEmpty())
        }
    }

    /**
     * Paint側でTransformがかかる場合の座標変換
     *
     * 逆変換する
     */
    fun addWithPaintTransform(transform: Matrix4?, position: Offset, hitTest: HitTestFunc): Boolean {
        var t: Matrix4? = transform
        if(transform != null) {
            t = transform.removeZTransform().tryInvert() ?: return false
        }
        return addWithRawTransform(
            t, position, hitTest
        )
    }

    /**
     * Paint側でOffsetがはいる場合の座標変換
     *
     * 逆のOffsetを登録する
     */
    fun addWithPaintOffset(offset: Offset?, position: Offset, hitTest: HitTestFunc): Boolean {
        val transformed = if(offset != null) position - offset else position
        offset?.let { pushOffset(-it) }
        val isHit = hitTest(this, transformed)
        offset?.let { popTransform() }
        return isHit
    }

    private fun addWithRawTransform(transform: Matrix4?, position: Offset, hitTest: HitTestFunc): Boolean {
        val calculated =
            transform?.perspectiveTransform(Vector3(listOf(position.dx.toFloat(), position.dy.toFloat(), 0f)))
        val transformed = if(calculated != null) Offset(
            calculated.vector[0].toDouble(),
            calculated.vector[1].toDouble()
        ) else position
        transform?.let {
            pushTransform(it)
        }
        val isHit = hitTest(this, transformed)
        transform?.let { popTransform() }
        return isHit
    }
}

typealias HitTestFunc = (HitTestResult, Offset) -> Boolean
