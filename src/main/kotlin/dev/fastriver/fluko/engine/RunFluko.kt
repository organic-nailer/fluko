package dev.fastriver.fluko.engine

fun runFluko(
    appMain: () -> Unit, windowWidth: Int = 640, windowHeight: Int = 480
) {
    val taskRunners = TaskRunners(
        platformTaskRunner = TaskRunner("PlatformTaskRunner"),
        rasterTaskRunner = TaskRunner("RasterTaskRunner"),
        uiTaskRunner = TaskRunner("UITaskRunner"),
        ioTaskRunner = TaskRunner("IOTaskRunner")
    )

    println("task created")

    val shell = Shell(taskRunners, null, windowWidth, windowHeight)
    shell.initRasterThread()

    shell.run {
        appMain()
    }

    while(!shell.glView.windowShouldClose()) {
        // 垂直同期がよくわからないので30ミリ秒ごとにvsyncを呼ぶことにする
        Thread.sleep(30)
        shell.onVsync()

        shell.glView.pollEvents()
    }
    taskRunners.terminateAll()
}