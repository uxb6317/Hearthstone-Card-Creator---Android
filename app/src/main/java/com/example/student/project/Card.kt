package com.example.student.project

import android.graphics.Bitmap
import java.io.Serializable

class Card(var className: String = "Neutral",
           var rarity: String = "Collectable",
           var attack: String = "",
           var cost: String = "",
           var health: String = "",
           var name: String = "",
           var description: String = "",
           var race: String = "",
           var durability: String = "",
           var type: String = "Minion",
           var image: Bitmap? = null) : Cloneable, Serializable {

    override public fun clone(): Any {
        return super.clone()
    }
}