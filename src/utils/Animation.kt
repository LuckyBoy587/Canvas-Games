package utils

interface Animation {
    val isFinished: Boolean
    fun update(deltaTime: Float)
    fun onComplete()
}

class SequentialAnimator {
    private val queue = ArrayDeque<Animation>()
    val isAnimating: Boolean
        get() = queue.isNotEmpty()

    fun play(animation: Animation) {
        queue.addLast(animation)
    }

    fun update(deltaTime: Float) {
        if (queue.isNotEmpty()) {
            val current = queue.first()
            current.update(deltaTime)
            if (current.isFinished) {
                current.onComplete()
                queue.removeFirst()
            }
        }
    }

    fun clear() {
        queue.clear()
    }
}
