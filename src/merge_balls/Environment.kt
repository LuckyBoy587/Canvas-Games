package merge_balls

import utils.Action
import utils.GameEnvironment
import utils.GameOverException

class Environment(
    private val view: MergeBallsView,
    private val spriteGrid: SpriteGrid
) : GameEnvironment() {
    private var currentBox: Box = getRandomBox()
    private val fallingSpeed = 15f

    override fun repaint() {
        view.refresh()
    }

    override fun onAction(action: Action) {
        if (currentBox.state != BoxState.CONTROLLED) return

        if (action == Action.DROP) {
            currentBox.state = BoxState.FALLING
            currentBox.vy = fallingSpeed
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

            if (spriteGrid.isValidCell(nextX, nextY) && spriteGrid.canMoveTo(nextX, nextY)) {
                currentBox.x = nextX.toFloat()
                currentBox.y = nextY.toFloat()
            } else if (action == Action.MOVE_DOWN) {
                currentBox.state = BoxState.LOCKED
                checkGameOver(currentBox)
                currentBox = getRandomBox()
            }
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        
        // Handle falling boxes and collisions
        val boxes = spriteList.filterIsInstance<Box>()
        boxes.forEach { box ->
            if (box.state == BoxState.FALLING) {
                val nextGridY = (box.y + 0.1f).toInt() + 1
                if (nextGridY >= spriteGrid.height || spriteGrid.get(box.x.toInt(), nextGridY) != null) {
                    // Check if we've reached the integer position of the current or next cell
                    if (box.y >= nextGridY - 1) {
                        box.y = (nextGridY - 1).toFloat()
                        box.vy = 0f
                        box.state = BoxState.LOCKED
                        checkGameOver(box)
                        if (box == currentBox) {
                            currentBox = getRandomBox()
                        }
                    }
                }
            }
        }

        syncSpriteListToGrid()
        view.updateSprites(boxes)
    }

    private fun checkGameOver(landedBox: Box) {
        if (landedBox.y.toInt() == 0) {
            throw GameOverException()
        }
    }

    private fun getRandomBox(): Box {
        var randomX: Int
        var attempts = 0
        do {
            randomX = (Math.random() * spriteGrid.width).toInt()
            attempts++
        } while (spriteGrid.get(randomX, 0) != null && attempts < 100)

        if (attempts >= 100) throw GameOverException()

        val box = Box(x = randomX.toFloat(), y = 0f, value = 2)
        box.state = BoxState.CONTROLLED
        addSprite(box)
        // Note: We don't add to spriteGrid yet, it will be synced in update
        return box
    }

    private fun syncSpriteListToGrid() {
        spriteGrid.clear()
        spriteList.forEach { sprite ->
            if (sprite is Box && sprite.state == BoxState.LOCKED) {
                spriteGrid.set(sprite.x.toInt(), sprite.y.toInt(), sprite)
            }
        }
    }
}
