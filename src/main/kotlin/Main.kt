import dev.fastriver.fluko.engine.*
import dev.fastriver.fluko.framework.*
import dev.fastriver.fluko.framework.geometrics.Axis
import dev.fastriver.fluko.framework.render.TextSpan

fun main(args: Array<String>) {
    println("Hello World!")

    runFluko(appMain = { appMain() })
}

fun appMain() {
    runApp(
        Align(
            child = Flex(
                children = listOf(
                    SizedBox(
                        child = ColoredBox(
                            child = null,
                            color = 0xFFFF0000.toInt()
                        ),
                        width = 100.0,
                        height = 100.0
                    ),
                    SizedBox(
                        child = RichText(
                            text = TextSpan("Hello Hello Hello Hello, Fluko!")
                        ),
                        width = 200.0
                    ),
                    SizedBox(
                        child = ColoredBox(
                            child = null,
                            color = 0xFF0000FF.toInt()
                        ),
                        width = 200.0,
                        height = 50.0

                    ),
                ),
                direction = Axis.Vertical
            )
        )
    )
}
