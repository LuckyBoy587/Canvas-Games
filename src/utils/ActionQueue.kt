package utils

import java.util.concurrent.ConcurrentLinkedQueue

class ActionQueue: ActionBuffer, ActionRetriever {
    private val actions = ConcurrentLinkedQueue<Action>()

    override fun addAction(action: Action) {
        actions.add(action)
    }

    override fun hasActions(): Boolean {
        return actions.isNotEmpty()
    }

    override fun getAction(): Action {
        return actions.poll() ?: throw NoSuchElementException("No actions available")
    }
}