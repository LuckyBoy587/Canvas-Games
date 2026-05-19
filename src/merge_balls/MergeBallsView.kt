package merge_balls

import utils.GameView
import java.awt.*

class MergeBallsView(
    spriteGrid: SpriteGrid,
    cellSize: Int = 60,
    padding: Int = 10,
    sidePanelWidth: Int = 140
) : GameView(
    "Merge Balls",
    spriteGrid.width * cellSize + padding * 3 + sidePanelWidth + 16,
    spriteGrid.height * cellSize + padding * 2 + 39
) {
    override val canvas: GridCanvas = GridCanvas(spriteGrid, cellSize, padding, sidePanelWidth)
    
    init {
        setupUI()
    }

    fun updateSprites(
        sprites: List<Box>, 
        isGameOver: Boolean = false, 
        ghostX: Int? = null, 
        ghostY: Int? = null, 
        ghostValue: Int? = null,
        nextValue: Int = 2,
        score: Int = 0,
        bestScore: Int = 0
    ) {
        canvas.sprites = sprites
        canvas.isGameOver = isGameOver
        canvas.ghostX = ghostX
        canvas.ghostY = ghostY
        canvas.ghostValue = ghostValue
        canvas.nextValue = nextValue
        canvas.score = score
        canvas.bestScore = bestScore
    }
}


