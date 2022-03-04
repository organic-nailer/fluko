package dev.fastriver.fluko.framework.element

import dev.fastriver.fluko.framework.Widget

abstract class Element(
    open var widget: Widget?
) {
    var parent: Element? = null

    /**
     * 自身をElementツリーに追加する
     */
    open fun mount(parent: Element?) {
        this.parent = parent
    }

    //    open val renderObject: RenderObject?
    //        get() {
    //            if(this is RenderObjectWidget) renderObject
    //            else
    //        }

    /**
     * 子となるWidget/Elementをとりそれらをツリー下部として展開する
     */
    fun updateChild(child: Element?, newWidget: Widget?): Element? { // とりあえずchildが来る場合は考えない
        assert(child == null)
        if(newWidget == null) return null
        val newChild = inflateWidget(newWidget!!)
        return newChild
    }

    /**
     * 与えられたWidgetから子Elementを作成しツリーに追加する
     */
    protected fun inflateWidget(newWidget: Widget): Element {
        val newChild = newWidget.createElement()
        newChild.mount(this)
        return newChild
    }

    /**
     * 自身がRenderObjectを持つ場合はRenderツリーにそれを追加する
     */
    open fun attachRenderObject() {

    }
}