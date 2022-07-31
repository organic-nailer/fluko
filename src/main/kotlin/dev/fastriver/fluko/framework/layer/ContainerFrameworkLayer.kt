package dev.fastriver.fluko.framework.layer

import kotlin.reflect.KProperty

open class ContainerFrameworkLayer : FrameworkLayer() {
    private val childrenInternal: MutableList<FrameworkLayer> = mutableListOf()
    val children: List<FrameworkLayer>
        get() = childrenInternal

    fun append(child: FrameworkLayer) {
        adoptChild(child)
        childrenInternal.add(child)
    }

    fun removeChild(child: FrameworkLayer) {
        childrenInternal.remove(child)
        child.parent = null
    }

    fun removeAllChildren() {
        for(layer in childrenInternal) {
            dropChild(layer)
        }
        childrenInternal.clear()
    }

    override fun addToScene(builder: UISceneBuilder) {
        addChildrenToScene(builder)
    }

    fun addChildrenToScene(builder: UISceneBuilder) {
        for(child in children) {
            child.addToSceneWithRetainedRendering(builder)
        }
    }

    override fun updateSubtreeNeedsAddToScene() {
        super.updateSubtreeNeedsAddToScene()
        for(child in children) {
            child.updateSubtreeNeedsAddToScene()
            needsAddToScene = needsAddToScene || child.needsAddToScene
        }
    }
}

class MarkAddToSceneProperty<T>(initialValue: T) {
    var child: T = initialValue
    operator fun getValue(thisRef: FrameworkLayer, property: KProperty<*>): T {
        return child
    }

    operator fun setValue(thisRef: FrameworkLayer, property: KProperty<*>, value: T) {
        if(child == value) return
        child = value
        thisRef.markNeedsAddToScene()
    }
}
