# Animation Implementation Plan

## Objective
Replace the instant merge and gravity calculations with fluid, visual animations. When an island of boxes merges, the boxes should visually travel towards the final target cell. When empty spaces are created, the remaining boxes should smoothly fall down. The animation system must be modular to easily add new animations in the future.

## Scope & Impact
- **`src/utils/Animation.kt` (New File)**: Introduce a modular `Animation` interface and a `SequentialAnimator` to manage queued animations.
- **`src/merge_balls/Animations.kt` (New File)**: Implement `MergeAnimation` (moves boxes to a target cell) and `GravityAnimation` (moves boxes down to their target rows).
- **`src/merge_balls/Environment.kt`**: Refactor the game loop to use the `SequentialAnimator`. The game should pause user input and box spawning while animations are playing.
- **`src/merge_balls/SpriteGrid.kt`**: Modify `merge()` and `applyGravity()` to return animation data (lists of moving boxes, target coordinates) instead of immediately modifying the grid and returning booleans/boxes.

## Proposed Solution

### 1. Core Animation Infrastructure
Create `src/utils/Animation.kt`:
```kotlin
package utils

interface Animation {
    val isFinished: Boolean
    fun update(deltaTime: Float)
    fun onComplete()
}

class SequentialAnimator {
    private val queue = ArrayDeque<Animation>()
    val isAnimating: Boolean get() = queue.isNotEmpty()

    fun play(animation: Animation) {
        queue.addLast(animation)
    }

    fun update(deltaTime: Float) {
        if (queue.isNotEmpty()) {
            val current = queue.first()
            current.update(deltaTime)
            if (current.isFinished) {
                current.onComplete()
                queue.removeFirst()
            }
        }
    }
}
```

### 2. Specific Game Animations
Create `src/merge_balls/Animations.kt`:
- **`MergeAnimation`**: Takes a list of `Box` instances and a target `(x, y)`. During `update`, it moves all boxes towards the target at a set speed (similar to `DROP`). When `isFinished` (all reach the target), `onComplete` removes the old boxes, adds the new merged box to the grid and sprite list, and triggers the next step.
- **`GravityAnimation`**: Takes a map/list of boxes and their target Y coordinates. Drops them at `fallingSpeed`. When finished, `onComplete` locks them into the grid and triggers the next step.

### 3. Refactoring `SpriteGrid.kt`
- Modify `merge()` so it doesn't immediately delete the boxes or create the new one in the grid. Instead, it returns a list of `MergeEvent` data classes (e.g., `data class MergeEvent(val boxesToMove: List<Box>, val targetX: Int, val targetY: Int, val newValue: Int)`).
- Modify `applyGravity()` to return a list of `GravityEvent` (e.g., `data class GravityEvent(val box: Box, val targetY: Int)`).

### 4. Updating `Environment.kt`
- Add an `animator = SequentialAnimator()` property.
- In `update(deltaTime)`, if `animator.isAnimating` is true, call `animator.update(deltaTime)` and skip normal game logic (falling controlled box, spawning new box).
- Refactor `performMerge()` into `checkBoardState()`. This function will check for merges. If merges exist, it queues a `MergeAnimation`. The `onComplete` of `MergeAnimation` will then call `checkGravity()`. `checkGravity()` queues a `GravityAnimation`. Its `onComplete` calls `checkBoardState()` again. This recursive loop continues until the board is stable, at which point `getRandomBox()` is finally called.

## Verification
- Run the game and perform a merge.
- Verify boxes glide to the merge point.
- Verify remaining boxes glide down to fill spaces.
- Verify a chain reaction (merge -> fall -> merge) happens sequentially and visually.
- Verify user input is ignored until all chain reactions finish and a new box is spawned.
