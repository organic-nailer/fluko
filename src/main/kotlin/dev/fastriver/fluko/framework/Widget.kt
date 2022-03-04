package dev.fastriver.fluko.framework

import dev.fastriver.fluko.framework.element.*
import dev.fastriver.fluko.framework.geometrics.*
import dev.fastriver.fluko.framework.render.*

abstract class Widget {
    abstract fun createElement(): Element
}

abstract class RenderObjectWidget : Widget() {
    abstract fun createRenderObject(): RenderObject

    // abstract fun updateRenderObject(renderObject: RenderObject)
    // abstract fun didUnmountRenderObject(renderObject: RenderObject)
}

class RenderObjectToWidgetAdapter(
    val child: Widget?, val container: RenderView
) : RenderObjectWidget() {
    override fun createElement(): Element = RenderObjectToWidgetElement(this)

    override fun createRenderObject(): RenderView = container

    fun attachToRenderTree(): RenderObjectToWidgetElement {
        val element = createElement() as RenderObjectToWidgetElement
        element.mount(null)
        return element
    }
}

abstract class SingleChildRenderObjectWidget(
    val child: Widget?
) : RenderObjectWidget() {
    override fun createElement(): Element = SingleChildRenderObjectElement(this)
}

abstract class MultiChildRenderObjectWidget(
    val children: List<Widget>
) : RenderObjectWidget() {
    override fun createElement(): Element = MultiChildRenderObjectElement(this)
}

abstract class LeafRenderObjectWidget : RenderObjectWidget() {
    override fun createElement(): Element = LeafRenderObjectElement(this)
}


// 実際のWidget実装

class SizedBox(
    child: Widget?,
    val width: Double? = null,
    val height: Double? = null
): SingleChildRenderObjectWidget(child) {
    override fun createRenderObject(): RenderObject = RenderConstrainedBox(
        additionalConstraints = BoxConstraints.tightFor(width, height)
    )
}

class ColoredBox(
    child: Widget?,
    val color: Int,
): SingleChildRenderObjectWidget(child) {
    override fun createRenderObject(): RenderObject = RenderColoredBox(color)
}

class Align(
    child: Widget?,
    val alignment: Alignment = Alignment.center,
    val widthFactor: Double? = null,
    val heightFactor: Double? = null,
): SingleChildRenderObjectWidget(child) {
    override fun createRenderObject(): RenderObject {
        return RenderPositionedBox(
            alignment = alignment,
            widthFactor = widthFactor,
            heightFactor = heightFactor
        )
    }
}

class Flex(
    children: List<Widget> = listOf(),
    val direction: Axis,
    val mainAxisAlignment: MainAxisAlignment = MainAxisAlignment.Start,
    val mainAxisSize: MainAxisSize = MainAxisSize.Max,
    val crossAxisAlignment: CrossAxisAlignment = CrossAxisAlignment.Center,
    val verticalDirection: VerticalDirection = VerticalDirection.Up
): MultiChildRenderObjectWidget(children) {
    override fun createRenderObject(): RenderObject {
        return RenderFlex(
            direction, mainAxisAlignment, mainAxisSize, crossAxisAlignment, verticalDirection
        )
    }
}

class RichText(
    val text: TextSpan,
): MultiChildRenderObjectWidget(listOf()) {
    override fun createRenderObject(): RenderObject {
        return RenderParagraph(
            text
        )
    }
}
