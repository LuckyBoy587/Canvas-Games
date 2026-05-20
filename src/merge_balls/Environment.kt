package merge_balls

import utils.Action
import utils.GameEnvironment
import utils.SequentialAnimator

class Environment(
    private val view: MergeBallsView,
    private val spriteGrid: SpriteGrid
) : GameEnvironment() {
    private var isGameOver = false
    private var nextValue: Int = generateNextValue()
    private var currentBox: Box = getNextControlledBox()
    private val fallingSpeed = 15f
    private val animator = SequentialAnimator()
    private var score = 0
    private var bestScore = 0
    private val particles = mutableListOf<Particle>()

    private fun generateNextValue(): Int {
        return if (Math.random() < 0.75) 2 else 4
    }

    private fun getNextControlledBox(): Box {
        var randomX: Int
        var attempts = 0
        do {
            randomX = (Math.random() * spriteGrid.width).toInt()
            attempts++
        } while (spriteGrid.get(randomX, 0) != null && attempts < 100)

        if (attempts >= 100) {
            isGameOver = true
            return Box(0f, 0f, 2)
        }

        val value = nextValue
        nextValue = generateNextValue()

        val box = Box(x = randomX.toFloat(), y = 0f, value = value)
        box.state = BoxState.CONTROLLED
        addSprite(box)
        // Note: We don't add to spriteGrid yet, it will be synced in update
        return box
    }

    override fun repaint() {
        view.refresh()
    }

    override fun onAction(action: Action) {
        if (action == Action.RESTART) {
            restart()
            return
        }
        if (isGameOver) return
        if (animator.isAnimating) return
        if (currentBox.state != BoxState.CONTROLLED) return

        if (action == Action.DROP) {
            currentBox.state = BoxState.FALLING
            currentBox.vy = fallingSpeed
            return
        }

        val (dx, dy) = when (action) {
            Action.MOVE_DOWN -> 0 to 1
            Action.MOVE_LEFT -> -1 to 0
            Action.MOVE_RIGHT -> 1 to 0
            else -> 0 to 0
        }

        if (dx != 0 || dy != 0) {
            val nextX = currentBox.x.toInt() + dx
            val nextY = currentBox.y.toInt() + dy

            if (spriteGrid.isValidCell(nextX, nextY) && spriteGrid.canMoveTo(nextX, nextY)) {
                currentBox.x = nextX.toFloat()
                currentBox.y = nextY.toFloat()
            } else if (action == Action.MOVE_DOWN) {
                currentBox.state = BoxState.LOCKED
                checkGameOver(currentBox)
                checkStableState()
            }
        }
    }

    override fun update(deltaTime: Float) {
        // Update particles first so they continue to animate smoothly under all conditions
        val particleIterator = particles.iterator()
        val drag = kotlin.math.exp(-3.0 * deltaTime).toFloat()
        while (particleIterator.hasNext()) {
            val p = particleIterator.next()
            p.update(deltaTime)
            p.vx *= drag
            p.vy *= drag
            p.life -= deltaTime
            if (p.life <= 0f) {
                particleIterator.remove()
            }
        }

        if (isGameOver) {
            view.updateSprites(spriteList.filterIsInstance<Box>(), isGameOver = true, particles = particles, nextValue = nextValue, score = score, bestScore = bestScore)
            return
        }

        if (animator.isAnimating) {
            animator.update(deltaTime)
            view.updateSprites(spriteList.filterIsInstance<Box>(), isGameOver = false, particles = particles, nextValue = nextValue, score = score, bestScore = bestScore)
            return
        }

        super.update(deltaTime)
        
        // Handle falling boxes and collisions
        var landed = false
        val boxes = spriteList.filterIsInstance<Box>()
        boxes.forEach { box ->
            if (box.state == BoxState.FALLING) {
                val nextGridY = (box.y + 0.1f).toInt() + 1
                if (nextGridY >= spriteGrid.height || spriteGrid.get(box.x.toInt(), nextGridY) != null) {
                    // Check if we've reached the integer position of the current or next cell
                    if (box.y >= nextGridY - 1) {
                        box.y = (nextGridY - 1).toFloat()
                        box.vy = 0f
                        box.state = BoxState.LOCKED
                        landed = true
                        checkGameOver(box)
                    }
                }
            }
        }

        if (landed) {
            checkStableState()
        }

        val ghostX = currentBox.x.toInt()
        val ghostY = if (currentBox.state == BoxState.CONTROLLED || currentBox.state == BoxState.FALLING) {
            val ly = spriteGrid.getLandingY(ghostX, currentBox.y.toInt())
            if (ly > currentBox.y.toInt()) ly else null
        } else {
            null
        }
        val ghostValue = if (ghostY != null) currentBox.value else null

        syncSpriteListToGrid()
        view.updateSprites(spriteList.filterIsInstance<Box>(), isGameOver = isGameOver, ghostX = ghostX, ghostY = ghostY, ghostValue = ghostValue, particles = particles, nextValue = nextValue, score = score, bestScore = bestScore)
    }

    private fun spawnMergeExplosion(targetX: Int, targetY: Int, newValue: Int, sourceColors: List<java.awt.Color>) {
        val count = when {
            newValue <= 4 -> 12
            newValue <= 16 -> 18
            newValue <= 64 -> 24
            newValue <= 256 -> 32
            else -> 42
        }

        val spawnX = targetX + 0.5f
        val spawnY = targetY + 0.5f
        val newColor = Box(0f, 0f, newValue).color

        for (i in 0 until count) {
            val angle = (Math.PI * 2 * i / count) + (Math.random() - 0.5) * 0.25
            val speed = 3f + Math.random().toFloat() * 6f
            val vx = (kotlin.math.cos(angle) * speed).toFloat()
            val vy = (kotlin.math.sin(angle) * speed).toFloat()

            // 40% merged color, 30% source color, 30% white/gold energy spark
            val color = when (Math.random()) {
                in 0.0..0.4 -> newColor
                in 0.4..0.7 -> sourceColors.random()
                else -> java.awt.Color(255, 220 + (Math.random() * 35).toInt(), 140 + (Math.random() * 110).toInt())
            }

            particles.add(Particle(
                x = spawnX,
                y = spawnY,
                vx = vx,
                vy = vy,
                ax = 0f,
                ay = -2f, // upward drift
                color = color,
                maxLife = 0.35f + Math.random().toFloat() * 0.4f,
                initialSize = 0.12f + Math.random().toFloat() * 0.08f
            ))
        }
    }

    private fun checkStableState() {
        if (isGameOver) return
        syncSpriteListToGrid()
        val merges = spriteGrid.checkMerges(currentBox.x.toInt(), currentBox.y.toInt())
        if (merges.isNotEmpty()) {
            val event = merges[0] // Handle one merge event at a time for simplicity and better visual
            
            // Remove merging boxes from grid immediately before they start moving
            for (box in event.boxes) {
                spriteGrid.remove(box.x.toInt(), box.y.toInt())
            }

            animator.play(MergeAnimation(event.boxes, event.targetX, event.targetY, { p -> particles.add(p) }) {
                // Remove old boxes from sprite list
                for (box in event.boxes) {
                    spriteList.remove(box)
                }

                // Trigger fusion explosion burst
                spawnMergeExplosion(event.targetX, event.targetY, event.newValue, event.boxes.map { it.color })

                // Add new box
                val mergedBox = Box(event.targetX.toFloat(), event.targetY.toFloat(), event.newValue)
                mergedBox.state = BoxState.LOCKED
                spriteGrid.place(mergedBox, event.targetX, event.targetY)
                spriteList.add(mergedBox)

                score += event.newValue
                if (score > bestScore) {
                    bestScore = score
                }
                animator.play(PopAnimation(mergedBox) {
                    animator.play(WaitAnimation(0.1f) {
                        checkStableState()
                    })
                })
            })
            return
        }

        val gravityEvents = spriteGrid.checkGravity()
        if (gravityEvents.isNotEmpty()) {
            // Remove boxes from their old positions in grid immediately to avoid collisions during check
            for (event in gravityEvents) {
                spriteGrid.remove(event.box.x.toInt(), event.box.y.toInt())
            }
            animator.play(GravityAnimation(gravityEvents) {
                // Place boxes in their new positions in grid
                for (event in gravityEvents) {
                    spriteGrid.set(event.box.x.toInt(), event.targetY, event.box)
                }
                animator.play(WaitAnimation(0.1f) {
                    checkStableState()
                })
            })
            return
        }

        // If we reach here, board is stable
        if (currentBox.state == BoxState.LOCKED) {
            currentBox = getNextControlledBox()
        }
    }

    private fun checkGameOver(landedBox: Box) {
        if (landedBox.y.toInt() == 0) {
            isGameOver = true
        }
    }

    private fun restart() {
        isGameOver = false
        score = 0
        spriteGrid.clear()
        spriteList.clear()
        animator.clear()
        particles.clear()
        nextValue = generateNextValue()
        currentBox = getNextControlledBox()
        view.updateSprites(spriteList.filterIsInstance<Box>(), isGameOver = false, particles = particles, nextValue = nextValue, score = score, bestScore = bestScore)
    }

    private fun syncSpriteListToGrid() {
        spriteGrid.clear()
        spriteList.forEach { sprite ->
            if (sprite is Box && sprite.state == BoxState.LOCKED) {
                spriteGrid.set(sprite.x.toInt(), sprite.y.toInt(), sprite)
            }
        }
    }
}
