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

    val renderPipeline = RenderPipeline().apply {
        renderView = RenderView(width.toDouble(),height.toDouble())
    }

    val shell = Shell(taskRunners, glView, null, renderPipeline, width, height)

    shell.initRasterThread()

    shell.drawFrame()

    var keyPressed = false

    glView.setKeyCallback { _, key, _, action, _ ->
        if(key == GLFW_KEY_M && action == GLFW_PRESS) {
            keyPressed = true
        }
    }

    while(!shell.glView.windowShouldClose()) {
        if(keyPressed) {
            keyPressed = false
            renderPipeline.renderView!!.child = RenderPositionedBox(
                child = RenderFlex(
                    children = listOf(
                        RenderConstrainedBox(
                            additionalConstraints = BoxConstraints.tight(Size(100.0, 100.0)),
                            child = RenderColoredBox(0xFFFF0000.toInt())
                        ),
                        RenderConstrainedBox(
                            additionalConstraints = BoxConstraints.tight(Size(200.0, 50.0)),
                            child = RenderColoredBox(0xFF0000FF.toInt())
                        ),
                    )
                )
            )
            shell.drawFrame()
        }
        shell.glView.pollEvents()
    }
    shell.taskRunners.terminateAll()
}
