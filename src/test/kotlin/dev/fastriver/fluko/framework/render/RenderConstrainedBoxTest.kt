package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.framework.geometrics.BoxConstraints
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class RenderConstrainedBoxTest {

    @org.junit.jupiter.api.Test
    fun layoutTightWithoutChild() {
        var renderSized = RenderConstrainedBox(
            additionalConstraints = BoxConstraints.tightFor(100.0, 200.0)
        )
        renderSized.layout(BoxConstraints(maxWidth = 300.0, maxHeight = 400.0))
        assertEquals(100.0, renderSized.size.width)
        assertEquals(200.0, renderSized.size.height)

        renderSized = RenderConstrainedBox(
            additionalConstraints = BoxConstraints.tightFor(100.0, 200.0)
        )
        renderSized.layout(BoxConstraints(maxWidth = 50.0, maxHeight = 150.0))
        assertEquals(50.0, renderSized.size.width)
        assertEquals(150.0, renderSized.size.height)
    }

    @Test
    fun layoutTightWithChild() {
        /**
         * 子は自身のサイズに関係なく親のサイズに強制される
         */
        var renderSized = RenderConstrainedBox(
            additionalConstraints = BoxConstraints.tightFor(100.0, 200.0)
        ).apply {
            child = RenderConstrainedBox(BoxConstraints.tightFor(200.0, 100.0))
        }
        renderSized.layout(BoxConstraints(maxWidth = 300.0, maxHeight = 400.0))
        assertEquals(100.0, renderSized.size.width)
        assertEquals(200.0, renderSized.size.height)
        assertEquals(100.0, renderSized.child!!.size.width)
        assertEquals(200.0, renderSized.child!!.size.height)
    }
}