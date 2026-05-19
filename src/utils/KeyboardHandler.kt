package utils

import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.concurrent.ConcurrentHashMap

class KeyboardHandler(
    private val actionBuffer: ActionBuffer,
    private val holdThreshold: Long = 300,  // initial delay in milliseconds
    private val holdInterval: Long = 50     // interval between subsequent hold events in milliseconds
): KeyListener {
    companion object {
        private val DEFAULT_KEY_MAP = mapOf(
            KeyEvent.VK_A to Action.MOVE_LEFT,
            KeyEvent.VK_LEFT to Action.MOVE_LEFT,
            KeyEvent.VK_D to Action.MOVE_RIGHT,
            KeyEvent.VK_RIGHT to Action.MOVE_RIGHT,
            KeyEvent.VK_S to Action.MOVE_DOWN,
            KeyEvent.VK_DOWN to Action.MOVE_DOWN,
            KeyEvent.VK_SPACE to Action.DROP,
        )
    }

    private val keyMap = DEFAULT_KEY_MAP.toMutableMap()
    private val keyHoldTime = ConcurrentHashMap<Int, Float>()  // Track total hold time since press
    private val lastEventTime = ConcurrentHashMap<Int, Float>() // Track time since last triggered event

    fun tick(deltaTime: Float) {
        for (keyCode in keyHoldTime.keys()) {
            val totalHoldTime = (keyHoldTime[keyCode] ?: 0f) + deltaTime
            keyHoldTime[keyCode] = totalHoldTime

            val timeSinceLastEvent = (lastEventTime[keyCode] ?: 0f) + deltaTime
            lastEventTime[keyCode] = timeSinceLastEvent

            val action = keyMap[keyCode] ?: continue

            // DROP action should not repeat on hold
            if (action == Action.DROP) continue

            // Check if we have passed the initial threshold
            if (totalHoldTime * 1000f >= holdThreshold) {
                // If it's the first hold event after threshold or interval has passed
                if (timeSinceLastEvent * 1000f >= holdInterval) {
                    actionBuffer.addAction(action)
                    lastEventTime[keyCode] = 0f // Reset interval timer
                }
            }
        }
    }

    override fun keyTyped(e: KeyEvent?) {
        // Not used
    }

    override fun keyPressed(e: KeyEvent?) {
        val keyCode = e?.keyCode ?: return
        if (keyMap.containsKey(keyCode) && !keyHoldTime.containsKey(keyCode)) {
            keyHoldTime[keyCode] = 0f
            lastEventTime[keyCode] = 0f
            actionBuffer.addAction(keyMap[keyCode]!!)
        }
    }

    override fun keyReleased(e: KeyEvent?) {
        val keyCode = e?.keyCode ?: return
        keyHoldTime.remove(keyCode)
        lastEventTime.remove(keyCode)
    }
}
