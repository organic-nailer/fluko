package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.framework.geometrics.Alignment
import dev.fastriver.fluko.framework.geometrics.BoxConstraints
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class RenderPositionedBoxTest {
    private fun assertAlign(alignment: Alignment, size: Size, childSize: Size, expectedOffset: Offset) {
        val box = RenderPositionedBox(alignment = alignment).apply {
            child = RenderConstrainedBox(BoxConstraints.tight(childSize))
        }
        box.layout(BoxConstraints.tight(size))
        val childOffset = (box.child!!.parentData as BoxParentData).offset
        assertEquals(expectedOffset.dx, childOffset.dx)
        assertEquals(expectedOffset.dy, childOffset.dy)
    }

    @Test
    fun layoutTopLeft() {
        assertAlign(
            Alignment.topLeft, Size(300.0, 300.0), Size(100.0, 100.0), Offset.zero
        )
    }


    @Test
    fun layoutTopCenter() {
        assertAlign(
            Alignment.topCenter, Size(300.0, 300.0), Size(100.0, 100.0), Offset(100.0, 0.0)
        )
    }


    @Test
    fun layoutTopRight() {
        assertAlign(
            Alignment.topRight, Size(300.0, 300.0), Size(100.0, 100.0), Offset(200.0, 0.0)
        )
    }


    @Test
    fun layoutCenterLeft() {
        assertAlign(
            Alignment.centerLeft, Size(300.0, 300.0), Size(100.0, 100.0), Offset(0.0, 100.0)
        )
    }


    @Test
    fun layoutCenter() {
        assertAlign(
            Alignment.center, Size(300.0, 300.0), Size(100.0, 100.0), Offset(100.0, 100.0)
        )
    }


    @Test
    fun layoutCenterRight() {
        assertAlign(
            Alignment.centerRight, Size(300.0, 300.0), Size(100.0, 100.0), Offset(200.0, 100.0)
        )
    }


    @Test
    fun layoutBottomLeft() {
        assertAlign(
            Alignment.bottomLeft, Size(300.0, 300.0), Size(100.0, 100.0), Offset(0.0, 200.0)
        )
    }


    @Test
    fun layoutBottomCenter() {
        assertAlign(
            Alignment.bottomCenter, Size(300.0, 300.0), Size(100.0, 100.0), Offset(100.0, 200.0)
        )
    }


    @Test
    fun layoutBottomRight() {
        assertAlign(
            Alignment.bottomRight, Size(300.0, 300.0), Size(100.0, 100.0), Offset(200.0, 200.0)
        )
    }


    @Test
    fun layoutFactor() {
        val box = RenderPositionedBox(
            widthFactor = 2.0, heightFactor = 3.0
        ).apply {
            child = RenderConstrainedBox(BoxConstraints.tightFor(100.0, 100.0))
        }
        box.layout(BoxConstraints())
        assertEquals(200.0, box.size.width)
        assertEquals(300.0, box.size.height)
        val childOffset = (box.child!!.parentData as BoxParentData).offset
        assertEquals(50.0, childOffset.dx)
        assertEquals(100.0, childOffset.dy)
    }


    @Test
    fun layoutNoChild() {
        val box = RenderPositionedBox()
        box.layout(BoxConstraints.tightFor(300.0, 300.0))
        assertEquals(300.0, box.size.width)
        assertEquals(300.0, box.size.height)
    }
}
