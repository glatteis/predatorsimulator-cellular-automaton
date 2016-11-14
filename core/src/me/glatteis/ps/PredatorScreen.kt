package me.glatteis.ps

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import java.util.*

/**
 * Created by Linus on 28.09.2016!
 */
class PredatorScreen : Screen {

    val camera = OrthographicCamera()
    val viewport = ExtendViewport(100F, 100F, camera)

    val sizeX = 50
    val sizeY = 50

    val shapeRenderer = ShapeRenderer()

    val entities = HashMap<Pair<Int, Int>, AnimalType>()

    init {

        //Create "animal types" used in this simulation

        val red = AnimalType(Color.RED)
        val green = AnimalType(Color.LIME)
        val blue = AnimalType(Color.BLUE)
        val yellow = AnimalType(Color.YELLOW)
        val gray = AnimalType(Color.DARK_GRAY)

        //Randomly scatter them along the board

        val allAnimals = listOf(red, green, blue, yellow, gray)
        for (x in -sizeX..sizeX) {
            for (y in -sizeY..sizeY) {
                entities[Pair(x, y)] = allAnimals[MathUtils.random(allAnimals.size - 1)]
            }
        }

        //One color will be the predator to two other ones

        red.predatorTo.add(gray)
        red.predatorTo.add(yellow)
        green.predatorTo.add(red)
        green.predatorTo.add(blue)
        yellow.predatorTo.add(green)
        yellow.predatorTo.add(blue)
        blue.predatorTo.add(red)
        blue.predatorTo.add(gray)
        gray.predatorTo.add(yellow)
        gray.predatorTo.add(green)
    }

    //Update timing values
    var updateAccumulator = 0F
    val updateSpeed = 1 / 2F

    override fun render(delta: Float) {
        //Render the whole thing

        //Initialize ShapeRenderer
        shapeRenderer.projectionMatrix = camera.projection
        shapeRenderer.transformMatrix = camera.view
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        //Iterate through every position and draw the entity in its position
        for ((position, entity) in entities) {
            entity.draw(shapeRenderer, position)
        }

        //End the ShapeRenderer
        shapeRenderer.end()

        //Update the stuff
        updateAccumulator += delta
        while (updateAccumulator > updateSpeed) {
            updateAnimals()
            updateAccumulator -= updateSpeed
        }
    }


    val updatedEntities = HashMap<Pair<Int, Int>, AnimalType>()
    val emptyLand = HashSet<Pair<Int, Int>>()
    val countedNeighbors = LinkedHashMap<AnimalType, Int>()
    
    fun updateAnimals() {
        updatedEntities.clear()
        emptyLand.clear()

        //Count neighboring predators of every entity, kill the entity if it is >= 3
        for ((position, entity) in entities) {
            var predatorCount = 0
            for (x in -1..1) {
                for (y in -1..1) {
                    if (x == 0 && y == 0) continue
                    val neighboringEntity = entities[Pair(position.first + x, position.second + y)] ?: continue
                    if (neighboringEntity.isPredatorTo(entity)) {
                        predatorCount++
                    }
                }
            }
            if (predatorCount < 3) {
                updatedEntities.put(position, entity)
            } else {
                emptyLand.add(Pair(position.first, position.second))
            }
        }

        //Go through every position that is now empty and throw the dice, give it to one of the entities around
        //If an AnimalType has 3 entities around the empty field it will get a 3/9 chance of winning and so forth
        for (position in emptyLand) {
            countedNeighbors.clear()
            for (x in -1..1) {
                for (y in -1..1) {
                    if (x == 0 && y == 0) continue
                    val neighbor = entities[Pair(position.first + x, position.second + y)]
                    if (neighbor != null) {
                        countedNeighbors.put(neighbor, (countedNeighbors[neighbor] ?: 0) + 1)
                    }
                }
            }
            var score = 0
            for (s in countedNeighbors.values) {
                score += s
            }
            val random = MathUtils.random(score - 1)
            score = 0
            for ((animal, s) in countedNeighbors) {
                score += s
                if (score >= random) {
                    updatedEntities.put(position, animal)
                    break
                }
            }
        }

        //Replace entities with updated entities
        entities.clear()
        entities.putAll(updatedEntities)
    }
    
    override fun resize(width: Int, height: Int) {
        //Update window size on resize
        viewport.update(width, height)
    }

    override fun show() {
    }

    override fun pause() {
    }

    override fun hide() {
    }


    override fun resume() {
    }

    override fun dispose() {
    }
}
