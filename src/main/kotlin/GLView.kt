import org.jetbrains.skija.DirectContext
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL

class GLView(private val width: Int, private val height: Int) {
    private var windowHandle: Long = -1
    init {
        GLFW.glfwInit()
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)
        windowHandle = GLFW.glfwCreateWindow(width, height, "Skija Sample", 0, 0)
        GLFW.glfwSwapInterval(1)
        GLFW.glfwShowWindow(windowHandle)
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
}
