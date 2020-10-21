package online.noxilab.tmdb.models

import androidx.leanback.widget.ArrayObjectAdapter
import online.noxilab.tmdb.AppConstants

class RowObjectAdapter {
    var list: ArrayObjectAdapter = ArrayObjectAdapter()
    var page: Int = 0
    var type: Enum<AppConstants.TypeRows>? = null
    var header: String = ""
}