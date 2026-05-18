package utils

import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class KeyboardHandler(
    private val actionBuffer: ActionBuffer,
    private val holdThreshold: Long = 300  // milliseconds
): KeyListener {
    companion object {
        private val DEFAULT_KEY_MAP = mapOf(
            'w' to Action.MOVE_UP,
            'W' to Action.MOVE_UP,
            's' to Action.MOVE_DOWN,
            'S' to Action.MOVE_DOWN,
            'a' to Action.MOVE_LEFT,
            'A' to Action.MOVE_LEFT,
            'd' to Action.MOVE_RIGHT,
            'D' to Action.MOVE_RIGHT
        )
    }

    private val keyMap = DEFAULT_KEY_MAP.toMutableMap()
    private val keyPressTime = mutableMapOf<Char, Long>()  // Track press timestamp

    fun remapKey(keyChar: Char, action: Action) {
        keyMap[keyChar] = action
    }

    fun addHoldEvents() {
        val currentTime = System.currentTimeMillis()

        for ((keyChar, pressTime) in keyPressTime) {
            val elapsedTime = currentTime - pressTime
            val action = keyMap[keyChar] ?: continue
            if (elapsedTime >= holdThreshold) {
                // Already past threshold, add action every frame
                actionBuffer.addAction(action)
            }
        }
    }

    override fun keyTyped(e: KeyEvent?) {
        // Not used for movement keys
    }

    override fun keyPressed(e: KeyEvent?) {
        if (e == null) return

        val keyChar = e.keyChar
        if (keyMap.containsKey(keyChar) && keyChar !in keyPressTime) {
            keyPressTime[keyChar] = System.currentTimeMillis()
            actionBuffer.addAction(keyMap[keyChar]!!)
        }
    }

    override fun keyReleased(e: KeyEvent?) {
        if (e == null) return
        val keyChar = e.keyChar
        keyPressTime.remove(keyChar)
    }
}