class GridCanvas(
    private val spriteGrid: SpriteGrid,
    private val cellSize: Int,
    private val padding: Int,
    private val sidePanelWidth: Int
) : javax.swing.JPanel() {
    var sprites: List<Box> = listOf()
    var isGameOver: Boolean = false
    var ghostX: Int? = null
    var ghostY: Int? = null
    var ghostValue: Int? = null
    var nextValue: Int = 2
    var score: Int = 0
    var bestScore: Int = 0

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

        // Draw Side Panel on the right
        drawSidePanel(g2d)

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

    private fun drawSidePanel(g2d: Graphics2D) {
        val sidePanelX = padding + spriteGrid.width * cellSize + padding
        val sidePanelY = padding
        val sidePanelH = spriteGrid.height * cellSize

        // Background card
        g2d.color = Color(205, 193, 180)
        g2d.fillRoundRect(sidePanelX, sidePanelY, sidePanelWidth, sidePanelH, 12, 12)

        // Border for the side panel card
        g2d.color = Color(149, 136, 134)
        g2d.stroke = BasicStroke(2f)
        g2d.drawRoundRect(sidePanelX, sidePanelY, sidePanelWidth, sidePanelH, 12, 12)

        // Draw "NEXT" header card inside the side panel
        val nextHeaderW = sidePanelWidth - 20
        val nextHeaderX = sidePanelX + 10
        val nextHeaderY = sidePanelY + 15
        val nextHeaderH = 30

        g2d.color = Color(187, 173, 160)
        g2d.fillRoundRect(nextHeaderX, nextHeaderY, nextHeaderW, nextHeaderH, 6, 6)

        // Text inside header card
        g2d.color = Color(119, 110, 101)
        val headerFont = Font("Arial", Font.BOLD, 14)
        g2d.font = headerFont
        val headerFm = g2d.fontMetrics
        val headerText = "NEXT"
        val headerTextX = nextHeaderX + (nextHeaderW - headerFm.stringWidth(headerText)) / 2
        val headerTextY = nextHeaderY + ((nextHeaderH - headerFm.height) / 2) + headerFm.ascent
        g2d.drawString(headerText, headerTextX, headerTextY)

        // Draw the next block preview box in the center of the side panel
        val previewSize = cellSize
        val previewX = sidePanelX + (sidePanelWidth - previewSize) / 2
        val previewY = nextHeaderY + nextHeaderH + 20

        // Draw next box using a temporary Box instance (to reuse its color mapping)
        val dummyBox = Box(0f, 0f, nextValue)

        // Draw box fill
        g2d.color = dummyBox.color
        g2d.fillRoundRect(previewX, previewY, previewSize, previewSize, 6, 6)

        // Draw border
        g2d.color = Color.BLACK
        g2d.stroke = BasicStroke(1.5f)
        g2d.drawRoundRect(previewX, previewY, previewSize, previewSize, 6, 6)

        // Draw value text
        g2d.color = if (nextValue >= 8) Color.WHITE else Color.BLACK
        val valFont = Font("Arial", Font.BOLD, 22)
        g2d.font = valFont
        val valFm = g2d.fontMetrics
        val valText = nextValue.toString()
        val textX = previewX + (previewSize - valFm.stringWidth(valText)) / 2
        val textY = previewY + ((previewSize - valFm.height) / 2) + valFm.ascent
        g2d.drawString(valText, textX, textY)

        // --- SCORE card ---
        val scoreCardY = previewY + previewSize + 15
        val scoreCardH = 45
        g2d.color = Color(187, 173, 160)
        g2d.fillRoundRect(nextHeaderX, scoreCardY, nextHeaderW, scoreCardH, 6, 6)

        // Draw "SCORE" label text inside card
        g2d.color = Color(238, 228, 218)
        val scoreLabelFont = Font("Arial", Font.BOLD, 10)
        g2d.font = scoreLabelFont
        val scoreLabelFm = g2d.fontMetrics
        val scoreLabelText = "SCORE"
        val scoreLabelTextX = nextHeaderX + (nextHeaderW - scoreLabelFm.stringWidth(scoreLabelText)) / 2
        val scoreLabelTextY = scoreCardY + 14
        g2d.drawString(scoreLabelText, scoreLabelTextX, scoreLabelTextY)

        // Draw score value
        g2d.color = Color.WHITE
        val scoreValFont = Font("Arial", Font.BOLD, 16)
        g2d.font = scoreValFont
        val scoreValFm = g2d.fontMetrics
        val scoreValText = score.toString()
        val scoreValTextX = nextHeaderX + (nextHeaderW - scoreValFm.stringWidth(scoreValText)) / 2
        val scoreValTextY = scoreCardY + 34
        g2d.drawString(scoreValText, scoreValTextX, scoreValTextY)

        // --- BEST card ---
        val bestCardY = scoreCardY + scoreCardH + 10
        val bestCardH = 45
        g2d.color = Color(187, 173, 160)
        g2d.fillRoundRect(nextHeaderX, bestCardY, nextHeaderW, bestCardH, 6, 6)

        // Draw "BEST" label text inside card
        g2d.color = Color(238, 228, 218)
        g2d.font = scoreLabelFont
        val bestLabelText = "BEST"
        val bestLabelTextX = nextHeaderX + (nextHeaderW - scoreLabelFm.stringWidth(bestLabelText)) / 2
        val bestLabelTextY = bestCardY + 14
        g2d.drawString(bestLabelText, bestLabelTextX, bestLabelTextY)

        // Draw best score value
        g2d.color = Color.WHITE
        g2d.font = scoreValFont
        val bestValText = bestScore.toString()
        val bestValTextX = nextHeaderX + (nextHeaderW - scoreValFm.stringWidth(bestValText)) / 2
        val bestValTextY = bestCardY + 34
        g2d.drawString(bestValText, bestValTextX, bestValTextY)

        // Draw "CONTROLS" header card
        val controlsHeaderY = bestCardY + bestCardH + 15
        g2d.color = Color(187, 173, 160)
        g2d.fillRoundRect(nextHeaderX, controlsHeaderY, nextHeaderW, nextHeaderH, 6, 6)

        // Text inside controls header card
        g2d.color = Color(119, 110, 101)
        g2d.font = headerFont
        val controlsText = "CONTROLS"
        val controlsTextX = nextHeaderX + (nextHeaderW - headerFm.stringWidth(controlsText)) / 2
        val controlsTextY = controlsHeaderY + ((nextHeaderH - headerFm.height) / 2) + headerFm.ascent
        g2d.drawString(controlsText, controlsTextX, controlsTextY)

        // Draw control descriptions
        val labelFont = Font("Arial", Font.BOLD, 11)
        val descFont = Font("Arial", Font.PLAIN, 11)
        g2d.color = Color(119, 110, 101)

        val controls = listOf(
            "A / \u2190" to "Move Left",
            "D / \u2192" to "Move Right",
            "S / \u2193" to "Move Down",
            "SPACE" to "Drop Block",
            "R" to "Restart"
        )

        var currentY = controlsHeaderY + nextHeaderH + 20
        controls.forEach { (key, action) ->
            // Draw Key label
            g2d.font = labelFont
            g2d.drawString(key, nextHeaderX + 5, currentY)

            // Draw Action description right-aligned
            g2d.font = descFont
            val actionFm = g2d.fontMetrics
            val actionX = nextHeaderX + nextHeaderW - 5 - actionFm.stringWidth(action)
            g2d.drawString(action, actionX, currentY)

            currentY += 24
        }
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
