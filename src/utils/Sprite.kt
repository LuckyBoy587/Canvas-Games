package utils

import java.awt.Color

abstract class Sprite(
    open var x: Float = 0f,
    open var y: Float = 0f,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var ax: Float = 0f,
    var ay: Float = 0f
) {
    abstract val color: Color
    var isDestroyed = false
    // Position
    val position
        get() = Pair(this.x, this.y)

    // Velocity
    val velocity
        get() = Pair(this.vx, this.vy)

    // Acceleration
    val acceleration
        get() = Pair(this.ax, this.ay)

    fun update(deltaTime: Float) {
        vx += ax * deltaTime
        vy += ay * deltaTime
        x += vx * deltaTime
        y += vy * deltaTime
    }

    fun setPosition(newX: Float, newY: Float) {
        x = newX
        y = newY
    }

    fun setVelocity(newVx: Float, newVy: Float) {
        vx = newVx
        vy = newVy
    }

    fun setAcceleration(newAx: Float, newAy: Float) {
        ax = newAx
        ay = newAy
    }

    fun destroy() {
        isDestroyed = true
    }
}
