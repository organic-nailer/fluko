import org.jetbrains.skija.Color
import org.jetbrains.skija.Paint
import org.jetbrains.skija.Rect
import kotlin.math.max

abstract class RenderObject {
    var parentData: ParentData? = null

    abstract var size: Size

    abstract fun layout(constraints: BoxConstraints)

    abstract fun paint(context: PaintingContext, offset: Offset)
}

abstract class RenderBox : RenderObject() {
    override var size: Size = Size.zero
}

class RenderConstrainedBox(
    private val additionalConstraints: BoxConstraints,
    val child: RenderBox?
) : RenderBox() {

    override fun layout(constraints: BoxConstraints) {
        if(child != null) {
            child.layout(additionalConstraints.enforce(constraints))
            size = child.size
        } else {
            size = additionalConstraints.enforce(constraints).constrain(size)
        }
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        child?.paint(context, offset)
    }
}

abstract class RenderProxyBox : RenderBox() {
    var child: RenderBox? = null
    override fun layout(constraints: BoxConstraints) {
        if(child != null) {
            child!!.layout(constraints)
            size = child!!.size
        } else {
            size = constraints.smallest
        }
    }
}

class RenderColoredBox(val color: Int) : RenderProxyBox() {
    override fun paint(context: PaintingContext, offset: Offset) {
        if(size.width != 0.0 && size.height != 0.0) {
            context.canvas.drawRect(
                size.and(offset), Paint().also { it.color = color })
        }
    }
}

class RenderPositionedBox(
    val child: RenderBox? = null,
    val widthFactor: Double? = null,
    val heightFactor: Double? = null,
    val alignment: Alignment = Alignment.center
) : RenderBox() {
    init {
        child?.parentData = BoxParentData()
    }
    override fun layout(constraints: BoxConstraints) {
        val shrinkWrapWidth = widthFactor != null || constraints.maxWidth == Double.POSITIVE_INFINITY
        val shrinkWrapHeight = heightFactor != null || constraints.maxHeight == Double.POSITIVE_INFINITY

        if(child != null) {
            child.layout(constraints.loosen())
            size = constraints.constrain(Size(
                if(shrinkWrapWidth) child.size.width * (widthFactor ?: 0.0) else Double.POSITIVE_INFINITY,
                if(shrinkWrapHeight) child.size.height * (heightFactor ?: 0.0) else Double.POSITIVE_INFINITY,
            ))
            alignChild()
        }
        else {
            size = constraints.constrain(Size(
                if(shrinkWrapWidth) 0.0 else Double.POSITIVE_INFINITY,
                if(shrinkWrapHeight) 0.0 else Double.POSITIVE_INFINITY
            ))
        }
    }

    /// alignmentに沿うように子のoffsetを決定する
    private fun alignChild() {
        val childParentData = child!!.parentData as BoxParentData
        childParentData.offset = alignment.computeOffset(size, child.size)
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        if(child != null) {
            val childParentData = child.parentData as BoxParentData
            child.paint(context, childParentData.offset + offset)
        }
    }
}

