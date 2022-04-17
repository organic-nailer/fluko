import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.engine.runFluko
import dev.fastriver.fluko.framework.element.BuildContext
import dev.fastriver.fluko.framework.geometrics.MainAxisAlignment
import dev.fastriver.fluko.framework.painting.BorderRadius
import dev.fastriver.fluko.framework.render.clip.CustomClipper
import dev.fastriver.fluko.framework.runApp
import dev.fastriver.fluko.framework.widget.layout.Row
import dev.fastriver.fluko.framework.widget.paint.ClipOval
import dev.fastriver.fluko.framework.widget.paint.ClipPath
import dev.fastriver.fluko.framework.widget.paint.ClipRRect
import dev.fastriver.fluko.framework.widget.primitive.ColoredBox
import dev.fastriver.fluko.framework.widget.primitive.SizedBox
import dev.fastriver.fluko.framework.widget.primitive.StatelessWidget
import dev.fastriver.fluko.framework.widget.primitive.Widget
import org.jetbrains.skia.Path

fun main() {
    println("Hello World!")

    runFluko(appMain = { appMain() })
}

fun appMain() {
    runApp(ClipPage())
}

class ClipPage : StatelessWidget() {
    override fun build(context: BuildContext): Widget {
        return Row(
            children = listOf(
                ClipPath(
                    clipper = ArcClipper(), child = SizedBox(
                        width = 100.0, height = 100.0, child = ColoredBox(color = 0xFFFF0000.toInt(), child = null)
                    )
                ),
                SizedBox(width = 20.0, child = null), ClipRRect(
                    borderRadius = BorderRadius.circular(20.0), child = SizedBox(
                        width = 100.0, height = 100.0, child = ColoredBox(color = 0xFF0000FF.toInt(), child = null)
                    )
                ),
                SizedBox(width = 20.0, child = null), ClipOval(
                    child = SizedBox(
                        width = 100.0, height = 150.0, child = ColoredBox(color = 0xFF00FF00.toInt(), child = null)
                    )
                )
            ), mainAxisAlignment = MainAxisAlignment.Center
        )
    }
}

class ArcClipper : CustomClipper<Path>() {
    override fun getClip(size: Size): Path {
        return Path().apply {
            lineTo(0f, size.height.toFloat() - 30f)

            val firstControlPoint = Offset(size.width / 4, size.height)
            val firstPoint = Offset(size.width / 2, size.height)
            quadTo(
                firstControlPoint.dx.toFloat(),
                firstControlPoint.dy.toFloat(),
                firstPoint.dx.toFloat(),
                firstPoint.dy.toFloat()
            )

            val secondControlPoint = Offset(size.width - size.width / 4, size.height)
            val secondPoint = Offset(size.width, size.height - 30)
            quadTo(
                secondControlPoint.dx.toFloat(),
                secondControlPoint.dy.toFloat(),
                secondPoint.dx.toFloat(),
                secondPoint.dy.toFloat()
            )

            lineTo(size.width.toFloat(), 0f)
            closePath()
        }
    }

    override fun shouldReclip(oldClipper: CustomClipper<Path>): Boolean = false
}
