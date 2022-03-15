package dev.fastriver.fluko.framework.element

import dev.fastriver.fluko.framework.Widget
import dev.fastriver.fluko.framework.render.RenderObject

// TODO: inheritance
// TODO: GlobalKey
abstract class Element(
    open var widget: Widget?
): Comparable<Element> {
    var parent: Element? = null
    var depth: Int = 0
    var owner: BuildOwner? = null
    var dirty: Boolean = true
    var inDirtyList: Boolean = false

    /**
     * 自分と子を探索して一番近い[RenderObjectElement]の持つ[RenderObject]を返す
     */
    open val renderObject: RenderObject?
        get() {
            var result: RenderObject? = null
            fun visit(element: Element) {
                if(element is RenderObjectElement) {
                    result = element.renderObject
                }
                else {
                    element.visitChildren { visit(it) }
                }
            }
            visit(this)
            return result
        }

    // TODO: ElementLifecycle

    /**
     * 子へのアクセス。子を持つElementはoverrideすること
     */
    open fun visitChildren(visitor: ElementVisitor) {

    }

    /**
     * 自身をElementツリーに追加する
     */
    open fun mount(parent: Element?) {
        this.parent = parent
        depth = if(parent != null) parent.depth + 1 else 1
        parent?.let {
            owner = it.owner
        }
    }

    /**
     * 子となるWidget/Elementをとりそれらをツリー下部として展開する
     */
    fun updateChild(child: Element?, newWidget: Widget?): Element? {
        if(newWidget == null) {
            if (child != null) {
                deactivateChild(child)
            }
            return null
        }
        if(child != null) {
            val newChild: Element
            // Flutterではここで`hasSameSuperclass`ということを確認しているが、
            // HotReloadによるStatefulElementとStatelessElementの置換を検知するものなので
            // 省略する
            if (child.widget == newWidget) {
                newChild = child
            }
            else if(Widget.canUpdate(child.widget!!, newWidget)) {
                child.update(newWidget)
                newChild = child
            }
            else {
                deactivateChild(child)
                newChild = inflateWidget(newWidget)
            }
            return newChild
        }
        else {
            return inflateWidget(newWidget)
        }
    }

    open fun update(newWidget: Widget) {
        widget = newWidget
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
    open fun attachRenderObject() {}

    open fun detachRenderObject() {
        visitChildren {
            it.detachRenderObject()
        }
    }

    fun deactivateChild(child: Element) {
        child.parent = null
        child.detachRenderObject()
    }

//    fun forgetChild(child: Element) {
//        // TODO: implement
//    }

    fun activate() {
        if(dirty) {
            owner!!.scheduleBuildFor(this)
        }
    }

    fun deactivate() {
        // TODO: dependencies
    }

    open fun unmount() {
        widget = null
    }

    fun didChangeDependencies() {
        markNeedsBuild()
    }

    fun markNeedsBuild() {
        if (dirty) return
        dirty = true
        owner!!.scheduleBuildFor(this)
    }

    fun rebuild() {
        performRebuild()
    }

    abstract fun performRebuild()

    /**
     * 階層が深いほど大きく、同じ深さならdirtyな方が大きい
     */
    override operator fun compareTo(other: Element): Int {
        when {
            depth < other.depth -> return -1
            other.depth < depth -> return 1
            !dirty && other.dirty -> return -1
            dirty && !other.dirty -> return 1
        }
        return 0
    }
}

typealias ElementVisitor = (child: Element) -> Unit
