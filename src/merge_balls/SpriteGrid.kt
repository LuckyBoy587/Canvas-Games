package merge_balls

import utils.Grid

class SpriteGrid(override val width: Int = 10, override val height: Int = 10) : Grid<Box> {
    private val grid: Array<Array<Box?>> = Array(width) { arrayOfNulls<Box>(height) }

    override fun set(gx: Int, gy: Int, value: Box?): Boolean {
        if (isValidCell(gx, gy)) {
            grid[gx][gy] = value
            return true
        }
        return false
    }

    fun place(box: Box, gx: Int, gy: Int): Boolean {
        if (isValidCell(gx, gy) && grid[gx][gy] == null) {
            set(gx, gy, box)
            box.x = gx.toFloat()
            box.y = gy.toFloat()
            return true
        }
        return false
    }

    fun remove(gx: Int, gy: Int): Box? {
        return grid[gx][gy].also { grid[gx][gy] = null }
    }

    override fun get(gx: Int, gy: Int): Box? {
        return if (isValidCell(gx, gy)) grid[gx][gy] else null
    }

    fun getAdjacentCells(gx: Int, gy: Int): List<Pair<Int, Int>> {
        val adjacent = mutableListOf<Pair<Int, Int>>()
        val directions = listOf(
            Pair(gx + 1, gy),  // Right
            Pair(gx - 1, gy),  // Left
            Pair(gx, gy + 1),  // Down
            Pair(gx, gy - 1)   // Up
        )

        for ((x, y) in directions) {
            if (isValidCell(x, y)) {
                adjacent.add(Pair(x, y))
            }
        }
        return adjacent
    }

    fun getAllSprites(): List<Box> {
        val sprites = mutableListOf<Box>()
        for (x in 0..<width) {
            for (y in 0..<height) {
                grid[x][y]?.let { sprites.add(it) }
            }
        }
        return sprites
    }

    override fun clear() {
        for (x in 0..<width) {
            for (y in 0..<height) {
                grid[x][y] = null
            }
        }
    }

    override fun isValidCell(gx: Int, gy: Int): Boolean {
        return gx in 0..<width && gy in 0..<height
    }
}
