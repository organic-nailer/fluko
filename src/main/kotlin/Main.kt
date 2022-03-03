import dev.fastriver.fluko.common.Size
import dev.fastriver.fluko.engine.*
import dev.fastriver.fluko.framework.*
import org.lwjgl.glfw.GLFW.GLFW_KEY_M
import org.lwjgl.glfw.GLFW.GLFW_PRESS

fun main(args: Array<String>) {
    println("Hello World!")

    val width = 640
    val height = 480

    val taskRunners = TaskRunners(
        platformTaskRunner = TaskRunner(),
        rasterTaskRunner = TaskRunner(),
        uiTaskRunner = TaskRunner(),
        ioTaskRunner = TaskRunner()
    )

    println("task created")

    val glView = GLView(width, height)
    val shell = Shell(taskRunners, glView, null, width, height)
    shell.initRasterThread()

    var keyPressed = false

    glView.setKeyCallback { _, key, _, action, _ ->
        if(key == GLFW_KEY_M && action == GLFW_PRESS) {
            keyPressed = true
        }
    }

    var stretch = 0

    while(!shell.glView.windowShouldClose()) {
        if(keyPressed) {
            keyPressed = false
            runApp(shell,
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
                                child = ColoredBox(
                                    child = null,
                                    color = 0xFF0000FF.toInt()
                                ),
                                width = 200.0,
                                height = 50.0 + stretch++
                            ),
                        ),
                        direction = Axis.Vertical
                    )
                )
            )
//            renderPipeline.renderView!!.child = RenderPositionedBox(
//                child = RenderFlex(
//                    children = listOf(
//                        RenderConstrainedBox(
//                            additionalConstraints = BoxConstraints.tight(Size(100.0, 100.0)),
//                            child = RenderColoredBox(0xFFFF0000.toInt())
//                        ),
//                        RenderConstrainedBox(
//                            additionalConstraints = BoxConstraints.tight(Size(200.0, 50.0)),
//                            child = RenderColoredBox(0xFF0000FF.toInt())
//                        ),
//                    )
//                )
//            )
//            shell.drawFrame()
        }
        shell.glView.pollEvents()
    }
    shell.taskRunners.terminateAll()
}
