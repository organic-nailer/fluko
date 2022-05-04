package dev.fastriver.fluko.framework.widget.primitive

import dev.fastriver.fluko.framework.element.Element
import dev.fastriver.fluko.framework.element.ParentDataElement
import dev.fastriver.fluko.framework.render.ParentData
import dev.fastriver.fluko.framework.render.RenderObject

abstract class ParentDataWidget<T: ParentData>(
    val child: Widget
): Widget() {
    override fun createElement(): Element = ParentDataElement(this)

    /**
     * 渡されたRenderObjectにParentDataを設定する
     */
    abstract fun applyParentData(renderObject: RenderObject)
}