package dev.fastriver.fluko.framework

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.layer.Clip
import dev.fastriver.fluko.common.makeOffset
import dev.fastriver.fluko.common.math.Matrix4
import dev.fastriver.fluko.framework.layer.*
import dev.fastriver.fluko.framework.render.RenderObject
import dev.fastriver.fluko.framework.render.RenderView
import org.jetbrains.skia.*

class RenderPipeline(
    private val onNeedVisualUpdate: () -> Unit
) {
    var renderView: RenderView? = null
        set(value) {
            // TODO: detach
            value?.attach(this)
            field = value
        }

    /**
     * flushPaint()でpaintする必要のあるRenderObjectたち
     *
     * [RenderObject.markNeedsPaint]の通り、[RenderObject.isRepaintBoundary] == trueの
     * もの([RenderView], [RenderRepaintBoundary] ...)のみが追加される
     */
    val nodesNeedingPaint: MutableList<RenderObject> = mutableListOf()

    val nodesNeedingLayout: MutableList<RenderObject> = mutableListOf()

    fun flushLayout() {
        while(nodesNeedingLayout.isNotEmpty()) {
            // ツリーの上の方を先にやる
            val dirtyNodes = nodesNeedingLayout.sortedBy { it.depth }
            nodesNeedingLayout.clear()
            for(node in dirtyNodes) {
                if(node.needsLayout && node.owner == this) {
                    node.layoutWithoutResize()
                }
            }
        }
    }

    fun flushPaint() {
        val dirtyNodes = nodesNeedingPaint.sortedBy { it.depth }
        nodesNeedingPaint.clear()
        for(node in dirtyNodes) {
            if(node.needsPaint && node.owner == this) {
                // TODO: consider if node detached
                PaintingContext.repaintCompositedChild(node)
            }
        }
    }

    fun requestVisualUpdate() {
        onNeedVisualUpdate()
    }
}

class PaintingContext(private val containerLayer: ContainerFrameworkLayer, private val estimatedBounds: Rect) {
    companion object {
        /**
         * [RenderObject.isRepaintBoundary] == trueのRenderObjectの下位Layerを再構築する
         */
        fun repaintCompositedChild(child: RenderObject) {
            var childLayer = child.layer as OffsetFrameworkLayer?
            if(childLayer == null) {
                childLayer = OffsetFrameworkLayer()
                child.layer = childLayer
            } else {
                childLayer.removeAllChildren()
            }
            val childContext = PaintingContext(childLayer, child.size.and(Offset.zero))

            // paintWithContext
            child.needsPaint = false
            child.paint(childContext, Offset.zero)
            // end paintWithContext
            childContext.stopRecordingIfNeeded()
        }
    }

    private var currentLayer: PictureFrameworkLayer? = null
    private var recorder: PictureRecorder? = null
    private var _canvas: Canvas? = null
    private val isRecording: Boolean
        get() = _canvas != null

    val canvas: Canvas
        get() {
            if(_canvas == null) startRecording()
            return _canvas!!
        }

    /// PictureLayerでの描画の録画を開始する
    private fun startRecording() {
        currentLayer = PictureFrameworkLayer(estimatedBounds)
        recorder = PictureRecorder()
        _canvas = recorder!!.beginRecording(estimatedBounds)
        containerLayer.append(currentLayer!!)
    }


    fun stopRecordingIfNeeded() {
        if(!isRecording) return

        currentLayer!!.picture = recorder!!.finishRecordingAsPicture()
        currentLayer = null
        recorder = null
        _canvas = null
    }

    /**
     * 渡されたRenderObjectのpaint()を呼ぶ
     *
     * [RenderObject.isRepaintBoundary] == trueの場合、別で処理するため[compositeChild]を呼ぶ
     */
    fun paintChild(child: RenderObject, offset: Offset) {
        if(child.isRepaintBoundary) {
            stopRecordingIfNeeded()
            compositeChild(child, offset)
        } else {
            child.needsPaint = false
            child.paint(this, offset)
        }
    }

    /**
     * [RenderObject.isRepaintBoundary] == trueのものをpaintする
     *
     * [RenderObject.needsPaint]が立っていない場合は前のものを再利用する
     */
    fun compositeChild(child: RenderObject, offset: Offset) {
        if(child.needsPaint) {
            repaintCompositedChild(child)
        }
        val childOffsetLayer = child.layer as OffsetFrameworkLayer
        childOffsetLayer.offset = offset
        appendLayer(childOffsetLayer)
    }

