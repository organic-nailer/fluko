package dev.fastriver.fluko.framework.widget.primitive

import dev.fastriver.fluko.framework.geometrics.*
import dev.fastriver.fluko.framework.render.RenderFlex

open class Flex(
    children: List<Widget> = listOf(),
    val direction: Axis,
    val mainAxisAlignment: MainAxisAlignment = MainAxisAlignment.Start,
    val mainAxisSize: MainAxisSize = MainAxisSize.Max,
    val crossAxisAlignment: CrossAxisAlignment = CrossAxisAlignment.Center,
    val verticalDirection: VerticalDirection = VerticalDirection.Down
) : MultiChildRenderObjectWidget<RenderFlex>(children) {
    override fun createRenderObject(): RenderFlex {
        return RenderFlex(
            direction, mainAxisAlignment, mainAxisSize, crossAxisAlignment, verticalDirection
        )
    }

    override fun updateRenderObject(renderObject: RenderFlex) {
        renderObject.let {
            it.direction = direction
            it.mainAxisAlignment = mainAxisAlignment
            it.mainAxisSize = mainAxisSize
            it.crossAxisAlignment = crossAxisAlignment
            it.verticalDirection = verticalDirection
        }
    }
}