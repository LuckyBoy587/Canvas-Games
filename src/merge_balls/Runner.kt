package merge_balls

import utils.ActionQueue
import utils.Clock
import utils.KeyboardHandler

fun main() {
    val spriteGrid = SpriteGrid(10, 10)
    val view = MergeBallsView(spriteGrid)
    val game = Environment(view, spriteGrid)
    val actionQueue = ActionQueue()
    val keyboardHandler = KeyboardHandler(actionQueue)
    
    // Set up keyboard input
    view.setKeyListener(keyboardHandler)
    
    // Start the game loop
    val clock = Clock(game, actionQueue)
    clock.start()
}
