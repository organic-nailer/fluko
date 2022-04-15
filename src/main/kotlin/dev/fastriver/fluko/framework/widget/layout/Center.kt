package dev.fastriver.fluko.framework.widget.layout

import dev.fastriver.fluko.framework.widget.primitive.Align
import dev.fastriver.fluko.framework.widget.primitive.Widget

class Center(
    widthFactor: Double? = null, heightFactor: Double? = null, child: Widget? = null
) : Align(child = child, widthFactor = widthFactor, heightFactor = heightFactor)
