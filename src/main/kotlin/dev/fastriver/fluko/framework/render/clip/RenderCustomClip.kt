package dev.fastriver.fluko.framework.render.clip

import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.common.layer.Clip
import dev.fastriver.fluko.framework.render.MarkPaintProperty
import dev.fastriver.fluko.framework.render.RenderBox
import dev.fastriver.fluko.framework.render.RenderProxyBox

abstract class RenderCustomClip<T>(
    child: RenderBox? = null, clipper: CustomClipper<T>? = null, clipBehavior: Clip = Clip.AntiAlias
) : RenderProxyBox() {
    var clipper: CustomClipper<T>? = clipper
        set(newClipper) {
            if(field == newClipper) return
            val oldClipper = field
            field = newClipper
            if(newClipper == null || oldClipper == null || newClipper::class != oldClipper::class || newClipper.shouldReclip(
                    oldClipper
                )
            ) {
                markNeedsClip()
            }
        }
    protected var clip: T? = null
    var clipBehavior: Clip by MarkPaintProperty(clipBehavior)
    protected abstract val defaultClip: T

    protected fun markNeedsClip() {
        clip = null
        markNeedsPaint()
    }

    override fun performLayout() {
        val oldSize = size
        super.performLayout()
        if(oldSize != size) {
            clip = null
        }
    }

    protected fun updateClip() {
        clip = clip ?: clipper?.getClip(size) ?: defaultClip
    }
}

abstract class CustomClipper<T> {
    abstract fun getClip(size: Size): T

    abstract fun shouldReclip(oldClipper: CustomClipper<T>): Boolean
}
