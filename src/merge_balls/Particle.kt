package merge_balls

import utils.Sprite
import java.awt.Color

class Particle(
    x: Float,
    y: Float,
    vx: Float,
    vy: Float,
    ax: Float,
    ay: Float,
    override val color: Color,
    val maxLife: Float,
    val initialSize: Float
) : Sprite(x, y, vx, vy, ax, ay) {
    var life: Float = maxLife
}
