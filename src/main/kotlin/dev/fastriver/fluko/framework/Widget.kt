package dev.fastriver.fluko.framework

import dev.fastriver.fluko.framework.element.*
import dev.fastriver.fluko.framework.geometrics.*
import dev.fastriver.fluko.framework.render.*

abstract class Widget {
    companion object {
        fun canUpdate(oldWidget: Widget, newWidget: Widget): Boolean {
            return oldWidget::class == newWidget::class
                && oldWidget.key == newWidget.key
        }
    }

    val key: Key? = null

    abstract fun createElement(): Element
}

abstract class RenderObjectWidget : Widget() {
    abstract fun createRenderObject(): RenderObject

    /**
     * RenderObjectの情報を更新する
     *
     * [Element.performRebuild]で発火される
     */
    open fun updateRenderObject(renderObject: RenderObject) { }

    /**
     * 関連付けられたRenderObjectが消されたときに呼ばれる
     */
    open fun didUnmountRenderObject(renderObject: RenderObject) { }
}

class RenderObjectToWidgetAdapter(
    val child: Widget?, val container: RenderView
) : RenderObjectWidget() {
    override fun createElement(): Element = RenderObjectToWidgetElement(this)

    override fun createRenderObject(): RenderView = container

    fun attachToRenderTree(owner: BuildOwner, element: RenderObjectToWidgetElement? = null): RenderObjectToWidgetElement {
        val result: RenderObjectToWidgetElement
        if(element == null) {
            result = createElement() as RenderObjectToWidgetElement
            result.owner = owner
            owner.buildScope {
                result.mount(null)
            }
        }
        else {
            result = element
            result.newWidget = this
            result.markNeedsBuild()
        }
        return result
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
    private val additionalConstraints: BoxConstraints
        get() = BoxConstraints.tightFor(width, height)

    override fun createRenderObject(): RenderObject = RenderConstrainedBox(
        additionalConstraints = BoxConstraints.tightFor(width, height)
    )

    override fun updateRenderObject(renderObject: RenderObject) {
        renderObject as RenderConstrainedBox
        renderObject.additionalConstraints = additionalConstraints
    }
}

class ColoredBox(
    child: Widget?,
    val color: Int,
): SingleChildRenderObjectWidget(child) {
    override fun createRenderObject(): RenderObject = RenderColoredBox(color)

    override fun updateRenderObject(renderObject: RenderObject) {
        renderObject as RenderColoredBox
        renderObject.color = color
    }
}

class Listener(
    child: Widget? = null,
    val onPointerDown: PointerEventListener? = null,
    val onPointerMove: PointerEventListener? = null,
    val onPointerUp: PointerEventListener? = null,
    val onPointerCancel: PointerEventListener? = null,
): SingleChildRenderObjectWidget(child) {
    override fun createRenderObject(): RenderObject {
        return RenderPointerListener(
            onPointerDown,
            onPointerMove,
            onPointerUp,
            onPointerCancel
        )
    }

    override fun updateRenderObject(renderObject: RenderObject) {
        renderObject as RenderPointerListener
        renderObject.let {
            it.onPointerDown = onPointerDown
            it.onPointerMove = onPointerMove
            it.onPointerUp = onPointerUp
            it.onPointerCancel = onPointerCancel
        }
    }
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
    val verticalDirection: VerticalDirection = VerticalDirection.Down
): MultiChildRenderObjectWidget(children) {
    override fun createRenderObject(): RenderObject {
        return RenderFlex(
            direction, mainAxisAlignment, mainAxisSize, crossAxisAlignment, verticalDirection
        )
    }

    override fun updateRenderObject(renderObject: RenderObject) {
        (renderObject as RenderFlex).let {
            it.direction = direction
            it.mainAxisAlignment = mainAxisAlignment
            it.mainAxisSize = mainAxisSize
            it.crossAxisAlignment = crossAxisAlignment
            it.verticalDirection = verticalDirection
        }
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

    override fun updateRenderObject(renderObject: RenderObject) {
        (renderObject as RenderParagraph).let {
            it.text = text
        }
    }
}

abstract class StatelessWidget: Widget() {
    override fun createElement(): Element = StatelessElement(this)

    abstract fun build(context: BuildContext): Widget
}

abstract class StatefulWidget: Widget() {
    override fun createElement(): Element = StatefulElement(this)

    abstract fun createState(): State<*>
}

abstract class State<T: StatefulWidget> {
    val widget: T
        get() = widgetInternal!!
    var widgetInternal: T? = null

    var element: StatefulElement? = null
    val context: BuildContext
        get() = element!!

    open fun initState() {}

    open fun didUpdateWidget(oldWidget: T) {}

    protected fun setState(func: () -> Unit) {
        func()
        element!!.markNeedsBuild()
    }

    abstract fun build(context: BuildContext): Widget

    open fun didChangeDependencies() {}
}
