package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.framework.PaintingContext
import dev.fastriver.fluko.framework.geometrics.BoxConstraints

class RenderConstrainedBox(
    private val additionalConstraints: BoxConstraints
) : RenderProxyBox() {

    override fun layout(constraints: BoxConstraints) {
        if(child != null) {
            child!!.layout(additionalConstraints.enforce(constraints))
            size = child!!.size
        } else {
            size = additionalConstraints.enforce(constraints).constrain(size)
        }
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        child?.paint(context, offset)
    }
}