
package com.legion2112.pokemonandroid

import android.location.Location

class  Pokemon{
    var name: String? = null
    var description: String? = null
    var power: Double? = null
    var location: Location? = null
    var isCatch: Boolean? = false
    var image: Int? = null

    constructor(name: String, description: String, power: Double, latitude: Double, longitude: Double, image: Int){
        this.name = name
        this.description = description
        this.power = power
        this.location = Location(name)
        this.location!!.latitude = latitude
        this.location!!.longitude = longitude
        this.isCatch = false
        this.image = image
    }
}