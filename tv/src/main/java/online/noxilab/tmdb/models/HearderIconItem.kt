package online.noxilab.tmdb.models

import androidx.leanback.widget.HeaderItem

class HeaderIconItem : HeaderItem {
    companion object {
        private val TAG = HeaderIconItem::class.java.simpleName
        val ICON_NONE = -1
    }
    /** Hold an icon resource id  */
    var iconResId = ICON_NONE

    @JvmOverloads
    constructor(id: Long, name: String, iconResId: Int = ICON_NONE) : super(id, name) {
        this.iconResId = iconResId
    }

    constructor(name: String) : super(name) {}
    constructor(name: String, iconResId: Int = ICON_NONE) : super(name) {
        this.iconResId = iconResId
    }
}