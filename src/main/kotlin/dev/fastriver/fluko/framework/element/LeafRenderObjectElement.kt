package dev.fastriver.fluko.framework.element

import dev.fastriver.fluko.framework.LeafRenderObjectWidget
import dev.fastriver.fluko.framework.render.RenderObject

class LeafRenderObjectElement<T: RenderObject>(widget: LeafRenderObjectWidget<T>) : RenderObjectElement<T>(widget)