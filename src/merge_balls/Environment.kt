package merge_balls

import utils.Action
import utils.GameEnvironment
import utils.GameOverException

class Environment(
    private val view: MergeBallsView,
    private val spriteGrid: SpriteGrid
) : GameEnvironment() {
    private var currentBox: Box = getRandomBox()

    override fun repaint() {
        view.refresh()
    }

    override fun onAction(action: Action) {
        if (action == Action.DROP) {
            var dropY = currentBox.y.toInt()
            while (spriteGrid.canMoveTo(currentBox.x.toInt(), dropY + 1)) {
                dropY++
            }
            currentBox.y = dropY.toFloat()
            checkGameOver()
            currentBox = getRandomBox()
            return
        }

        val (dx, dy) = when (action) {
            Action.MOVE_DOWN -> 0 to 1
            Action.MOVE_LEFT -> -1 to 0
            Action.MOVE_RIGHT -> 1 to 0
        }

        if (dx != 0 || dy != 0) {
            val nextX = currentBox.x.toInt() + dx
            val nextY = currentBox.y.toInt() + dy

            if (spriteGrid.canMoveTo(nextX, nextY)) {
                currentBox.x = nextX.toFloat()
                currentBox.y = nextY.toFloat()
            } else if (action == Action.MOVE_DOWN) {
                // If gravity/move down fails, it has landed
                checkGameOver()
                currentBox = getRandomBox()
            }
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        syncSpriteListToGrid()
    }

    private fun checkGameOver() {
        if (currentBox.y.toInt() == 0) {
            throw GameOverException()
        }
    }

    private fun getRandomBox(): Box {
        var randomX: Int
        do {
            randomX = (Math.random() * spriteGrid.width).toInt()
        } while (spriteGrid.get(randomX, 0) != null)

        val box = Box(x = randomX.toFloat(), y = 0f, value = 2)
        addSprite(box)
        spriteGrid.set(randomX, 0, box)
        return box
    }

    private fun syncSpriteListToGrid() {
        spriteGrid.clear()
        spriteList.forEach { sprite ->
            if (sprite is Box) {
                spriteGrid.set(sprite.x.toInt(), sprite.y.toInt(), sprite)
            }
        }
    }
}
