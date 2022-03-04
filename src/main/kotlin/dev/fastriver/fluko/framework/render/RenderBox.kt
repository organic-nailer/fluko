package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Size

abstract class RenderBox : RenderObject() {
    override var size: Size = Size.zero
}