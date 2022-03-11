package dev.fastriver.fluko.framework.element

import dev.fastriver.fluko.framework.RenderObjectToWidgetAdapter
import dev.fastriver.fluko.framework.RenderObjectWidget
import dev.fastriver.fluko.framework.render.RenderObject
import dev.fastriver.fluko.framework.render.RenderObjectWithChild

class RenderObjectToWidgetElement(widget: RenderObjectWidget) : RenderObjectElement(widget) {
    private var child: Element? = null
    override fun mount(parent: Element?) {
        super.mount(parent)
        rebuild()
    }

    /**
     * ツリーを再構築する
     */
    private fun rebuild() {
        child = updateChild(child, (widget as RenderObjectToWidgetAdapter).child)
    }

    override fun insertRenderObjectChild(child: RenderObject) {
        (renderObject as RenderObjectWithChild<RenderObject>).child = child
    }
}