package dev.fastriver.fluko.framework.widget.primitive

import dev.fastriver.fluko.framework.element.Element
import dev.fastriver.fluko.framework.element.InheritedElement

abstract class InheritedWidget(
    val child: Widget
) : Widget() {
    override fun createElement(): Element = InheritedElement(this)

    abstract fun updateShouldNotify(oldWidget: InheritedWidget): Boolean
}