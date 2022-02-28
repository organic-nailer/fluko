import org.jetbrains.skija.Canvas
import org.jetbrains.skija.PictureRecorder
import org.jetbrains.skija.Rect

class RenderPipeline {
    var renderView: RenderView? = null
    fun flushLayout() {
        renderView!!.performLayout()
    }

    fun flushPaint() {
        val rootLayer = renderView!!.layer
        val context = PaintingContext(rootLayer, renderView!!.size.and(Offset.zero))
        renderView!!.paint(context, Offset.zero)
        context.stopRecordingIfNeeded()
    }
}

class PaintingContext(private val containerLayer: ContainerLayer, private val estimatedBounds: Rect) {
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
        containerLayer.children.add(currentLayer!!)
    }


    fun stopRecordingIfNeeded() {
        if(!isRecording) return

        currentLayer!!.picture = recorder!!.finishRecordingAsPicture()
        currentLayer = null
        recorder = null
        _canvas = null
    }


    fun pushLayer(
        childLayer: ContainerLayer, painter: PaintingContextCallback, offset: Offset, childPaintBounds: Rect? = null
    ) {
        if(childLayer.children.isNotEmpty()) {
            childLayer.children.clear()
        }

        // 新しいレイヤーを作るときは現在のPictureLayerを終了する
        stopRecordingIfNeeded()
        containerLayer.children.add(childLayer)

        // 新しいPaintingContextで再帰的に動作させる
        val childContext = PaintingContext(childLayer, childPaintBounds ?: estimatedBounds)
        painter(childContext, offset)
        childContext.stopRecordingIfNeeded()
    }
}

typealias PaintingContextCallback = (PaintingContext, Offset) -> Unit
