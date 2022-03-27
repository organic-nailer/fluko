import dev.fastriver.fluko.engine.*
import dev.fastriver.fluko.framework.*
import dev.fastriver.fluko.framework.animation.AnimationController
import dev.fastriver.fluko.framework.animation.TickerProvider
import dev.fastriver.fluko.framework.animation.TickerProviderImpl
import dev.fastriver.fluko.framework.element.BuildContext
import dev.fastriver.fluko.framework.geometrics.Axis
import dev.fastriver.fluko.framework.render.TextSpan
import kotlin.time.Duration.Companion.seconds

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

class MyStatefulWidgetState: State<MyStatefulWidget>(), TickerProvider by TickerProviderImpl() {
    private val animationController = AnimationController(
        initialValue = 1.0,
        duration = 1.seconds,
        vsync = this
    )
    private var isForward = false

    private fun animate() {
        if(isForward) {
            animationController.forward(0.0)
            isForward = false
        }
        else {
            animationController.reverse(1.0)
            isForward = true
        }
    }

    override fun build(context: BuildContext): Widget {
        return SizedBox(
            child = Listener(
                child = FadeTransition(
                    opacity = animationController,
                    child = ColoredBox(
                        child = null,
                        color = 0xFF00FF00.toInt()
                    ),
                ),
                onPointerUp = {
                    animate()
                },
            ),
            width = 100.0,
            height = 100.0
        )
    }
}
