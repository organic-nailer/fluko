package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.RenderPipeline
import dev.fastriver.fluko.framework.geometrics.BoxConstraints
import dev.fastriver.fluko.framework.gesture.HitTestResult
import org.jetbrains.skija.Canvas
import org.jetbrains.skija.FontMgr
import org.jetbrains.skija.paragraph.FontCollection
import org.jetbrains.skija.paragraph.Paragraph
import org.jetbrains.skija.paragraph.ParagraphBuilder
import org.jetbrains.skija.paragraph.ParagraphStyle
import kotlin.math.ceil

class RenderParagraph(
    text: TextSpan
) : RenderBox(), ContainerRenderObject<RenderBox> {
    private val textPainter = TextPainter(text)
    var text: TextSpan
        get() = textPainter.text
        set(value) {
            if(textPainter.text == value) return
            textPainter.text = value
            markNeedsLayout()
            // TODO: 実際はもっと色々ある
        }

    override val thisRef: RenderObject = this
    override val children: MutableList<RenderBox> = mutableListOf()

    override fun performLayout() {
        textPainter.layout(
            minWidth = constraints.minWidth, maxWidth = constraints.maxWidth
        )

        val textSize = textPainter.size
        size = constraints.constrain(textSize)

        // overflow計算
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        textPainter.paint(context.canvas, offset)
    }

    override fun attach(owner: RenderPipeline) {
        super.attach(owner)
        attachChildren(owner)
    }

    override fun detach() {
        super.detach()
        detachChildren()
    }

    override fun visitChildren(visitor: RenderObjectVisitor) {
        super<ContainerRenderObject>.visitChildren(visitor)
    }

    override fun redepthChildren() {
        super<ContainerRenderObject>.redepthChildren { redepthChild(it) }
    }

    override fun hitTestSelf(position: Offset): Boolean = true

    override fun hitTestChildren(result: HitTestResult, position: Offset): Boolean {
        // spanがHitTestTargetの子を持つ場合や、childrenを持つ場合はそれらを処理する必要がある
        return false
    }
}

class TextSpan(
    val text: String
) {
    fun build(builder: ParagraphBuilder) {
        builder.addText(text)
    }
}

class TextPainter(
    text: TextSpan
) {
    var text: TextSpan = text
        set(value) {
            if(field.text == value.text) return
            field = value
            markNeedsLayout()
        }
    private var paragraph: Paragraph? = null
    private var lastMinWidth: Double? = null
    private var lastMaxWidth: Double? = null
    val width: Double get() = paragraph!!.maxWidth.toDouble()
    val height: Double get() = paragraph!!.height.toDouble()
    val size: Size get() = Size(width, height)

    private fun markNeedsLayout() {
        paragraph = null
    }

    private fun createParagraphStyle(): ParagraphStyle {
        return ParagraphStyle().apply {
            textStyle = textStyle.apply {
                color = 0xFF000000.toInt()
                fontSize = 30f
            }
        }
    }

    private fun createParagraph() {
        val builder = ParagraphBuilder(
            createParagraphStyle(),
            FontCollection().apply { setDefaultFontManager(FontMgr.getDefault()) })
        text.build(builder)
        paragraph = builder.build()
    }

    private fun layoutParagraph(minWidth: Double, maxWidth: Double) {
        paragraph!!.layout(maxWidth.toFloat())
        if(minWidth != maxWidth) {
            var newWidth = ceil(paragraph!!.maxIntrinsicWidth)
            newWidth = newWidth.coerceIn(minWidth.toFloat(), maxWidth.toFloat())
            if(newWidth != ceil(paragraph!!.maxWidth)) {
                paragraph!!.layout(newWidth)
            }
        }
    }

    fun layout(minWidth: Double = 0.0, maxWidth: Double = Double.POSITIVE_INFINITY) {
        lastMinWidth = minWidth
        lastMaxWidth = maxWidth
        if(paragraph == null) {
            createParagraph()
        }
        layoutParagraph(minWidth, maxWidth)
    }

    fun paint(canvas: Canvas, offset: Offset) {
        paragraph!!.paint(canvas, offset.dx.toFloat(), offset.dy.toFloat())
    }
}
