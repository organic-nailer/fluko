//package dev.fastriver.fluko.framework.keyboard
//
//import dev.fastriver.fluko.common.KeyEvent
//
//class FocusScopeNode(
//    onKeyEvent: (KeyEvent) -> Unit
//) : FocusNode(onKeyEvent) {
//    override val nearestScope: FocusScopeNode
//        get() = this
//
//    val isFirstFocus: Boolean
//        get() = enclosingScope!!.focusedChild == this
//
//    val focusedChild: FocusNode?
//        get() = if(focusedChildren.isNotEmpty()) focusedChildren.last() else null
//
//    val focusedChildren: MutableList<FocusNode> = mutableListOf()
//
//    fun setFirstFocus(scope: FocusScopeNode) {
//        if(scope.parent == null) {
//            reparent(scope)
//        }
//        if(hasFocus) {
//            scope.doRequestFocus(findFirstFocus = true)
//        } else {
//            scope.setAsFocusedChildForScope()
//        }
//    }
//
//    override fun doRequestFocus(findFirstFocus: Boolean) {
//        val focusedChild = focusedChild
//        if(!findFirstFocus || focusedChild == null) {
//            setAsFocusedChildForScope()
//            markNextFocus(this)
//            return
//        }
//
//        focusedChild.doRequestFocus(findFirstFocus = true)
//    }
//}