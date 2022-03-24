package dev.fastriver.fluko.framework.element

import dev.fastriver.fluko.framework.StatelessWidget
import dev.fastriver.fluko.framework.Widget

class StatelessElement(widget: StatelessWidget) : ComponentElement(widget) {
    override fun build(): Widget = (widget as StatelessWidget).build(this)

    override fun update(newWidget: Widget) {
        super.update(newWidget)
        dirty = true
        rebuild()
    }
}
