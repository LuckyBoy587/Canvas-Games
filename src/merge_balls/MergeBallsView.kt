package merge_balls

import utils.GameView
import java.awt.*

class MergeBallsView(
    spriteGrid: SpriteGrid,
    cellSize: Int = 60,
    padding: Int = 10
) : GameView(
    "Merge Balls",
    spriteGrid.width * cellSize + padding * 2 + 16,
    spriteGrid.height * cellSize + padding * 2 + 39
) {
    override val canvas: GridCanvas = GridCanvas(spriteGrid, cellSize, padding)
    
    init {
        setupUI()
    }

    fun updateSprites(sprites: List<Box>, isGameOver: Boolean = false) {
        canvas.sprites = sprites
        canvas.isGameOver = isGameOver
    }
}

class GridCanvas(
    private val spriteGrid: SpriteGrid,
    private val cellSize: Int,
    private val padding: Int
) : javax.swing.JPanel() {
    var sprites: List<Box> = listOf()
    var isGameOver: Boolean = false

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // Draw grid background
        g2d.color = Color(187, 173, 160)
        g2d.fillRect(padding, padding, spriteGrid.width * cellSize, spriteGrid.height * cellSize)

        // Draw grid lines
        g2d.color = Color(149, 136, 134)
        g2d.stroke = BasicStroke(2f)
        
        // Vertical lines
        for (i in 0..spriteGrid.width) {
            val pos = padding + i * cellSize
            g2d.drawLine(pos, padding, pos, padding + spriteGrid.height * cellSize)
        }
        
        // Horizontal lines
        for (i in 0..spriteGrid.height) {
            val pos = padding + i * cellSize
            g2d.drawLine(padding, pos, padding + spriteGrid.width * cellSize, pos)
        }

        // Draw sprites from the list
        sprites.forEach { sprite ->
            drawSprite(g2d, sprite)
        }

        // Draw Game Over overlay if game is over
        if (isGameOver) {
            drawGameOverOverlay(g2d)
        }
    }

    private fun drawGameOverOverlay(g: Graphics2D) {
        val width = spriteGrid.width * cellSize
        val height = spriteGrid.height * cellSize

        // Translucent black overlay
        g.color = Color(0, 0, 0, 180)
        g.fillRect(padding, padding, width, height)

        // "GAME OVER" Text
        val font = Font("Arial", Font.BOLD, 36)
        g.font = font
        g.color = Color(246, 94, 59)
        val fm = g.fontMetrics
        val text = "GAME OVER"
        val textX = padding + (width - fm.stringWidth(text)) / 2
        val textY = padding + height / 2 - 10
        g.drawString(text, textX, textY)

        // "Press R to Restart" Text
        val subFont = Font("Arial", Font.PLAIN, 16)
        g.font = subFont
        g.color = Color.WHITE
        val subFm = g.fontMetrics
        val subText = "Press R to Restart"
        val subTextX = padding + (width - subFm.stringWidth(subText)) / 2
        val subTextY = textY + subFm.height + 20
        g.drawString(subText, subTextX, subTextY)
    }

    private fun drawSprite(g: Graphics2D, sprite: Box) {
        val x = padding + sprite.x * cellSize + 2
        val y = padding + sprite.y * cellSize + 2
        val size = cellSize - 4

        // Draw box with rounded corners
        g.color = sprite.color
        g.fillRoundRect(x.toInt(), y.toInt(), size, size, 6, 6)

        // Draw border
        g.color = Color.BLACK
        g.stroke = BasicStroke(1f)
        g.drawRoundRect(x.toInt(), y.toInt(), size, size, 6, 6)

        // Draw value text
        g.color = if (sprite.value >= 8) Color.WHITE else Color.BLACK
        val font = Font("Arial", Font.BOLD, 20)
        g.font = font
        val fm = g.fontMetrics
        val textX = x + (size - fm.stringWidth(sprite.value.toString())) / 2
        val textY = y + ((size - fm.height) / 2) + fm.ascent
        g.drawString(sprite.value.toString(), textX.toInt(), textY.toInt())
    }
}
