package dev.fastriver.fluko.engine

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
        // 垂直同期がよくわからないので30ミリ秒ごとにvsyncを呼ぶことにする
        Thread.sleep(30)
        shell.onVsync()

        glView.pollEvents()
    }
    taskRunners.terminateAll()
}