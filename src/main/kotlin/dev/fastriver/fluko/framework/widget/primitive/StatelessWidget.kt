package dev.fastriver.fluko.framework.widget.primitive

import dev.fastriver.fluko.framework.element.BuildContext
import dev.fastriver.fluko.framework.element.Element
import dev.fastriver.fluko.framework.element.StatelessElement

abstract class StatelessWidget : Widget() {
    override fun createElement(): Element = StatelessElement(this)

    abstract fun build(context: BuildContext): Widget
}