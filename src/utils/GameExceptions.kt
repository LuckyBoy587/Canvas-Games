package utils

class ActionNotFoundException : Exception() {
    override val message = "Action not found"
}