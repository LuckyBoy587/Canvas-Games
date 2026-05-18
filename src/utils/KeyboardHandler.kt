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
            'a' to Action.MOVE_LEFT,
            'A' to Action.MOVE_LEFT,
            'd' to Action.MOVE_RIGHT,
            'D' to Action.MOVE_RIGHT,
            ' ' to Action.DROP,
        )
    }

    private val keyMap = DEFAULT_KEY_MAP.toMutableMap()
    private val keyHoldTime = ConcurrentHashMap<Char, Float>()  // Track total hold time since press
    private val lastEventTime = ConcurrentHashMap<Char, Float>() // Track time since last triggered event

    fun remapKey(keyChar: Char, action: Action) {
        keyMap[keyChar] = action
    }

    fun tick(deltaTime: Float) {
        for (keyChar in keyHoldTime.keys()) {
            val totalHoldTime = (keyHoldTime[keyChar] ?: 0f) + deltaTime
            keyHoldTime[keyChar] = totalHoldTime

            val timeSinceLastEvent = (lastEventTime[keyChar] ?: 0f) + deltaTime
            lastEventTime[keyChar] = timeSinceLastEvent

            val action = keyMap[keyChar] ?: continue

            // Check if we have passed the initial threshold
            if (totalHoldTime * 1000f >= holdThreshold) {
                // If it's the first hold event after threshold or interval has passed
                if (timeSinceLastEvent * 1000f >= holdInterval) {
                    actionBuffer.addAction(action)
                    lastEventTime[keyChar] = 0f // Reset interval timer
                }
            }
        }
    }

    override fun keyTyped(e: KeyEvent?) {
        // Not used for movement keys
    }

    override fun keyPressed(e: KeyEvent?) {
        val keyChar = e?.keyChar ?: return
        if (keyMap.containsKey(keyChar) && !keyHoldTime.containsKey(keyChar)) {
            keyHoldTime[keyChar] = 0f
            lastEventTime[keyChar] = 0f
            actionBuffer.addAction(keyMap[keyChar]!!)
        }
    }

    override fun keyReleased(e: KeyEvent?) {
        val keyChar = e?.keyChar ?: return
        keyHoldTime.remove(keyChar)
        lastEventTime.remove(keyChar)
    }
}