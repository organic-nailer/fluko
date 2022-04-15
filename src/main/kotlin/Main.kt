import dev.fastriver.fluko.engine.runFluko
import dev.fastriver.fluko.framework.element.BuildContext
import dev.fastriver.fluko.framework.geometrics.Axis
import dev.fastriver.fluko.framework.render.TextSpan
import dev.fastriver.fluko.framework.runApp
import dev.fastriver.fluko.framework.widget.primitive.*

fun main() {
    println("Hello World!")

    runFluko(appMain = { appMain() })
}

fun appMain() {
    runApp(HomePage())
}

class HomePage : StatefulWidget() {
    override fun createState(): State<*> = HomePageState()
}

class HomePageState : State<HomePage>() {
    private var count = 1

    override fun build(context: BuildContext): Widget {
        return Inherited(
            message = createMessage(),
            child = Flex(
                children = listOf(
                    Message(),
                    SizedBox(
                        width = 100.0, height = 100.0,
                        child = Listener(
                            child = ColoredBox(
                                child = null, color = 0xFF00FF00.toInt()
                            ),
                            onPointerUp = {
                                setState {
                                    count++
                                }
                            }
                        ),
                    )
                ),
                direction = Axis.Vertical
            )
        )
    }

    private fun createMessage(): String {
        val result = when {
            count % 15 == 0 -> "FizzBuzz"
            count % 3 == 0 -> "Fizz"
            count % 5 == 0 -> "Buzz"
            else -> count.toString()
        }
        return result
    }
}

class Message : StatelessWidget() {
    override fun build(context: BuildContext): Widget {
        return RichText(
            text = TextSpan(
                "Message: ${Inherited.of(context, listen = true).message}"
            )
        )
    }
}

class Inherited(
    val message: String, child: Widget
) : InheritedWidget(child) {
    companion object {
        fun of(context: BuildContext, listen: Boolean): Inherited {
            return if(listen) {
                context.dependOnInheritedWidgetOfExactType(Inherited::class)!!
            } else {
                context.getElementForInheritedWidgetOfExactType(Inherited::class)!!.widget as Inherited
            }
        }
    }

    override fun updateShouldNotify(oldWidget: InheritedWidget): Boolean = message != (oldWidget as Inherited).message
}
