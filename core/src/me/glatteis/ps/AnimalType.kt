package me.glatteis.ps

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import java.util.*

/**
 * Created by Linus on 28.09.2016!
 */
class AnimalType(val color: Color) {

    //Which animals this thing will kill
    val predatorTo = HashSet<AnimalType>()

    //Draw the entity as a rectangle using the ShapeRenderer
    fun draw(shapeRenderer: ShapeRenderer, position: Pair<Int, Int>) {
        shapeRenderer.color = color
        shapeRenderer.rect(position.first.toFloat(), position.second.toFloat(), 1F, 1F)
    }

    fun isPredatorTo(animalType: AnimalType): Boolean {
        return predatorTo.contains(animalType)
    }

}