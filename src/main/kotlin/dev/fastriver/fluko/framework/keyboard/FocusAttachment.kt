//package dev.fastriver.fluko.framework.keyboard
//
//class FocusAttachment(
//    val node: FocusNode
//) {
//    val isAttached: Boolean
//        get() = node.attachment == this
//
//    fun detach() {
//        if(isAttached) {
//            if(node.hasPrimaryFocus || node.manager.markedForFocus == node) {
//                node.unfocus(UnfocusDisposition.PreviouslyFocusedChild)
//            }
//
//            node.manager.markDetached(node)
//            node.parent?.removeChild(node)
//            node.attachment = null
//        }
//    }
//
//    fun reparent(parent: FocusNode? = null) {
//        if(isAttached) {
//            val p = parent
//                ?: Focus.maybeOf(node.context, scopeOk = true)
//                ?: FocusManager.instance.rootScope
//            p.reparent(node)
//        }
//    }
//}