    fun appendLayer(layer: FrameworkLayer) {
        layer.remove()
        containerLayer.append(layer)
    }

    fun pushLayer(
        childLayer: ContainerFrameworkLayer,
        painter: PaintingContextCallback,
        offset: Offset,
        childPaintBounds: Rect? = null
    ) {
        if(childLayer.children.isNotEmpty()) {
            childLayer.removeAllChildren()
        }

        // 新しいレイヤーを作るときは現在のPictureLayerを終了する
        stopRecordingIfNeeded()
        containerLayer.append(childLayer)

        // 新しいPaintingContextで再帰的に動作させる
        val childContext = PaintingContext(childLayer, childPaintBounds ?: estimatedBounds)
        painter(childContext, offset)
        childContext.stopRecordingIfNeeded()
    }

    fun pushOpacity(
        offset: Offset, alpha: Int, painter: PaintingContextCallback, oldLayer: OpacityFrameworkLayer? = null
    ): OpacityFrameworkLayer {
        val layer = oldLayer ?: OpacityFrameworkLayer()
        layer.let {
            it.alpha = alpha
            it.offset = offset
        }
        pushLayer(layer, painter, Offset.zero)
        return layer
    }

    fun pushClipPath(
        offset: Offset,
        bounds: Rect,
        clipPath: Path,
        painter: PaintingContextCallback,
        clipBehavior: Clip = Clip.AntiAlias,
        oldLayer: ClipPathFrameworkLayer? = null
    ): ClipPathFrameworkLayer {
        val offsetBounds = bounds.offset(offset.dx.toFloat(), offset.dy.toFloat())
        val offsetClipPath = clipPath.offset(offset.dx.toFloat(), offset.dy.toFloat())
        val layer = oldLayer ?: ClipPathFrameworkLayer(offsetClipPath)
        layer.let {
            it.clipPath = offsetClipPath
            it.clipBehavior = clipBehavior
        }
        pushLayer(layer, painter, offset, childPaintBounds = offsetBounds)
        return layer
    }

    fun pushClipRRect(
        offset: Offset,
        bounds: Rect,
        clipRRect: RRect,
        painter: PaintingContextCallback,
        clipBehavior: Clip = Clip.AntiAlias,
        oldLayer: ClipRRectFrameworkLayer? = null
    ): ClipRRectFrameworkLayer {
        val offsetBounds = bounds.offset(offset.dx.toFloat(), offset.dy.toFloat())
        val offsetClipRRect = clipRRect.makeOffset(offset)
        val layer = oldLayer ?: ClipRRectFrameworkLayer(offsetClipRRect)
        layer.let {
            it.clipRRect = offsetClipRRect
            it.clipBehavior = clipBehavior
        }
        pushLayer(layer, painter, offset, childPaintBounds = offsetBounds)
        return layer
    }

    fun pushClipRect(
        offset: Offset,
        clipRect: Rect,
        painter: PaintingContextCallback,
        clipBehavior: Clip = Clip.AntiAlias,
        oldLayer: ClipRectFrameworkLayer? = null
    ): ClipRectFrameworkLayer {
        val offsetClipRect = clipRect.makeOffset(offset)
        val layer = oldLayer ?: ClipRectFrameworkLayer(offsetClipRect)
        layer.let {
            it.clipRect = offsetClipRect
            it.clipBehavior = clipBehavior
        }
        pushLayer(layer, painter, offset, childPaintBounds = offsetClipRect)
        return layer
    }

    fun pushTransform(
        offset: Offset, transform: Matrix4, painter: PaintingContextCallback, oldLayer: TransformFrameworkLayer? = null
    ): TransformFrameworkLayer? {
        canvas.save()
        canvas.translate(offset.dx.toFloat(), offset.dy.toFloat())
        canvas.concat(transform.toMatrix44())
        canvas.translate(-offset.dx.toFloat(), -offset.dy.toFloat())
        painter(this, offset)
        canvas.restore()
        return null
    }
}

typealias PaintingContextCallback = (PaintingContext, Offset) -> Unit
