package dev.fastriver.fluko.framework.widget.primitive

import dev.fastriver.fluko.framework.Key
import dev.fastriver.fluko.framework.element.*
import dev.fastriver.fluko.framework.geometrics.*
import dev.fastriver.fluko.framework.render.*

abstract class Widget {
    companion object {
        fun canUpdate(oldWidget: Widget, newWidget: Widget): Boolean {
            return oldWidget::class == newWidget::class && oldWidget.key == newWidget.key
        }
    }

    val key: Key? = null

    abstract fun createElement(): Element
}

abstract class RenderObjectWidget<RenderObjectType: RenderObject> : Widget() {
    abstract fun createRenderObject(): RenderObjectType

    /**
     * RenderObjectの情報を更新する
     *
     * [Element.performRebuild]で発火される
     */
    open fun updateRenderObject(renderObject: RenderObjectType) {}

    /**
     * 関連付けられたRenderObjectが消されたときに呼ばれる
     */
    open fun didUnmountRenderObject(renderObject: RenderObjectType) {}
}

class RenderObjectToWidgetAdapter(
    val child: Widget?, val container: RenderView
) : RenderObjectWidget<RenderView>() {
    override fun createElement(): Element = RenderObjectToWidgetElement(this)

    override fun createRenderObject(): RenderView = container

    fun attachToRenderTree(
        owner: BuildOwner,
        element: RenderObjectToWidgetElement<*>? = null
    ): RenderObjectToWidgetElement<*> {
        val result: RenderObjectToWidgetElement<*>
        if(element == null) {
            result = createElement() as RenderObjectToWidgetElement<*>
            result.owner = owner
            owner.buildScope {
                result.mount(null)
            }
        } else {
            result = element
            result.newWidget = this
            result.markNeedsBuild()
        }
        return result
    }
}

abstract class SingleChildRenderObjectWidget<RenderObjectType: RenderObject>(
    val child: Widget?
) : RenderObjectWidget<RenderObjectType>() {
    override fun createElement(): Element = SingleChildRenderObjectElement(this)
}

abstract class MultiChildRenderObjectWidget<RenderObjectType: RenderObject>(
    val children: List<Widget>
) : RenderObjectWidget<RenderObjectType>() {
    override fun createElement(): Element = MultiChildRenderObjectElement(this)
}

abstract class LeafRenderObjectWidget<RenderObjectType: RenderObject> : RenderObjectWidget<RenderObjectType>() {
    override fun createElement(): Element = LeafRenderObjectElement(this)
}
