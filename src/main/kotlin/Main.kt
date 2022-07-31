import dev.fastriver.fluko.engine.runFluko
import dev.fastriver.fluko.framework.element.BuildContext
import dev.fastriver.fluko.framework.geometrics.Alignment
import dev.fastriver.fluko.framework.runApp
import dev.fastriver.fluko.framework.widget.layout.Center
import dev.fastriver.fluko.framework.widget.layout.Positioned
import dev.fastriver.fluko.framework.widget.layout.Stack
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
            width = 120.0,
            height = 120.0,
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
        return Center(
            child = SizedBox(
                width = 360.0, height = 360.0,
                child = Stack(
                    children = listOf(
                        getOpacity(0.1),
                        Positioned(
                            left = 120.0,
                            top = 0.0,
                            child = getOpacity(0.2)
                        ),
                        Align(
                            alignment = Alignment.topRight,
                            child = getOpacity(0.3)
                        ),
                        Positioned(
                            top = 120.0,
                            height = 120.0,
                            child = getOpacity(0.4)
                        ),
                        Positioned(
                            top = 120.0, left = 120.0, right = 120.0, bottom = 120.0,
                            child = getOpacity(0.5)
                        ),
                        Align(
                            alignment = Alignment.centerRight,
                            child = getOpacity(0.6)
                        ),
                        Positioned(
                            left = 0.0,
                            bottom = 0.0,
                            child = getOpacity(0.7)
                        ),
                        Positioned(
                            right = 120.0,
                            bottom = 0.0,
                            child = getOpacity(0.8)
                        ),
                        Positioned(
                            right = 0.0,
                            bottom = 0.0,
                            child = getOpacity(0.9)
                        )
                    )
                )
            )
        )
    }
}
