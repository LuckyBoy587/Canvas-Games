package utils

import javax.swing.JFrame
import javax.swing.JPanel
import java.awt.event.KeyListener

abstract class GameView(
    title: String,
    val width: Int,
    val height: Int
) {
    protected val frame: JFrame = JFrame(title).apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        isResizable = false
    }
    
    protected abstract val canvas: JPanel
    
    protected fun setupUI() {
        frame.add(canvas)
        frame.setSize(width, height)
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }
    
    fun setKeyListener(keyListener: KeyListener) {
        canvas.addKeyListener(keyListener)
        canvas.focusTraversalKeysEnabled = false
        canvas.requestFocusInWindow()
    }
    
    fun refresh() {
        canvas.repaint()
    }
}

