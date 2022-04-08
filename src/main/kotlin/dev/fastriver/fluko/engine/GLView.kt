package dev.fastriver.fluko.engine

import dev.fastriver.fluko.common.PointerEvent
import org.jetbrains.skia.DirectContext
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL

class GLView(
    width: Int, height: Int, private val delegate: GLViewDelegate
) {
    private var windowHandle: Long = -1
    private val pointerController: PointerController

    init {
        GLFW.glfwInit()
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)
        windowHandle = GLFW.glfwCreateWindow(width, height, "Fluko", 0, 0)
        GLFW.glfwSwapInterval(60)
        GLFW.glfwShowWindow(windowHandle)
        pointerController = PointerController(windowHandle) {
            delegate.onPointerEvent(it)
        }
    }

    fun windowShouldClose(): Boolean {
        return GLFW.glfwWindowShouldClose(windowHandle)
    }

    fun swapBuffers() {
        GLFW.glfwSwapBuffers(windowHandle)
    }

    fun pollEvents() {
        GLFW.glfwPollEvents()
    }

    fun createContext(): DirectContext {
        GLFW.glfwMakeContextCurrent(windowHandle)
        GL.createCapabilities()
        return DirectContext.makeGL()
    }

    fun setKeyCallback(callback: (window: Long, key: Int, code: Int, action: Int, mods: Int) -> Unit) {
        GLFW.glfwSetKeyCallback(windowHandle, callback)
    }

    interface GLViewDelegate {
        fun onPointerEvent(event: PointerEvent)
    }
}
