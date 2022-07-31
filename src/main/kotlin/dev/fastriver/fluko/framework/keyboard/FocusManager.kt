//package dev.fastriver.fluko.framework.keyboard
//
//import dev.fastriver.fluko.common.KeyEvent
//import dev.fastriver.fluko.framework.WidgetsFlukoBinding
//
//class FocusManager {
//    companion object {
//        val instance: FocusManager = WidgetsFlukoBinding.buildOwner.focusManager
//    }
//
//    var rootScope: FocusScopeNode
//    var primaryFocus: FocusNode?
//        private set(value) {}
//
//    var markedForFocus: FocusNode?
//    private val dirtyNodes: MutableSet<FocusNode> = mutableSetOf()
//
//    private var haveScheduledUpdate = false
//
//    fun handleKeyMessage(event: KeyEvent) {
//
//    }
//
//    fun markDetached(node: FocusNode) {
//        if(primaryFocus == node) {
//            primaryFocus = null
//        }
//        dirtyNodes.remove(node)
//    }
//
//    fun markNextFocus(node: FocusNode) {
//        if(primaryFocus == node) {
//            markedForFocus = null
//        }
//        else {
//            markedForFocus = node
//            markNeedsUpdate()
//        }
//    }
//
//    private fun markNeedsUpdate() {
//        if(haveScheduledUpdate) return
//        haveScheduledUpdate = false
//        applyFocusChange()
//    }
//
//    private fun applyFocusChange() {
//        haveScheduledUpdate = false
//        val previousFocus = primaryFocus
//
//        if(primaryFocus == null && markedForFocus == null) {
//            markedForFocus = rootScope
//        }
//
//        if(markedForFocus != null && markedForFocus != primaryFocus) {
//            val previousPath = previousFocus?.ancestors.toSet() ?: setOf()
//            val nextPath = markedForFocus!!.ancestors.toSet()
//
//            dirtyNodes.addAll(nextPath.difference(previousPath))
//
//            dirtyNodes.addAll(previousPath.difference(nextPath))
//
//            primaryFocus = markedForFocus
//            markedForFocus = null
//        }
//
//        if(previousFocus != markedForFocus) {
//            if(previousFocus != null) {
//                dirtyNodes.add(previousFocus)
//            }
//            if(primaryFocus != null) {
//                dirtyNodes.add(primaryFocus!!)
//            }
//        }
//
//        for(dirtyNode in dirtyNodes) {
//            dirtyNode.notify()
//        }
//        dirtyNodes.clear()
//    }
//}