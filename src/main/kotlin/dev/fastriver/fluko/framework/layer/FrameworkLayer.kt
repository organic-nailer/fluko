package dev.fastriver.fluko.framework.layer

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.layer.Clip
import dev.fastriver.fluko.common.math.Matrix4
import org.jetbrains.skia.*

abstract class FrameworkLayer {
    var parent: ContainerFrameworkLayer? = null

    var needsAddToScene = true
    protected val alwaysNeedsAddToScene = false
    var engineLayer: UIEngineLayer? = null
        get() = field
        protected set(value) {
            field = value
            if(!alwaysNeedsAddToScene) {
                if(parent?.alwaysNeedsAddToScene == false) {
                    parent!!.markNeedsAddToScene()
                }
            }
        }

    abstract fun addToScene(builder: UISceneBuilder)

    fun addToSceneWithRetainedRendering(builder: UISceneBuilder) {
        if(!needsAddToScene && engineLayer != null) {
            builder.addRetained(engineLayer!!)
            return
        }
        addToScene(builder)
        needsAddToScene = false
    }

    fun markNeedsAddToScene() {

    }

    open fun updateSubtreeNeedsAddToScene() {
        needsAddToScene = needsAddToScene || alwaysNeedsAddToScene
    }

    // AbstractNode
    fun dropChild(child: FrameworkLayer) {
        child.parent = null
    }

    fun adoptChild(child: FrameworkLayer) {
        child.parent = this as ContainerFrameworkLayer
    }

    fun remove() {
        parent?.removeChild(this)
    }

    interface UIEngineLayer

    interface UISceneBuilder {
        fun pushTransform(matrix4: Matrix4): UIEngineLayer
        fun pushOffset(offset: Offset): UIEngineLayer
        fun pushClipRect(
            rect: Rect, clipBehavior: Clip = Clip.AntiAlias
        ): UIEngineLayer

        fun pushClipRRect(
            rRect: RRect, clipBehavior: Clip = Clip.AntiAlias
        ): UIEngineLayer

        fun pushClipPath(
            path: Path, clipBehavior: Clip = Clip.AntiAlias
        ): UIEngineLayer

        fun pushOpacity(alpha: Int, offset: Offset = Offset.zero): UIEngineLayer
        fun pushColorFilter(filter: ColorFilter): UIEngineLayer
        fun pushImageFilter(filter: ImageFilter): UIEngineLayer
        fun pushBackdropFilter(
            filter: ImageFilter, blendMode: BlendMode = BlendMode.SRC_OVER
        ): UIEngineLayer

        fun pushShaderMask(
            shader: Shader, maskRect: Rect, blendMode: BlendMode, filterQuality: FilterQuality = FilterQuality.LOW
        ): UIEngineLayer

        fun pushPhysicalShape(
            path: Path, elevation: Double, color: Color, shadowColor: Color? = null, clipBehavior: Clip = Clip.None
        ): UIEngineLayer

        fun pop()

        fun addRetained(retainedLayer: UIEngineLayer)
        fun addPicture(offset: Offset, picture: Picture)
        fun addTexture(
            textureId: Int,
            offset: Offset = Offset.zero,
            width: Double = 0.0,
            height: Double = 0.0,
            freeze: Boolean = false,
            filterQuality: FilterQuality = FilterQuality.LOW
        )
    }
}