package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.framework.geometrics.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class RenderFlexTest {

    @Test
    fun layoutDirection() {
        var flex = RenderFlex(direction = Axis.Vertical).apply {
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
        }
        flex.layout(BoxConstraints(maxHeight = 200.0, maxWidth = 200.0))
        var firstChildOffset = (flex.children[0].parentData as BoxParentData).offset
        var secondChildOffset = (flex.children[1].parentData as BoxParentData).offset
        assertEquals(100.0, flex.size.width)
        assertEquals(200.0, flex.size.height)
        assertEquals(0.0, firstChildOffset.dx)
        assertEquals(0.0, firstChildOffset.dy)
        assertEquals(0.0, secondChildOffset.dx)
        assertEquals(100.0, secondChildOffset.dy)

        flex = RenderFlex(direction = Axis.Horizontal).apply {
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
        }
        flex.layout(BoxConstraints(maxHeight = 200.0, maxWidth = 200.0))
        firstChildOffset = (flex.children[0].parentData as BoxParentData).offset
        secondChildOffset = (flex.children[1].parentData as BoxParentData).offset
        assertEquals(200.0, flex.size.width)
        assertEquals(100.0, flex.size.height)
        assertEquals(0.0, firstChildOffset.dx)
        assertEquals(0.0, firstChildOffset.dy)
        assertEquals(100.0, secondChildOffset.dx)
        assertEquals(0.0, secondChildOffset.dy)
    }

    @Test
    fun layoutAxisSize() {
        var flex = RenderFlex(mainAxisSize = MainAxisSize.Max).apply {
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
        }
        flex.layout(BoxConstraints(maxHeight = 300.0))
        assertEquals(100.0, flex.size.width)
        assertEquals(300.0, flex.size.height)

        flex = RenderFlex(mainAxisSize = MainAxisSize.Min).apply {
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
        }
        flex.layout(BoxConstraints(maxHeight = 300.0))
        assertEquals(100.0, flex.size.width)
        assertEquals(200.0, flex.size.height)
    }

    @Test
    fun layoutMainAlignment() { // ■ ■ _ _
        var flex = RenderFlex(mainAxisAlignment = MainAxisAlignment.Start).apply {
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
        }
        flex.layout(BoxConstraints(maxHeight = 300.0))
        var firstChildOffset = (flex.children[0].parentData as BoxParentData).offset
        var secondChildOffset = (flex.children[1].parentData as BoxParentData).offset
        assertEquals(0.0, firstChildOffset.dy)
        assertEquals(100.0, secondChildOffset.dy)

        // _ ■ ■ _
        flex = RenderFlex(mainAxisAlignment = MainAxisAlignment.Center).apply {
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
        }
        flex.layout(BoxConstraints(maxHeight = 300.0))
        firstChildOffset = (flex.children[0].parentData as BoxParentData).offset
        secondChildOffset = (flex.children[1].parentData as BoxParentData).offset
        assertEquals(50.0, firstChildOffset.dy)
        assertEquals(150.0, secondChildOffset.dy)

        // _ _ ■ ■
        flex = RenderFlex(mainAxisAlignment = MainAxisAlignment.End).apply {
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
        }
        flex.layout(BoxConstraints(maxHeight = 300.0))
        firstChildOffset = (flex.children[0].parentData as BoxParentData).offset
        secondChildOffset = (flex.children[1].parentData as BoxParentData).offset
        assertEquals(100.0, firstChildOffset.dy)
        assertEquals(200.0, secondChildOffset.dy)

        // ■ _ ■ _ ■
        flex = RenderFlex(mainAxisAlignment = MainAxisAlignment.SpaceBetween).apply {
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
        }
        flex.layout(BoxConstraints(maxHeight = 420.0))
        firstChildOffset = (flex.children[0].parentData as BoxParentData).offset
        secondChildOffset = (flex.children[1].parentData as BoxParentData).offset
        var thirdChildOffset = (flex.children[2].parentData as BoxParentData).offset
        assertEquals(0.0, firstChildOffset.dy)
        assertEquals(160.0, secondChildOffset.dy)
        assertEquals(320.0, thirdChildOffset.dy)

        // _ ■ _ _ ■ _ _ ■ _
        flex = RenderFlex(mainAxisAlignment = MainAxisAlignment.SpaceAround).apply {
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
        }
        flex.layout(BoxConstraints(maxHeight = 420.0))
        firstChildOffset = (flex.children[0].parentData as BoxParentData).offset
        secondChildOffset = (flex.children[1].parentData as BoxParentData).offset
        thirdChildOffset = (flex.children[2].parentData as BoxParentData).offset
        assertEquals(20.0, firstChildOffset.dy)
        assertEquals(160.0, secondChildOffset.dy)
        assertEquals(300.0, thirdChildOffset.dy)

        // _ ■ _ ■ _ ■　_
        flex = RenderFlex(mainAxisAlignment = MainAxisAlignment.SpaceEvenly).apply {
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
        }
        flex.layout(BoxConstraints(maxHeight = 420.0))
        firstChildOffset = (flex.children[0].parentData as BoxParentData).offset
        secondChildOffset = (flex.children[1].parentData as BoxParentData).offset
        thirdChildOffset = (flex.children[2].parentData as BoxParentData).offset
        assertEquals(30.0, firstChildOffset.dy)
        assertEquals(160.0, secondChildOffset.dy)
        assertEquals(290.0, thirdChildOffset.dy)
    }

    @Test
    fun layoutCrossAlignment() {
        var flex = RenderFlex(crossAxisAlignment = CrossAxisAlignment.Start).apply {
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(200.0, 100.0))))
        }
        flex.layout(BoxConstraints(maxHeight = 200.0))
        var firstChildOffset = (flex.children[0].parentData as BoxParentData).offset
        var secondChildOffset = (flex.children[1].parentData as BoxParentData).offset
        assertEquals(0.0, firstChildOffset.dx)
        assertEquals(0.0, secondChildOffset.dx)

        flex = RenderFlex(crossAxisAlignment = CrossAxisAlignment.Center).apply {
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(200.0, 100.0))))
        }
        flex.layout(BoxConstraints(maxHeight = 200.0))
        firstChildOffset = (flex.children[0].parentData as BoxParentData).offset
        secondChildOffset = (flex.children[1].parentData as BoxParentData).offset
        assertEquals(50.0, firstChildOffset.dx)
        assertEquals(0.0, secondChildOffset.dx)

        flex = RenderFlex(crossAxisAlignment = CrossAxisAlignment.End).apply {
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(200.0, 100.0))))
        }
        flex.layout(BoxConstraints(maxHeight = 200.0))
        firstChildOffset = (flex.children[0].parentData as BoxParentData).offset
        secondChildOffset = (flex.children[1].parentData as BoxParentData).offset
        assertEquals(100.0, firstChildOffset.dx)
        assertEquals(0.0, secondChildOffset.dx)

        flex = RenderFlex(crossAxisAlignment = CrossAxisAlignment.Stretch).apply {
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tightFor(height = 100.0, width = null)))
        }
        flex.layout(BoxConstraints.tight(Size(200.0, 300.0)))
        val firstChild = flex.children[0]
        val secondChild = flex.children[1]
        firstChildOffset = (firstChild.parentData as BoxParentData).offset
        secondChildOffset = (secondChild.parentData as BoxParentData).offset
        assertEquals(0.0, firstChildOffset.dx)
        assertEquals(0.0, secondChildOffset.dx)
        assertEquals(200.0, firstChild.size.width)
        assertEquals(200.0, secondChild.size.width)
    }

    @Test
    fun layoutVerticalDirection() {
        var flex = RenderFlex(verticalDirection = VerticalDirection.Down).apply {
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
        }
        flex.layout(BoxConstraints(maxHeight = 200.0, maxWidth = 200.0))
        var firstChildOffset = (flex.children[0].parentData as BoxParentData).offset
        var secondChildOffset = (flex.children[1].parentData as BoxParentData).offset
        assertEquals(0.0, firstChildOffset.dy)
        assertEquals(100.0, secondChildOffset.dy)


        flex = RenderFlex(verticalDirection = VerticalDirection.Up).apply {
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
            insert(RenderConstrainedBox(BoxConstraints.tight(Size(100.0, 100.0))))
        }
        flex.layout(BoxConstraints(maxHeight = 200.0, maxWidth = 200.0))
        firstChildOffset = (flex.children[0].parentData as BoxParentData).offset
        secondChildOffset = (flex.children[1].parentData as BoxParentData).offset
        assertEquals(100.0, firstChildOffset.dy)
        assertEquals(0.0, secondChildOffset.dy)
    }
}