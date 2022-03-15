package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.framework.geometrics.BoxConstraints

class RenderConstrainedBox(
    additionalConstraints: BoxConstraints
) : RenderProxyBox() {
    var additionalConstraints: BoxConstraints by MarkLayoutProperty(additionalConstraints)

    override fun performLayout() {
        if(child != null) {
            child!!.layout(additionalConstraints.enforce(constraints))
            size = child!!.size
        } else {
            size = additionalConstraints.enforce(constraints).constrain(size)
        }
    }
}