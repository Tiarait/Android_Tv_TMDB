package online.noxilab.tmdb.models

import java.io.Serializable

class ObjectSerializable(item: Any) : Serializable {
    var obj: Any? = item
}