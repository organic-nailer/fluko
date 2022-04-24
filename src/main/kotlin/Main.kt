import dev.fastriver.fluko.engine.runFluko
import dev.fastriver.fluko.framework.element.BuildContext
import dev.fastriver.fluko.framework.runApp
import dev.fastriver.fluko.framework.widget.layout.AspectRatio
import dev.fastriver.fluko.framework.widget.layout.Center
import dev.fastriver.fluko.framework.widget.primitive.ColoredBox
import dev.fastriver.fluko.framework.widget.primitive.StatelessWidget
import dev.fastriver.fluko.framework.widget.primitive.Widget

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
            child = AspectRatio(
                aspectRatio = 16.0 / 9.0,
                child = ColoredBox(
                    color = 0xFF00FFFF.toInt(),
                )
            )
        )
    }
}
