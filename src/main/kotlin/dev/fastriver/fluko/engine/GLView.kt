package dev.fastriver.fluko.engine

import dev.fastriver.fluko.common.KeyEvent
import dev.fastriver.fluko.common.PointerEvent
import org.jetbrains.skia.DirectContext
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL

class GLView(
    width: Int, height: Int, private val delegate: GLViewDelegate
) {
    private var windowHandle: Long = -1
    private val pointerController: PointerController
    private val keyboardController: KeyboardController

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
        keyboardController = KeyboardController(windowHandle) {
            if(!checkWindowClose(it))
                delegate.onKeyEvent(it)
        }
        GLFW.glfwSetWindowSizeCallback(windowHandle) { _, width, height ->
            delegate.onWindowMetricsChanged(width, height)
        }
    }

    private fun checkWindowClose(event: KeyEvent): Boolean {
        if(event.logicalKeyboardKey == GLFW.GLFW_KEY_ESCAPE && event.phase == KeyEvent.KeyEventPhase.KeyDown) {
            GLFW.glfwSetWindowShouldClose(windowHandle, true)
            return true
        }
        return false
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

    interface GLViewDelegate {
        fun onPointerEvent(event: PointerEvent)

        fun onKeyEvent(event: KeyEvent)

        fun onWindowMetricsChanged(width: Int, height: Int)
    }
}
