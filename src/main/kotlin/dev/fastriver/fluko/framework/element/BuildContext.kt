package dev.fastriver.fluko.framework.element

import dev.fastriver.fluko.framework.Widget

interface BuildContext {
    val widget: Widget
    var owner: BuildOwner?
}