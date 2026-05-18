package utils

class ActionNotFoundException : Exception() {
    override val message = "Action not found"
}

class GameOverException : Exception() {
    override val message = "Game is over"
}