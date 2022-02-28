import org.jetbrains.skija.Canvas
import org.jetbrains.skija.Paint
import org.jetbrains.skija.PictureRecorder
import org.jetbrains.skija.Rect
import org.lwjgl.glfw.GLFW.GLFW_KEY_M
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import kotlin.random.Random

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

//    taskRunners.rasterTaskRunner.postTask {
//        println("in rasterThread")
//        val context = shell.glView.createContext()
//        val rasterizer = Rasterizer(width, height, context)
//        shell.rasterizer = rasterizer
//    }

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

fun createRandomTree(width: Float, height: Float): LayerTree {
    val root = ContainerLayer()
    //val rect = Rect.fromLTWH(0.0,0.0,width,height)
    val rect = Rect.makeXYWH(0f,0f,width, height)
    val leaf = PictureLayer(rect)
    val recorder = PictureRecorder()
    val canvas = recorder.beginRecording(rect)

        val paint = Paint().apply { color = 0xFFFF0000.toInt() }

        val randomX = Random.nextFloat() * width
        val randomY = Random.nextFloat() * height

        canvas.drawCircle(randomX, randomY, 40f, paint)

    leaf.picture = recorder.finishRecordingAsPicture()
    root.children.add(leaf)
    return LayerTree().apply {
        rootLayer = root
    }
}