package merge_balls

import utils.ActionQueue
import utils.Clock
import utils.GameOverException
import utils.KeyboardHandler

fun main() {
    val spriteGrid = SpriteGrid(6, 7)
    val view = MergeBallsView(spriteGrid)
    val game = Environment(view, spriteGrid)
    val actionQueue = ActionQueue()
    val keyboardHandler = KeyboardHandler(actionQueue)
    
    // Set up keyboard input
    view.setKeyListener(keyboardHandler)
    
    // Start the game loop
    val clock = Clock(game, actionQueue, keyboardHandler::tick)
    try {
        clock.start()
    } catch (e: GameOverException) {
        println(e.message)
        clock.stop()
    }
}
