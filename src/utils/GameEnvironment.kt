package utils

abstract class GameEnvironment {
    protected val spriteList: MutableList<Sprite> = mutableListOf()

    open fun update(deltaTime: Float) {
        spriteList.removeAll { sprite -> sprite.isDestroyed }

        for (sprite in spriteList) {
            sprite.update(deltaTime)
        }
    }

    abstract fun repaint()

    fun addSprite(sprite: Sprite) {
        spriteList.add(sprite)
    }

    abstract fun onAction(action: Action)
}