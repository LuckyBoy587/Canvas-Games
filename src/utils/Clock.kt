package utils

class Clock(
    private val gameEnvironment: GameEnvironment,
    private val actionRetriever: ActionRetriever,
    private val onTick: ((Float) -> Unit)? = null,
    var gravityInterval: Float = 1.0f // seconds
) {
    private var fps = 120
    private var frameTime = 1000f / fps
    private var lastTime = System.nanoTime()
    private var isRunning = true
    private var gravityAccumulator = 0f

    fun start() {
        while (isRunning) {
            val currentTime = System.nanoTime()
            val elapsedTimeMs = (currentTime - lastTime) / 1_000_000f

            if (elapsedTimeMs >= frameTime) {
                val deltaTime = elapsedTimeMs / 1000f

                onTick?.invoke(deltaTime)

                if (gravityInterval > 0) {
                    gravityAccumulator += deltaTime
                    if (gravityAccumulator >= gravityInterval) {
                        gameEnvironment.onAction(Action.MOVE_DOWN)
                        gravityAccumulator -= gravityInterval
                    }
                }

                // Process all queued actions before updating game state
                while (actionRetriever.hasActions()) {
                    val action = actionRetriever.getAction()
                    gameEnvironment.onAction(action)
                }

                gameEnvironment.update(deltaTime)
                gameEnvironment.repaint()

                lastTime = currentTime
            }
        }
    }

    fun stop() {
        isRunning = false
    }

    fun setFPS(newFps: Int) {
        if (newFps <= 0) return
        fps = newFps
        frameTime = 1000f / fps
    }

    fun getFPS(): Int = fps
}