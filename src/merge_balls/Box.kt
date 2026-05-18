package merge_balls

import utils.Sprite

class Box(override var x: Float, override var y: Float, val value: Int): Sprite() {
    override val color = when (value) {
        2 -> java.awt.Color(0xeee4da)
        4 -> java.awt.Color(0xede0c8)
        8 -> java.awt.Color(0xf2b179)
        16 -> java.awt.Color(0xf59563)
        32 -> java.awt.Color(0xf67c5f)
        64 -> java.awt.Color(0xf65e3b)
        128 -> java.awt.Color(0xedcf72)
        256 -> java.awt.Color(0xedcc61)
        512 -> java.awt.Color(0xedc850)
        1024 -> java.awt.Color(0xedc53f)
        2048 -> java.awt.Color(0xedc22e)
        else -> java.awt.Color(0xcdc1b4) // Default color for values > 2048
    }

    fun moveLeft(distance: Float = 1f) {
        x -= distance
    }

    fun moveRight(distance: Float = 1f) {
        x += distance
    }
}