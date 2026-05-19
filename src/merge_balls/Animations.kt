package merge_balls

import utils.Animation
import kotlin.math.abs

class MergeAnimation(
    private val boxes: List<Box>,
    private val targetX: Int,
    private val targetY: Int,
    private val newValue: Int,
    private val grid: SpriteGrid,
    private val spriteList: MutableList<utils.Sprite>,
    private val onAnimationFinished: () -> Unit
) : Animation {
    private val speed = 15f
    override var isFinished = false

    override fun update(deltaTime: Float) {
        var allReached = true
        for (box in boxes) {
            val dx = targetX.toFloat() - box.x
            val dy = targetY.toFloat() - box.y
            val dist = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            if (dist < 0.1f) {
                box.x = targetX.toFloat()
                box.y = targetY.toFloat()
            } else {
                val step = speed * deltaTime
                if (step >= dist) {
                    box.x = targetX.toFloat()
                    box.y = targetY.toFloat()
                } else {
                    box.x += (dx / dist) * step
                    box.y += (dy / dist) * step
                    allReached = false
                }
            }
        }
        if (allReached) isFinished = true
    }

    override fun onComplete() {
        // Remove old boxes from grid and sprite list
        for (box in boxes) {
            grid.remove(box.x.toInt(), box.y.toInt())
            spriteList.remove(box)
        }
        // Add new box
        val mergedBox = Box(targetX.toFloat(), targetY.toFloat(), newValue)
        mergedBox.state = BoxState.LOCKED
        grid.place(mergedBox, targetX, targetY)
        spriteList.add(mergedBox)
        
        onAnimationFinished()
    }
}

class GravityAnimation(
    private val events: List<GravityEvent>,
    private val grid: SpriteGrid,
    private val onAnimationFinished: () -> Unit
) : Animation {
    private val speed = 15f
    override var isFinished = false

    init {
        // Remove boxes from their old positions in grid immediately to avoid collisions during check
        for (event in events) {
            grid.remove(event.box.x.toInt(), event.box.y.toInt())
        }
    }

    override fun update(deltaTime: Float) {
        var allReached = true
        for (event in events) {
            val box = event.box
            val targetY = event.targetY.toFloat()
            if (box.y < targetY) {
                box.y += speed * deltaTime
                if (box.y >= targetY) {
                    box.y = targetY
                } else {
                    allReached = false
                }
            }
        }
        if (allReached) isFinished = true
    }

    override fun onComplete() {
        // Place boxes in their new positions in grid
        for (event in events) {
            grid.set(event.box.x.toInt(), event.targetY, event.box)
        }
        onAnimationFinished()
    }
}

class WaitAnimation(
    private val duration: Float,
    private val onAnimationFinished: () -> Unit
) : Animation {
    private var elapsed = 0f
    override var isFinished = false

    override fun update(deltaTime: Float) {
        elapsed += deltaTime
        if (elapsed >= duration) {
            isFinished = true
        }
    }

    override fun onComplete() {
        onAnimationFinished()
    }
}
