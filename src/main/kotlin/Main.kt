import dev.fastriver.fluko.engine.runFluko
import dev.fastriver.fluko.framework.element.BuildContext
import dev.fastriver.fluko.framework.geometrics.Axis
import dev.fastriver.fluko.framework.runApp
import dev.fastriver.fluko.framework.widget.layout.AspectRatio
import dev.fastriver.fluko.framework.widget.layout.Center
import dev.fastriver.fluko.framework.widget.paint.Opacity
import dev.fastriver.fluko.framework.widget.primitive.*

fun main() {
    println("Hello World!")

    runFluko(appMain = { appMain() }, windowHeight = 600)
}

fun appMain() {
    runApp(MainPage())
}

class MainPage : StatelessWidget() {
    private fun getColored(): Widget {
        return SizedBox(
            width = 300.0,
            height = 50.0,
            child = ColoredBox(
                child = null,
                color = 0xFFaa11cc.toInt()
            )
        )
    }

    private fun getOpacity(opacity: Double): Widget {
        return Opacity(
            child = getColored(),
            opacity = opacity
        )
    }

    override fun build(context: BuildContext): Widget {
        return Flex(
            children = List(10) { index ->
                getOpacity(index / 10.0)
            },
            direction = Axis.Vertical
        )
    }
}
