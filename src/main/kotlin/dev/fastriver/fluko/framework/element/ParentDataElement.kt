package dev.fastriver.fluko.framework.element

import dev.fastriver.fluko.framework.render.ParentData
import dev.fastriver.fluko.framework.widget.primitive.ParentDataWidget
import dev.fastriver.fluko.framework.widget.primitive.Widget

class ParentDataElement<T: ParentData>(
    widget: ParentDataWidget<T>
): ComponentElement(widget) {
    fun applyParentData(widget: ParentDataWidget<T>) {
        fun applyParentDataToChild(child: Element) {
            if(child is RenderObjectElement<*>) {
                child.updateParentData(widget)
            }
            else {
                child.visitChildren { applyParentDataToChild(it) }
            }
        }
        visitChildren { applyParentDataToChild(it) }
    }

    override fun build(): Widget = (widget as ParentDataWidget<T>).child

    override fun update(newWidget: Widget) {
        val oldWidget = widget as ParentDataWidget<T>
        super.update(newWidget)
        notifyClients(oldWidget)
        dirty = true
        rebuild()
    }

    private fun notifyClients(oldWidget: ParentDataWidget<T>) {
        applyParentData(oldWidget)
    }
}