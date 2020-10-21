package online.noxilab.tmdb.models

import com.fasterxml.jackson.annotation.JsonProperty
import info.movito.themoviedbapi.model.Video
import info.movito.themoviedbapi.model.core.IdElement

class VideoResults : IdElement() {

    @JsonProperty("results")
    var videos: List<Video>? = null
}