package dev.fastriver.fluko.framework

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.layer.*
import dev.fastriver.fluko.framework.render.RenderObject
import dev.fastriver.fluko.framework.render.RenderView
import org.jetbrains.skija.Canvas
import org.jetbrains.skija.PictureRecorder
import org.jetbrains.skija.Rect

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
            val dirtyNodes = nodesNeedingLayout.toList()
            nodesNeedingLayout.clear()
            // TODO: depth
            for(node in dirtyNodes) {
                if(node.needsLayout && node.owner == this) {
                    node.layoutWithoutResize()
                }
            }
        }
    }

    fun flushPaint() {
       val dirtyNodes = nodesNeedingPaint.toList()
       nodesNeedingPaint.clear()
        for(node in dirtyNodes) {
            if(node.needsPaint && node.owner == this) {
                // TODO: consider if node detached
                PaintingContext.repaintCompositedChild(node)
            }
        }
//        val rootLayer = renderView!!.layer
//        val context = PaintingContext(rootLayer, renderView!!.size.and(Offset.zero))
//        renderView!!.paint(context, Offset.zero)
//        context.stopRecordingIfNeeded()
    }

    fun requestVisualUpdate() {
        onNeedVisualUpdate()
    }
}

class PaintingContext(private val containerLayer: ContainerLayer, private val estimatedBounds: Rect) {
    companion object {
        /**
         * [RenderObject.isRepaintBoundary] == trueのRenderObjectの下位Layerを再構築する
         */
        fun repaintCompositedChild(child: RenderObject) {
            var childLayer = child.layer as OffsetLayer?
            if(childLayer == null) {
                childLayer = OffsetLayer()
                child.layer = childLayer
            }
            else {
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

    private var currentLayer: PictureLayer? = null
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
        currentLayer = PictureLayer(estimatedBounds)
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
        }
        else {
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
        val childOffsetLayer = child.layer as OffsetLayer
        childOffsetLayer.offset = offset
        appendLayer(childOffsetLayer)
    }

    fun appendLayer(layer: Layer) {
        layer.remove()
        containerLayer.append(layer)
    }

    fun pushLayer(
        childLayer: ContainerLayer, painter: PaintingContextCallback, offset: Offset, childPaintBounds: Rect? = null
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
}

typealias PaintingContextCallback = (PaintingContext, Offset) -> Unit
