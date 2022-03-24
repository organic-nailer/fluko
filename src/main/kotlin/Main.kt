import dev.fastriver.fluko.engine.*
import dev.fastriver.fluko.framework.*
import dev.fastriver.fluko.framework.element.BuildContext
import dev.fastriver.fluko.framework.geometrics.Axis
import dev.fastriver.fluko.framework.render.TextSpan

fun main(args: Array<String>) {
    println("Hello World!")

    runFluko(appMain = { appMain() })
}

fun appMain() {
    runApp(MyApp())
}

class MyApp: StatelessWidget() {
    override fun build(context: BuildContext): Widget {
        return Align(
            child = Flex(
                children = listOf(
                    MyStatefulWidget(),
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
    }
}

class MyStatefulWidget: StatefulWidget() {
    override fun createState(): MyStatefulWidgetState = MyStatefulWidgetState()
}

class MyStatefulWidgetState: State<MyStatefulWidget>() {
    private var isGreen = false
    override fun build(context: BuildContext): Widget {
        return SizedBox(
            child = Listener(
                child = ColoredBox(
                    child = null,
                    color = if(isGreen) 0xFF00FF00.toInt() else 0xFFFF0000.toInt()
                ),
                onPointerUp = {
                    println("PointerUp  : ${it.position}")
                    setState {
                        isGreen = !isGreen
                    }
                },
            ),
            width = 100.0,
            height = 100.0
        )
    }
}
