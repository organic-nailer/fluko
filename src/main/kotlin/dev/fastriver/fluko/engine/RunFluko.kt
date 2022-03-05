package dev.fastriver.fluko.engine

import dev.fastriver.fluko.framework.Engine

fun runFluko(
    appMain: () -> Unit,
    windowWidth: Int = 640,
    windowHeight: Int = 480
) {
    val taskRunners = TaskRunners(
        platformTaskRunner = TaskRunner("PlatformTaskRunner"),
        rasterTaskRunner = TaskRunner("RasterTaskRunner"),
        uiTaskRunner = TaskRunner("UITaskRunner"),
        ioTaskRunner = TaskRunner("IOTaskRunner")
    )

    println("task created")

    val glView = GLView(windowWidth, windowHeight)
    val shell = Shell(taskRunners, glView, null, windowWidth, windowHeight)
    shell.initRasterThread()

    shell.run {
        appMain()
    }

    while(!glView.windowShouldClose()) {
        shell.onVsync()
        glView.swapBuffers()

        glView.pollEvents()
    }
    taskRunners.terminateAll()
}