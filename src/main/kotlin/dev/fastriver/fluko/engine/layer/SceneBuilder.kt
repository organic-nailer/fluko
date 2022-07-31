package dev.fastriver.fluko.engine.layer

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.layer.Clip
import dev.fastriver.fluko.common.math.Matrix4
import dev.fastriver.fluko.framework.layer.FrameworkLayer
import org.jetbrains.skia.*

class SceneBuilder : FrameworkLayer.UISceneBuilder {
    private val layerStack = ArrayDeque<ContainerEngineLayer>()

    init {
        pushLayer(ContainerEngineLayer())
    }

    private fun pushLayer(layer: ContainerEngineLayer) {
        addLayer(layer)
        layerStack.addLast(layer)
    }

    private fun addLayer(layer: EngineLayer) {
        if(layerStack.isNotEmpty()) {
            layerStack.last().add(layer)
        }
    }

    private fun popLayer() {
        if(layerStack.isNotEmpty()) {
            layerStack.removeLast()
        }
    }

    /**
     * oldLayerの紐づけは未実装
     */
    override fun pushTransform(matrix4: Matrix4): FrameworkLayer.UIEngineLayer {
        val layer = TransformEngineLayer(matrix4.toMatrix44().asMatrix33())
        pushLayer(layer)
        return layer
    }

    override fun pushOffset(offset: Offset): FrameworkLayer.UIEngineLayer {
        val matrix = Matrix4.translationValues(offset.dx.toFloat(), offset.dy.toFloat(), 0f)
        val layer = TransformEngineLayer(matrix.toMatrix44().asMatrix33())
        pushLayer(layer)
        return layer
    }

    override fun pushClipRect(
        rect: Rect, clipBehavior: Clip
    ): FrameworkLayer.UIEngineLayer {
        val layer = ClipRectEngineLayer(rect, clipBehavior)
        pushLayer(layer)
        return layer
    }

    override fun pushClipRRect(rRect: RRect, clipBehavior: Clip): FrameworkLayer.UIEngineLayer {
        val layer = ClipRRectEngineLayer(rRect, clipBehavior)
        pushLayer(layer)
        return layer
    }

    override fun pushClipPath(path: Path, clipBehavior: Clip): FrameworkLayer.UIEngineLayer {
        val layer = ClipPathEngineLayer(path, clipBehavior)
        pushLayer(layer)
        return layer
    }

    override fun pushOpacity(alpha: Int, offset: Offset): FrameworkLayer.UIEngineLayer {
        val layer = OpacityEngineLayer(alpha, offset)
        pushLayer(layer)
        return layer
    }

    override fun pushColorFilter(filter: ColorFilter): FrameworkLayer.UIEngineLayer {
        TODO("Not yet implemented")
    }

    override fun pushImageFilter(filter: ImageFilter): FrameworkLayer.UIEngineLayer {
        TODO("Not yet implemented")
    }

    override fun pushBackdropFilter(filter: ImageFilter, blendMode: BlendMode): FrameworkLayer.UIEngineLayer {
        TODO("Not yet implemented")
    }

    override fun pushShaderMask(
        shader: Shader, maskRect: Rect, blendMode: BlendMode, filterQuality: FilterQuality
    ): FrameworkLayer.UIEngineLayer {
        TODO("Not yet implemented")
    }

    override fun pushPhysicalShape(
        path: Path, elevation: Double, color: Color, shadowColor: Color?, clipBehavior: Clip
    ): FrameworkLayer.UIEngineLayer {
        TODO("Not yet implemented")
    }

    override fun pop() {
        popLayer()
    }

    override fun addRetained(retainedLayer: FrameworkLayer.UIEngineLayer) {
        addLayer(retainedLayer as EngineLayer)
    }

    override fun addPicture(offset: Offset, picture: Picture) {
        val layer = PictureEngineLayer(offset, picture)
        addLayer(layer)
    }

    override fun addTexture(
        textureId: Int, offset: Offset, width: Double, height: Double, freeze: Boolean, filterQuality: FilterQuality
    ) {
        TODO("Not yet implemented")
    }

    fun build(): LayerTree {
        val layerTree = LayerTree().apply {
            rootLayer = layerStack.first()
        }
        layerStack.clear()
        return layerTree
    }
}