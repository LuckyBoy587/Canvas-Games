package merge_balls

import utils.GameView
import java.awt.*

class MergeBallsView(
    spriteGrid: SpriteGrid,
    gridSize: Int = 10,
    cellSize: Int = 60,
    padding: Int = 10
) : GameView(
    "Merge Balls",
    gridSize * cellSize + padding * 2 + 16,
    gridSize * cellSize + padding * 2 + 39
) {
    override val canvas: GridCanvas = GridCanvas(spriteGrid, gridSize, cellSize, padding)
    
    init {
        setupUI()
    }
}

class GridCanvas(
    private val spriteGrid: SpriteGrid,
    private val gridSize: Int,
    private val cellSize: Int,
    private val padding: Int
) : javax.swing.JPanel() {

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // Draw grid background
        g2d.color = Color(187, 173, 160)
        g2d.fillRect(padding, padding, gridSize * cellSize, gridSize * cellSize)

        // Draw grid lines
        g2d.color = Color(149, 136, 134)
        g2d.stroke = BasicStroke(2f)
        for (i in 0..gridSize) {
            val pos = padding + i * cellSize
            // Vertical lines
            g2d.drawLine(pos, padding, pos, padding + gridSize * cellSize)
            // Horizontal lines
            g2d.drawLine(padding, pos, padding + gridSize * cellSize, pos)
        }

        // Draw sprites
        for (x in 0..<spriteGrid.width) {
            for (y in 0..<spriteGrid.height) {
                spriteGrid.get(x, y)?.let { sprite ->
                    drawSprite(g2d, sprite, x, y)
                }
            }
        }
    }

    private fun drawSprite(g: Graphics2D, sprite: Box, gridX: Int, gridY: Int) {
        val x = padding + gridX * cellSize + 2
        val y = padding + gridY * cellSize + 2
        val size = cellSize - 4

        // Draw box with rounded corners
        g.color = sprite.color
        g.fillRoundRect(x, y, size, size, 6, 6)

        // Draw border
        g.color = Color.BLACK
        g.stroke = BasicStroke(1f)
        g.drawRoundRect(x, y, size, size, 6, 6)

        // Draw value text
        g.color = if (sprite.value >= 8) Color.WHITE else Color.BLACK
        val font = Font("Arial", Font.BOLD, 20)
        g.font = font
        val fm = g.fontMetrics
        val textX = x + (size - fm.stringWidth(sprite.value.toString())) / 2
        val textY = y + ((size - fm.height) / 2) + fm.ascent
        g.drawString(sprite.value.toString(), textX, textY)
    }
}
