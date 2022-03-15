package dev.fastriver.fluko.framework.element

import dev.fastriver.fluko.framework.RenderObjectToWidgetAdapter
import dev.fastriver.fluko.framework.RenderObjectWidget
import dev.fastriver.fluko.framework.Widget
import dev.fastriver.fluko.framework.render.RenderObject
import dev.fastriver.fluko.framework.render.RenderObjectWithChild

class RenderObjectToWidgetElement(widget: RenderObjectWidget) : RenderObjectElement(widget) {
    private var child: Element? = null
    var newWidget: Widget? = null

    override fun visitChildren(visitor: ElementVisitor) {
        child?.let(visitor)
    }

    override fun mount(parent: Element?) {
        super.mount(parent)
        _rebuild()
    }

    override fun update(newWidget: Widget) {
        super.update(newWidget)
        _rebuild()
    }

    override fun performRebuild() {
        if(newWidget != null) {
            val tmp = newWidget!!
            newWidget = null
            update(tmp)
        }
        super.performRebuild()
    }

    /**
     * ツリーを再構築する
     */
    private fun _rebuild() {
        child = updateChild(child, (widget as RenderObjectToWidgetAdapter).child)
    }

    override fun insertRenderObjectChild(child: RenderObject) {
        (renderObject as RenderObjectWithChild<RenderObject>).child = child
    }
}