//package dev.fastriver.fluko.framework.keyboard
//
//import dev.fastriver.fluko.framework.element.BuildContext
//import dev.fastriver.fluko.framework.widget.primitive.StatefulWidget
//import dev.fastriver.fluko.framework.widget.primitive.Widget
//
//class FocusTraversalGroup(
//    val child: Widget,
//    val policy: FocusTraversalPolicy
//): StatefulWidget() {
//    companion object {
//        fun of(context: BuildContext) {
//            val marker = context.dependOnInheritedWidgetOfExactType()
//        }
//    }
//}