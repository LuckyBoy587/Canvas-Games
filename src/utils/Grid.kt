package utils

interface Grid<T : Sprite> {
    val width: Int
    val height: Int
    fun get(gx: Int, gy: Int): T?
    fun set(gx: Int, gy: Int, value: T?): Boolean
    fun isValidCell(gx: Int, gy: Int): Boolean
    fun clear()
    
    fun isOccupied(gx: Int, gy: Int): Boolean {
        return isValidCell(gx, gy) && get(gx, gy) != null
    }

    fun canMoveTo(gx: Int, gy: Int): Boolean {
        return isValidCell(gx, gy) && !isOccupied(gx, gy)
    }
}
