package merge_balls

import utils.Action
import utils.GameEnvironment

class Environment: GameEnvironment() {
    private val spriteGrid = SpriteGrid(10, 10)

    override fun repaint() {
        println("\n=== Sprites ===")
        for ((index, sprite) in spriteList.withIndex()) {
            println("Sprite $index: Position(${sprite.x}, ${sprite.y}) Velocity(${sprite.vx}, ${sprite.vy}) Acceleration(${sprite.ax}, ${sprite.ay})")
        }
    }

    override fun onAction(action: Action) {
        TODO("Not yet implemented")
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        syncGridToSpriteList()
        addRandomBox()
    }

    fun addRandomBox() {
        val randomX = (Math.random() * 10).toInt()
        val randomY = (Math.random() * 10).toInt()
        val box = Box(x = randomX.toFloat(), y = randomY.toFloat(), value = 2)
        if (spriteGrid.place(box, randomX, randomY)) {
            addSprite(box)
        }
    }

    fun syncGridToSpriteList() {
        spriteList.clear()
        spriteList.addAll(spriteGrid.getAllSprites())
    }
}