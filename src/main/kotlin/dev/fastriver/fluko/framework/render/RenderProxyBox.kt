package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.framework.geometrics.BoxConstraints

abstract class RenderProxyBox : RenderBox(), RenderObjectWithChildMixin<RenderBox> {
    override var child: RenderBox? = null
    override fun layout(constraints: BoxConstraints) {
        if(child != null) {
            child!!.layout(constraints)
            size = child!!.size
        } else {
            size = constraints.smallest
        }
    }
}