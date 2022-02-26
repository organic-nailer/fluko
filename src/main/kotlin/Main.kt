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

    val shell = Shell(taskRunners, glView, null)

    taskRunners.rasterTaskRunner.postTask {
        println("in rasterThread")
        val context = shell.glView.createContext()
        val rasterizer = Rasterizer(width, height, context)
        shell.rasterizer = rasterizer
    }

    var keyPressed = false

    glView.setKeyCallback { _, key, _, action, _ ->
        if(key == GLFW_KEY_M && action == GLFW_PRESS) {
            keyPressed = true
        }
    }

    while(!shell.glView.windowShouldClose()) {
        if(keyPressed) {
            keyPressed = false
            shell.taskRunners.rasterTaskRunner.postTask {
                shell.rasterizer!!.drawToSurface()
                shell.glView.swapBuffers()
            }
        }
        shell.glView.pollEvents()
    }
    shell.taskRunners.terminateAll()
}