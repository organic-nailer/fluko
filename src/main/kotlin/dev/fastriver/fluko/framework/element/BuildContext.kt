package dev.fastriver.fluko.framework.element

import dev.fastriver.fluko.framework.InheritedWidget
import dev.fastriver.fluko.framework.Widget
import kotlin.reflect.KClass

interface BuildContext {
    val widget: Widget
    var owner: BuildOwner?

    fun <T: InheritedWidget> dependOnInheritedWidgetOfExactType(type: KClass<T>): T?

    fun <T: InheritedWidget> getElementForInheritedWidgetOfExactType(type: KClass<T>): InheritedElement?
}