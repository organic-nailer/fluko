package dev.fastriver.fluko.framework.element

import dev.fastriver.fluko.framework.RenderObjectToWidgetAdapter
import dev.fastriver.fluko.framework.SizedBox
import dev.fastriver.fluko.framework.render.RenderConstrainedBox
import dev.fastriver.fluko.framework.render.RenderView
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ElementTest {
    @Test
    fun updateElement() {
        val renderView = RenderView(100.0, 100.0)
        val buildOwner = BuildOwner { }
        val viewWidget = SizedBox(child = null, width = 50.0, height = 50.0)
        val rootWidget = RenderObjectToWidgetAdapter(
            viewWidget,
            renderView
        )
        val rootElement = rootWidget.attachToRenderTree(buildOwner)
        assertEquals(rootElement.widget, rootWidget)
        assert(rootWidget.child is SizedBox)
        var leafElement: Element? = null
        rootElement.visitChildren {
            assert(it is SingleChildRenderObjectElement)
            leafElement = it
        }
        (leafElement!!.renderObject as RenderConstrainedBox).let {
            assertEquals(50.0, it.additionalConstraints.maxWidth)
            assertEquals(50.0, it.additionalConstraints.maxHeight)
        }

        val nextViewWidget = SizedBox(child = null, width = 100.0, height = 25.0)
        val nextRootWidget = RenderObjectToWidgetAdapter(
            nextViewWidget,
            renderView
        )
        val nextRootElement = nextRootWidget.attachToRenderTree(buildOwner, rootElement)
        buildOwner.buildScope {}
        assertEquals(rootElement, nextRootElement)
        nextRootElement.visitChildren {
            assertEquals(leafElement, it)
        }
        assertNotEquals(rootWidget, nextRootWidget)
        assertNotEquals(rootWidget.child, nextRootWidget.child)
        (leafElement!!.renderObject as RenderConstrainedBox).let {
            assertEquals(100.0, it.additionalConstraints.maxWidth)
            assertEquals(25.0, it.additionalConstraints.maxHeight)
        }
    }
}