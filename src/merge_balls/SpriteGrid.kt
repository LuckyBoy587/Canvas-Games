package merge_balls

import utils.Grid
import kotlin.math.pow

data class MergeEvent(val boxes: List<Box>, val targetX: Int, val targetY: Int, val newValue: Int)
data class GravityEvent(val box: Box, val targetY: Int)

class SpriteGrid(override val width: Int = 10, override val height: Int = 10) : Grid<Box> {
    private val grid: Array<Array<Box?>> = Array(width) { arrayOfNulls(height) }

    fun checkMerges(lastX: Int, lastY: Int): List<MergeEvent> {
        val events = mutableListOf<MergeEvent>()
        val visited = Array(width) { BooleanArray(height) }

        for (x in 0..<width) {
            for (y in 0..<height) {
                val box = get(x, y)
                if (box != null && !visited[x][y]) {
                    val island = mutableListOf<Pair<Int, Int>>()
                    findIsland(x, y, box.value, island, visited)

                    if (island.size > 1) {
                        val boxes = island.map { (ix, iy) -> get(ix, iy)!! }

                        // Decide final merging cell:
                        // 1. Lowest in grid (max Y)
                        // 2. Closest to lastX, lastY
                        // 3. One on left (min X)
                        val target = island.sortedWith(compareByDescending<Pair<Int, Int>> { it.second }
                            .thenBy { (ix, iy) -> 
                                val dx = ix - lastX
                                val dy = iy - lastY
                                dx * dx + dy * dy
                            }
                            .thenBy { it.first }
                        ).first()

                        val newValue = box.value * 2.0.pow(island.size - 1).toInt()
                        events.add(MergeEvent(boxes, target.first, target.second, newValue))
                    }
                }
            }
        }
        return events
    }

    fun getLandingY(x: Int, startY: Int): Int {
        var targetY = startY
        while (targetY + 1 < height && get(x, targetY + 1) == null) {
            targetY++
        }
        return targetY
    }

    fun checkGravity(): List<GravityEvent> {
        val events = mutableListOf<GravityEvent>()
        for (x in 0..<width) {
            var nextEmptyY = height - 1
            for (y in height - 1 downTo 0) {
                val box = get(x, y)
                if (box != null) {
                    if (y != nextEmptyY) {
                        events.add(GravityEvent(box, nextEmptyY))
                    }
                    nextEmptyY--
                }
            }
        }
        return events
    }

    private fun findIsland(x: Int, y: Int, targetValue: Int, island: MutableList<Pair<Int, Int>>, visited: Array<BooleanArray>) {
        visited[x][y] = true
        island.add(Pair(x, y))

        for ((nx, ny) in getAdjacentCells(x, y)) {
            val neighbor = get(nx, ny)
            if (neighbor != null && neighbor.value == targetValue && !visited[nx][ny]) {
                findIsland(nx, ny, targetValue, island, visited)
            }
        }
    }

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
