package utils

class Clock(
    private val gameEnvironment: GameEnvironment,
    private val actionRetriver: ActionRetriver
) {
    private var fps = 60
    private var frameTime = 1000f / fps
    private var lastTime = System.nanoTime()
    private var isRunning = true

    fun start() {
        while (isRunning) {
            val currentTime = System.nanoTime()
            val elapsedTimeMs = (currentTime - lastTime) / 1_000_000f
            
            if (elapsedTimeMs >= frameTime) {
                val deltaTime = elapsedTimeMs / 1000f
                
                // Process all queued actions before updating game state
                while (actionRetriver.hasActions()) {
                    val action = actionRetriver.getAction()
                    gameEnvironment.onAction(action)
                }
                
                gameEnvironment.update(deltaTime)
                gameEnvironment.repaint()
                
                lastTime = currentTime
            } else {
                val sleepTime = frameTime - elapsedTimeMs
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime.toLong())
                }
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