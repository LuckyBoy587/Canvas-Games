package utils

interface ActionRetriever {
    fun hasActions(): Boolean
    fun getAction(): Action
}