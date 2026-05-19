package merge_balls

import utils.Animation
import kotlin.math.sqrt

class MergeAnimation(
    private val boxes: List<Box>,
    private val targetX: Int,
    private val targetY: Int,
    private val onAnimationFinished: () -> Unit
) : Animation {
    private val speed = 15f
    override var isFinished = false

    override fun update(deltaTime: Float) {
        var allReached = true
        for (box in boxes) {
            val dx = targetX.toFloat() - box.x
            val dy = targetY.toFloat() - box.y
            val dist = sqrt((dx * dx + dy * dy).toDouble()).toFloat()

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
        onAnimationFinished()
    }
}

class GravityAnimation(
    private val events: List<GravityEvent>,
    private val onAnimationFinished: () -> Unit
) : Animation {
    private val speed = 15f
    override var isFinished = false

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

class PopAnimation(
    private val box: Box,
    private val onAnimationFinished: () -> Unit
) : Animation {
    private val duration = 0.15f // 150 milliseconds
    private var elapsed = 0f
    override var isFinished = false

    override fun update(deltaTime: Float) {
        elapsed += deltaTime
        if (elapsed >= duration) {
            box.scale = 1.0f
            isFinished = true
        } else {
            val progress = elapsed / duration
            // Sine wave to go 1.0 -> 1.3 -> 1.0
            val scaleOffset = kotlin.math.sin(progress * Math.PI).toFloat() * 0.3f
            box.scale = 1.0f + scaleOffset
        }
    }

    override fun onComplete() {
        onAnimationFinished()
    }
}
