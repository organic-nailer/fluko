package dev.fastriver.fluko.framework

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

abstract class RenderObjectElement(
    widget: RenderObjectWidget
) : Element(widget) {
    override var widget: Widget? = super.widget as RenderObjectWidget

    var renderObject: RenderObject? = null

    override fun mount(parent: Element?) {
        super.mount(parent)
        renderObject = (widget as RenderObjectWidget).createRenderObject()
        attachRenderObject()
    }

    override fun attachRenderObject() {
        val ancestorRenderObjectElement = findAncestorRenderObjectElement()
        ancestorRenderObjectElement?.insertRenderObjectChild(renderObject!!)
    }

    /**
     * Elementツリーで一番直近のRenderObjectElementを探す
     */
    fun findAncestorRenderObjectElement(): RenderObjectElement? {
        var ancestor = parent
        while(ancestor != null && ancestor !is RenderObjectElement) {
            ancestor = ancestor.parent
        }
        return ancestor as RenderObjectElement?
    }

    /**
     * 渡されたRenderObjectを子に追加する
     * 実装はサブクラスで行う(子を持たないクラスは実装しない)
     */
    open fun insertRenderObjectChild(child: RenderObject) {

    }

}

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
        (renderObject as RenderObjectWithChildMixin<RenderObject>).setRenderObjectChild(child)
    }
}

class SingleChildRenderObjectElement(widget: SingleChildRenderObjectWidget) : RenderObjectElement(widget) {
    val widgetCasted: SingleChildRenderObjectWidget = widget
    private var child: Element? = null

    override fun mount(parent: Element?) {
        super.mount(parent)
        child = updateChild(child, widgetCasted.child)
    }

    override fun insertRenderObjectChild(child: RenderObject) {
        (renderObject as RenderObjectWithChildMixin<RenderObject>).setRenderObjectChild(child)
    }
}

class MultiChildRenderObjectElement(widget: MultiChildRenderObjectWidget) : RenderObjectElement(widget) {
    val widgetCasted: MultiChildRenderObjectWidget = widget
    private var children: List<Element> = listOf()

    override fun mount(parent: Element?) {
        super.mount(parent)
        children = widgetCasted.children.map {
            inflateWidget(it)
        }
    }

    override fun insertRenderObjectChild(child: RenderObject) {
        (renderObject as ContainerRenderObjectMixin<RenderObject>).insertChild(child)
    }
}

class LeafRenderObjectElement(widget: LeafRenderObjectWidget) : RenderObjectElement(widget)
