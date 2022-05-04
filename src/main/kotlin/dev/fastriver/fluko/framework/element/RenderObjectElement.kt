package dev.fastriver.fluko.framework.element

import dev.fastriver.fluko.framework.widget.primitive.RenderObjectWidget
import dev.fastriver.fluko.framework.widget.primitive.Widget
import dev.fastriver.fluko.framework.render.RenderObject
import dev.fastriver.fluko.framework.widget.primitive.ParentDataWidget

abstract class RenderObjectElement<T: RenderObject>(
    widget: RenderObjectWidget<T>
) : Element(widget) {
    private val widgetCasted: RenderObjectWidget<T>
        get() = widget as RenderObjectWidget<T>

    override val renderObject: RenderObject?
        get() = renderObjectInternal
    private var renderObjectInternal: RenderObject? = null

    private var ancestorRenderObjectElement: RenderObjectElement<*>? = null

    override fun mount(parent: Element?) {
        super.mount(parent)
        renderObjectInternal = (widget as RenderObjectWidget<*>).createRenderObject()
        attachRenderObject()
        dirty = false
    }

    override fun update(newWidget: Widget) {
        super.update(newWidget)
        performRebuild()
    }

    override fun performRebuild() {
        widgetCasted.updateRenderObject(renderObject as T)
        dirty = false
    }

    protected fun updateChildren(
        oldChildren: List<Element>, newWidgets: List<Widget>, forgottenChildren: Set<Element>? = null
    ): List<Element> {
        fun replaceWithNullIfForgotten(child: Element): Element? {
            return if(forgottenChildren?.contains(child) == true) null else child
        }

        var newChildrenTop = 0
        var oldChildrenTop = 0
        var newChildrenBottom = newWidgets.size - 1
        var oldChildrenBottom = oldChildren.size - 1

        val newChildren: MutableList<Element?> =
            if(oldChildren.size == newWidgets.size) oldChildren.toMutableList() else (1..newWidgets.size).map { null }
                .toMutableList()

        while((oldChildrenTop <= oldChildrenBottom) && (newChildrenTop <= newChildrenBottom)) {
            val oldChild = replaceWithNullIfForgotten(oldChildren[oldChildrenTop])
            val newWidget = newWidgets[newChildrenTop]
            if(oldChild == null || !Widget.canUpdate(oldChild.widget, newWidget)) {
                break
            }
            val newChild = updateChild(oldChild, newWidget)
            newChildren[newChildrenTop] = newChild
            newChildrenTop++
            oldChildrenTop++
        }

        while((oldChildrenTop <= oldChildrenBottom) && (newChildrenTop <= newChildrenBottom)) {
            val oldChild = replaceWithNullIfForgotten(oldChildren[oldChildrenBottom])
            val newWidget = newWidgets[newChildrenBottom]
            if(oldChild == null || !Widget.canUpdate(oldChild.widget, newWidget)) {
                break
            }
            newChildrenTop--
            oldChildrenTop--
        }

        val haveOldChildren = oldChildrenTop <= oldChildrenBottom
        if(haveOldChildren) {
            while(oldChildrenTop <= oldChildrenBottom) {
                val oldChild = replaceWithNullIfForgotten(oldChildren[oldChildrenTop])
                if(oldChild != null) { // TODO: keyed
                    deactivateChild(oldChild)
                }
                oldChildrenTop++
            }
        }

        while(newChildrenTop <= newChildrenBottom) {
            val oldChild: Element? = null
            val newWidget = newWidgets[newChildrenTop]
            val newChild = updateChild(oldChild, newWidget)
            newChildren[newChildrenTop] = newChild
            newChildrenTop++
        }

        newChildrenBottom = newWidgets.size - 1
        oldChildrenBottom = oldChildren.size - 1

        while((oldChildrenTop <= oldChildrenBottom) && (newChildrenTop <= newChildrenBottom)) {
            val oldChild = oldChildren[oldChildrenTop]
            val newWidget = newWidgets[newChildrenTop]
            val newChild = updateChild(oldChild, newWidget)
            newChildren[newChildrenTop] = newChild
            newChildrenTop++
            oldChildrenTop++
        }

        return newChildren.mapNotNull { it }
    }

    //    override fun unmount() {
    //        val oldWidget = widgetCasted
    //        super.unmount()
    //        // oldWidget.didUnmountRenderObject(renderObject)
    //        renderObject!!.dispose()
    //        renderObjectInternal = null
    //    }

    fun updateParentData(parentDataWidget: ParentDataWidget<*>) {
        parentDataWidget.applyParentData(renderObject!!)
    }

    override fun attachRenderObject() {
        ancestorRenderObjectElement = findAncestorRenderObjectElement()
        ancestorRenderObjectElement?.insertRenderObjectChild(renderObject!!)
        val parentDataElement = findAncestorParentDataElement()
        parentDataElement?.let {
            updateParentData(it.widget as ParentDataWidget<*>)
        }
    }

    override fun detachRenderObject() {
        if(ancestorRenderObjectElement != null) {
            ancestorRenderObjectElement!!.removeRenderObjectChild(renderObject!!)
            ancestorRenderObjectElement = null
        }
    }

    /**
     * Elementツリーで一番直近のRenderObjectElementを探す
     */
    fun findAncestorRenderObjectElement(): RenderObjectElement<*>? {
        var ancestor = parent
        while(ancestor != null && ancestor !is RenderObjectElement<*>) {
            ancestor = ancestor.parent
        }
        return ancestor as RenderObjectElement<*>?
    }

    private fun findAncestorParentDataElement(): ParentDataElement<*>? {
        var ancestor = parent
        var result: ParentDataElement<*>? = null
        while(ancestor != null && ancestor !is RenderObjectElement<*>) {
            if(ancestor is ParentDataElement<*>) {
                result = ancestor
                break
            }
            ancestor = ancestor.parent
        }
        return result
    }

    /**
     * 渡されたRenderObjectを子に追加する
     * 実装はサブクラスで行う(子を持たないクラスは実装しない)
     */
    open fun insertRenderObjectChild(child: RenderObject) {

    }

    open fun removeRenderObjectChild(child: RenderObject) {

    }
}