import dev.fastriver.fluko.common.EdgeInsets
import dev.fastriver.fluko.common.Offset
import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.engine.runFluko
import dev.fastriver.fluko.framework.element.BuildContext
import dev.fastriver.fluko.framework.geometrics.MainAxisAlignment
import dev.fastriver.fluko.framework.painting.BorderRadius
import dev.fastriver.fluko.framework.painting.BoxFit
import dev.fastriver.fluko.framework.render.TextSpan
import dev.fastriver.fluko.framework.render.clip.CustomClipper
import dev.fastriver.fluko.framework.runApp
import dev.fastriver.fluko.framework.widget.layout.Center
import dev.fastriver.fluko.framework.widget.layout.FittedBox
import dev.fastriver.fluko.framework.widget.layout.Padding
import dev.fastriver.fluko.framework.widget.layout.Row
import dev.fastriver.fluko.framework.widget.paint.ClipOval
import dev.fastriver.fluko.framework.widget.paint.ClipPath
import dev.fastriver.fluko.framework.widget.paint.ClipRRect
import dev.fastriver.fluko.framework.widget.primitive.*
import org.jetbrains.skia.Path

fun main() {
    println("Hello World!")

    runFluko(appMain = { appMain() })
}

fun appMain() {
    runApp(MainPage())
}

class MainPage : StatelessWidget() {
    override fun build(context: BuildContext): Widget {
        return Center(
            child = SizedBox(
                child = ColoredBox(
                    child = FittedBox(
                        child = SizedBox(
                            child = ColoredBox(
                                color = 0xFFFF0000.toInt(),
                            ),
                            width = 50.0, 50.0
                        ),
                        fit = BoxFit.Contain
                    ), color = 0xFF00FF00.toInt()
                ), width = 200.0, height = 100.0
            )
        )
    }
}