class RenderFlex(
    val direction: Axis = Axis.Vertical,
    val mainAxisAlignment: MainAxisAlignment = MainAxisAlignment.Start,
    val mainAxisSize: MainAxisSize = MainAxisSize.Max,
    val crossAxisAlignment: CrossAxisAlignment = CrossAxisAlignment.Center,
    val verticalDirection: VerticalDirection = VerticalDirection.Down,
    val children: List<RenderBox> = listOf()
) : RenderBox() {
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
        // MainAxisAlignment.startなので最初の余白も間もなし
        val leadingSpace = 0.0
        val betweenSpace = 0.0

        var childMainPosition = leadingSpace
        for(child in children) {
            val childParentData = child.parentData as BoxParentData
            // CrossAxisAlignment.centerなので幅の半分を引いたものが余白
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

enum class Axis {
    Horizontal, Vertical
}

enum class MainAxisAlignment {
    Start, End, Center, SpaceBetween, SpaceAround, SpaceEvenly
}

enum class MainAxisSize { Max, Min }

enum class CrossAxisAlignment { Start, End, Center, Stretch, Baseline }

enum class VerticalDirection { Up, Down }

class RenderView(width: Double, height: Double) : RenderObject() {
    override var size: Size = Size(width, height)
    var child: RenderBox? = null
    val layer: ContainerLayer = TransformLayer()
    override fun layout(constraints: BoxConstraints) {
        throw NotImplementedError()
    }

    fun performLayout() {
        child?.layout(BoxConstraints.tight(size))
    }

    override fun paint(context: PaintingContext, offset: Offset) {
        if(child != null) {
            child!!.paint(context, offset)
        }
    }
}

class BoxConstraints(
    val minWidth: Double = 0.0,
    val maxWidth: Double = Double.POSITIVE_INFINITY,
    val minHeight: Double = 0.0,
    val maxHeight: Double = Double.POSITIVE_INFINITY
) {
    companion object {
        fun tight(size: Size): BoxConstraints {
            return BoxConstraints(size.width, size.width, size.height, size.height)
        }

        fun tightFor(width: Double?, height: Double?): BoxConstraints {
            return BoxConstraints(
                width ?: 0.0, width ?: Double.POSITIVE_INFINITY,
                height ?: 0.0, height ?: Double.POSITIVE_INFINITY
            )
        }
    }

    /// 与えられた制約を満たすよう現在の制約を修正する
    fun enforce(constraints: BoxConstraints): BoxConstraints {
        return BoxConstraints(
            minWidth.coerceIn(constraints.minWidth..constraints.maxWidth),
            maxWidth.coerceIn(constraints.minWidth..constraints.maxWidth),
            minHeight.coerceIn(constraints.minHeight..constraints.maxHeight),
            maxHeight.coerceIn(constraints.minHeight..constraints.maxHeight),
        )
    }

    fun constrainWidth(width: Double): Double {
        return width.coerceIn(minWidth..maxWidth)
    }

    fun constrainHeight(height: Double): Double {
        return height.coerceIn(minHeight..maxHeight)
    }

    /// 制約内に修正したサイズを返す
    fun constrain(size: Size): Size {
        return Size(constrainWidth(size.width), constrainHeight(size.height))
    }

    /// 最大大きさのみ指定
    fun loosen() = BoxConstraints(maxWidth = maxWidth, maxHeight = maxHeight)

    val smallest: Size = Size(constrainWidth(0.0), constrainHeight(0.0))
}

abstract class OffsetBase(
    private val dx: Double,
    private val dy: Double
) {
    val isInfinite: Boolean
        get() = dx >= Double.POSITIVE_INFINITY || dy >= Double.POSITIVE_INFINITY

    val isFinite: Boolean
        get() = dx.isFinite() && dy.isFinite()
}

class Size(
    val width: Double,
    val height: Double
) : OffsetBase(width, height) {
    companion object {
        val zero = Size(0.0, 0.0)
    }

    val isEmpty = width <= 0.0 || height <= 0.0

    operator fun minus(other: OffsetBase): OffsetBase {
        if(other is Offset) {
            return Size(width - other.dx, height - other.dy)
        }
        if(other is Size) {
            return Offset(width - other.width, height - other.height)
        }
        throw IllegalArgumentException()
    }

    fun and(other: Offset): Rect {
        return Rect.makeXYWH(other.dx.toFloat(),other.dy.toFloat(),width.toFloat(),height.toFloat())
    }
}

class Offset(val dx: Double, val dy: Double): OffsetBase(dx,dy) {
    companion object {
        val zero = Offset(0.0,0.0)
    }

    operator fun plus(other: Offset): Offset {
        return Offset(dx+other.dx,dy+other.dy)
    }
}

class Alignment(val x: Double, val y: Double) {
    companion object {
        val topLeft = Alignment(-1.0, -1.0)
        val topCenter = Alignment(0.0, -1.0)
        val topRight = Alignment(1.0, -1.0)
        val centerLeft = Alignment(-1.0, 0.0)
        val center = Alignment(0.0, 0.0)
        val centerRight = Alignment(1.0, 0.0)
        val bottomLeft = Alignment(-1.0, 1.0)
        val bottomCenter = Alignment(0.0, 1.0)
        val bottomRight = Alignment(1.0, 1.0)
    }

    private fun alongOffset(other: Offset): Offset {
        val centerX = other.dx / 2.0
        val centerY = other.dy / 2.0
        return Offset((1+x) * centerX, (1+y) * centerY)
    }

    fun computeOffset(parentSize: Size, childSize: Size): Offset {
        val offsetIfCenter = (parentSize - childSize) as Offset
        return alongOffset(offsetIfCenter)
    }
}

interface ParentData

data class BoxParentData(
    var offset: Offset = Offset.zero
): ParentData
