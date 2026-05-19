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

    fun updateSprites(
        sprites: List<Box>, 
        isGameOver: Boolean = false, 
        ghostX: Int? = null, 
        ghostY: Int? = null, 
        ghostValue: Int? = null
    ) {
        canvas.sprites = sprites
        canvas.isGameOver = isGameOver
        canvas.ghostX = ghostX
        canvas.ghostY = ghostY
        canvas.ghostValue = ghostValue
    }
}


class GridCanvas(
    private val spriteGrid: SpriteGrid,
    private val cellSize: Int,
    private val padding: Int
) : javax.swing.JPanel() {
    var sprites: List<Box> = listOf()
    var isGameOver: Boolean = false
    var ghostX: Int? = null
    var ghostY: Int? = null
    var ghostValue: Int? = null

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

        // Draw Ghost projection if coordinates are present
        val gx = ghostX
        val gy = ghostY
        val gv = ghostValue
        if (gx != null && gy != null && gv != null && !isGameOver) {
            drawGhostSprite(g2d, gx, gy, gv)
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

    private fun drawGhostSprite(g: Graphics2D, gx: Int, gy: Int, value: Int) {
        val x = padding + gx * cellSize + 2
        val y = padding + gy * cellSize + 2
        val size = cellSize - 4

        // Look up corresponding box color
        val dummyBox = Box(0f, 0f, value)
        val baseColor = dummyBox.color

        // Ensure proper AlphaComposite blending is active
        val originalComposite = g.composite
        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)

        // Translucent fill with increased opacity for clear visibility (130/255 ~ 51%)
        g.color = Color(baseColor.red, baseColor.green, baseColor.blue, 130)
        g.fillRoundRect(x, y, size, size, 6, 6)

        // Solid-contrast dotted/dashed border
        val originalStroke = g.stroke
        val dashedStroke = BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0f, floatArrayOf(4f, 4f), 0f)
        g.stroke = dashedStroke
        g.color = Color(0, 0, 0, 180) // darker translucent black border (70% opacity)
        g.drawRoundRect(x, y, size, size, 6, 6)
        g.stroke = originalStroke

        // Draw clear value text
        g.color = if (value >= 8) Color(255, 255, 255, 200) else Color(0, 0, 0, 200)
        val font = Font("Arial", Font.BOLD, 20)
        g.font = font
        val fm = g.fontMetrics
        val textX = x + (size - fm.stringWidth(value.toString())) / 2
        val textY = y + ((size - fm.height) / 2) + fm.ascent
        g.drawString(value.toString(), textX, textY)

        // Restore graphics composite state
        g.composite = originalComposite
    }

    private fun drawSprite(g: Graphics2D, sprite: Box) {
        val scale = sprite.scale
        val originalSize = cellSize - 4
        val size = originalSize * scale
        
        // Keep block centered inside its grid coordinate cell
        val x = padding + sprite.x * cellSize + 2 + (originalSize - size) / 2
        val y = padding + sprite.y * cellSize + 2 + (originalSize - size) / 2

        // Draw box with scaled corners
        g.color = sprite.color
        g.fillRoundRect(x.toInt(), y.toInt(), size.toInt(), size.toInt(), (6 * scale).toInt(), (6 * scale).toInt())

        // Draw border
        g.color = Color.BLACK
        g.stroke = BasicStroke(1f * scale)
        g.drawRoundRect(x.toInt(), y.toInt(), size.toInt(), size.toInt(), (6 * scale).toInt(), (6 * scale).toInt())

        // Draw value text with scaled font size
        g.color = if (sprite.value >= 8) Color.WHITE else Color.BLACK
        val font = Font("Arial", Font.BOLD, (20 * scale).toInt())
        g.font = font
        val fm = g.fontMetrics
        val textX = x + (size - fm.stringWidth(sprite.value.toString())) / 2
        val textY = y + ((size - fm.height) / 2) + fm.ascent
        g.drawString(sprite.value.toString(), textX.toInt(), textY.toInt())
    }
}
