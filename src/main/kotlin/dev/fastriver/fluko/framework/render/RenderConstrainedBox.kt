package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.framework.geometrics.BoxConstraints

class RenderConstrainedBox(
    private val additionalConstraints: BoxConstraints
) : RenderProxyBox() {

    override fun performLayout() {
        if(child != null) {
            child!!.layout(additionalConstraints.enforce(constraints))
            size = child!!.size
        } else {
            size = additionalConstraints.enforce(constraints).constrain(size)
        }
    }
}