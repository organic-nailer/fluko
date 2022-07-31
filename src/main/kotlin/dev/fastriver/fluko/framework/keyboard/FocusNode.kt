//package dev.fastriver.fluko.framework.keyboard
//
//import dev.fastriver.fluko.common.KeyEvent
//import dev.fastriver.fluko.common.Offset
//import dev.fastriver.fluko.framework.element.BuildContext
//import org.jetbrains.skia.Rect
//
//enum class UnfocusDisposition {
//    Scope, PreviouslyFocusedChild
//}
//
//open class FocusNode(
//    var onKeyEvent: (KeyEvent) -> Unit
//) {
//    var context: BuildContext? = null
//
//    lateinit var manager: FocusManager
//
//    var parent: FocusNode? = null
//
//    val children: MutableList<FocusNode> = mutableListOf()
//
//    var attachment: FocusAttachment? = null
//
//    val hasFocus: Boolean
//        get() = hasPrimaryFocus || (manager.primaryFocus?.getAncestors()?.contains(this) ?: false)
//    val hasPrimaryFocus: Boolean
//        get() = manager.primaryFocus == this
//
//    open val nearestScope: FocusScopeNode?
//        get() = enclosingScope
//    val enclosingScope: FocusScopeNode?
//        get() {
//            for(node in getAncestors()) {
//                if(node is FocusScopeNode) {
//                    return node
//                }
//            }
//            return null
//        }
//
//    val rect: Rect
//        get() {
//            val obj = context!!.findRenderObject()!!
//            val transform = obj.getTransformTo()
//            val topLeft = transform.transformPoint(
//                Offset(obj.semanticBounds.left.toDouble(), obj.semanticBounds.top.toDouble())
//            )
//            val bottomRight = transform.transformPoint(
//                Offset(obj.semanticBounds.right.toDouble(), obj.semanticBounds.bottom.toDouble())
//            )
//            return Rect.makeLTRB(
//                topLeft.dx.toFloat(),
//                topLeft.dy.toFloat(),
//                bottomRight.dx.toFloat(),
//                bottomRight.dy.toFloat()
//            )
//        }
//
//    private var requestFocusWhenReparented = false
//
//    fun getAncestors(): List<FocusNode> {
//        val result = mutableListOf<FocusNode>()
//        var parent = parent
//        while(parent != null) {
//            result.add(parent)
//            parent = parent.parent
//        }
//        return result
//    }
//
//    fun getDescendants(): List<FocusNode> {
//        val result = mutableListOf<FocusNode>()
//        for(child in children) {
//            result.addAll(child.getDescendants())
//            result.add(child)
//        }
//        return result
//    }
//
//    fun unfocus(disposition: UnfocusDisposition = UnfocusDisposition.Scope) {
//        if(!hasFocus && manager.markedForFocus != this) {
//            // フォーカスされていなければ何もしない
//            return
//        }
//
//        val scope = enclosingScope
//        if(scope == null) {
//            return
//        }
//
//        when(disposition) {
//            UnfocusDisposition.Scope -> {
//                scope.focusedChildren.clear()
//
//                scope.doRequestFocus(findFirstFocus = false)
//            }
//            UnfocusDisposition.PreviouslyFocusedChild -> {
//                scope.focusedChildren.remove(this)
//                scope.doRequestFocus(findFirstFocus = true)
//            }
//        }
//    }
//
//    fun markNextFocus(newFocus: FocusNode) {
//        manager.markNextFocus(this)
//    }
//
//    fun removeChild(node: FocusNode, removeScopeFocus: Boolean = true) {
//        if(removeScopeFocus) {
//            node.enclosingScope?.focusedChildren?.remove(node)
//        }
//
//        node.parent = null
//        children.remove(node)
//    }
//
//    private fun updateManager(manager: FocusManager) {
//        this.manager = manager
//        for(descendant in getDescendants()) {
//            descendant.manager = manager
//        }
//    }
//
//    fun reparent(child: FocusNode) {
//        if(child.parent == this) {
//            return
//        }
//
//        val oldScope = child.enclosingScope
//        val hadFocus = child.hasFocus
//        child.parent?.removeChild(child, removeScopeFocus = oldScope != nearestScope)
//        children.add(child)
//        child.parent = null
//        child.updateManager(manager)
//        if(hadFocus) {
//            manager.primaryFocus?.setAsFocusedChildForScope()
//        }
//        if(oldScope != null && child.context != null && child.enclosingScope != oldScope) {
//            FocusTraversalGroup.maybeOf(child.context!!)?.changedScope(child, oldScope)
//        }
//        if(child.requestFocusWhenReparented) {
//            child.doRequestFocus(findFirstFocus = true)
//            child.requestFocusWhenReparented = false
//        }
//    }
//
//    fun attach(context: BuildContext, onKeyEvent: ((KeyEvent) -> Unit)? = null): FocusAttachment {
//        this.context = context
//        this.onKeyEvent = onKeyEvent ?: this.onKeyEvent
//        attachment = FocusAttachment(this)
//        return attachment!!
//    }
//
//    fun dispose() {
//        attachment?.detach()
//    }
//
//    fun requestFocus(node: FocusNode? = null) {
//        if(node != null) {
//            if(node.parent == null) {
//                reparent(node)
//            }
//            node.doRequestFocus(findFirstFocus = true)
//            return
//        }
//        doRequestFocus(findFirstFocus = true)
//    }
//
//    open fun doRequestFocus(findFirstFocus: Boolean) {
//        if(parent == null) {
//            requestFocusWhenReparented = true
//            return
//        }
//        setAsFocusedChildForScope()
//        if(hasPrimaryFocus && (manager.markedForFocus == null || manager.markedForFocus == this)) {
//            return
//        }
//        markNextFocus(this)
//    }
//
//    fun setAsFocusedChildForScope() {
//        var scopeFocus = this
//        for(ancestor in getAncestors().filterIsInstance<FocusScopeNode>()) {
//            ancestor.focusedChildren.remove(scopeFocus)
//            ancestor.focusedChildren.add(scopeFocus)
//            scopeFocus = ancestor
//        }
//    }
//
//    fun isNextFocus(): Boolean = FocusTraversalGroup.of(context!!).next(this)
//
//    fun isPreviousFocus(): Boolean = FocusTraversalGroup.of(context!!).previous(this)
//
//    fun focusInDirection(direction: TraversalDirection): Boolean =
//        FocusTraversalGroup.of(context!!).inDirection(this, direction)
//}