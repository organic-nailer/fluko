package dev.fastriver.fluko.framework.widget.layout

import dev.fastriver.fluko.framework.geometrics.*
import dev.fastriver.fluko.framework.widget.primitive.Flex
import dev.fastriver.fluko.framework.widget.primitive.Widget

class Column(
    mainAxisAlignment: MainAxisAlignment = MainAxisAlignment.Start,
    mainAxisSize: MainAxisSize = MainAxisSize.Max,
    crossAxisAlignment: CrossAxisAlignment = CrossAxisAlignment.Center,
    verticalDirection: VerticalDirection = VerticalDirection.Down,
    children: List<Widget> = listOf()
) : Flex(
    children, Axis.Vertical, mainAxisAlignment, mainAxisSize, crossAxisAlignment, verticalDirection
)