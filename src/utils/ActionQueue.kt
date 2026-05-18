package utils

import java.util.LinkedList
import java.util.Queue

class ActionQueue: ActionBuffer, ActionRetriever {
    private val actions: Queue<Action> = LinkedList()

    override fun addAction(action: Action) {
        actions.add(action)
    }

    override fun hasActions(): Boolean {
        return actions.isNotEmpty()
    }

    override fun getAction(): Action {
        if (actions.isEmpty()) {
            throw NoSuchElementException("No actions available")
        }
        return actions.poll()
    }
}