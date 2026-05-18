package merge_balls

import utils.Action
import utils.GameEnvironment

class Environment(
    private val view: MergeBallsView,
    private val spriteGrid: SpriteGrid
) : GameEnvironment() {
    private val gridSize = 10
    private val currentBox: Box = getRandomBox()

    override fun repaint() {
        view.refresh()
    }

    override fun onAction(action: Action) {
        TODO("Implement Movements")
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        syncGridToSpriteList()
    }

    private fun getRandomBox(): Box {
        val randomX = (Math.random() * gridSize).toInt()
        val randomY = (Math.random() * gridSize).toInt()
        val box = Box(x = randomX.toFloat(), y = randomY.toFloat(), value = 2)
        if (spriteGrid.place(box, randomX, randomY)) {
            addSprite(box)
        }
        return box
    }

    private fun syncGridToSpriteList() {
        spriteList.clear()
        spriteList.addAll(spriteGrid.getAllSprites())
    }
}
