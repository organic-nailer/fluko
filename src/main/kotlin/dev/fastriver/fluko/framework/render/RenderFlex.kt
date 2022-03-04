package dev.fastriver.fluko.framework.render

import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.framework.*
import dev.fastriver.fluko.framework.geometrics.*
import kotlin.math.max

class RenderFlex(
    val direction: Axis = Axis.Vertical,
    val mainAxisAlignment: MainAxisAlignment = MainAxisAlignment.Start,
    val mainAxisSize: MainAxisSize = MainAxisSize.Max,
    val crossAxisAlignment: CrossAxisAlignment = CrossAxisAlignment.Center,
    val verticalDirection: VerticalDirection = VerticalDirection.Down
) : RenderBox(), ContainerRenderObjectMixin<RenderBox> {
    override val children: MutableList<RenderBox> = mutableListOf()
    init {
        children.forEach {
            it.parentData = BoxParentData()
        }
    }
    override fun layout(constraints: BoxConstraints) {
        // まずはデフォルトのみ動作、Flexなし
        val maxMainSize = constraints.maxHeight
        var crossSize = 0.0
        // 子のサイズの合計(メイン軸)
        var allocatedSize = 0.0
        for(child in children) {
            // 子に渡す制約は交差軸の最大大きさのみ
            val innerConstraints = BoxConstraints(maxWidth = constraints.maxWidth)
            child.layout(innerConstraints)
            val childSize = child.size
            allocatedSize += childSize.height
            crossSize = max(crossSize, childSize.width)
        }
        var idealMainSize = maxMainSize

        // 自身のサイズを合わせる
        size = constraints.constrain(Size(crossSize, idealMainSize))
        idealMainSize = size.height
        crossSize = size.width

        // 余った幅を算出
        val remainingSpace = max(0.0, idealMainSize - allocatedSize)
        // dev.fastriver.fluko.framework.MainAxisAlignment.startなので最初の余白も間もなし
        val leadingSpace = 0.0
        val betweenSpace = 0.0

        var childMainPosition = leadingSpace
        for(child in children) {
            val childParentData = child.parentData as BoxParentData
            // dev.fastriver.fluko.framework.CrossAxisAlignment.centerなので幅の半分を引いたものが余白
            val childCrossPosition = crossSize / 2.0 - child.size.width / 2.0

            // 子のOffsetを決定
            childParentData.offset = Offset(childCrossPosition, childMainPosition)

            // 次の子の開始位置を更新
            childMainPosition += child.size.height + betweenSpace
        }
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        for(child in children) {
            val childParentData = child.parentData as BoxParentData
            child.paint(context, childParentData.offset + offset)
        }
    }
}