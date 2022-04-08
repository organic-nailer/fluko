package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset

interface ParentData

data class BoxParentData(
    var offset: Offset = Offset.zero
) : ParentData