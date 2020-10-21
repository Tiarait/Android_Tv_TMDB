package online.noxilab.tmdb

object AppConstants {
    const val API_TMDB = "[YOUR TMDB KEY]"

    const val ITEM = "item"
    const val TMDB = "tmdb"
    const val TRANSITION_POSTER = "TRANSITION_POSTER"

    const val DEFAULT_LANG = "ru"

    const val CONTENT = "content://"
    const val SLASH = "/"
    const val DOT = "."
    const val ELLIPSIS = "…"//… ⋮ ⋯ ⋰ ⋱ ∴ ∵
    const val SLASH_HASH = "/#"
    const val SLASH_ASTERISK = "/*"
    const val SLASH_DELETE = "/delete"

    const val DOT_DELIMETERSPACE = " • " //•·●▪■●♦°
    const val DOT_DELIMETER = "•"
    const val DOT_DELIMETERLEFTSPACE = " •"
    const val COLON_DELIMETER = ": "
    const val COLON = ": "
    const val COMMA = ", "
    const val SLASHSPACE = " / "
    const val SPACE = " "
    const val SPACE_TEXT = "SPACE"
    const val NEWLINE = "\n"

    const val HTML_BR = "<br>"

    enum class TypeRows{
        MOVIE_NOW, MOVIE_UPCOMING, MOVIE_POPULAR, MOVIE_TOP,
        SERIAL_TODAY, SERIAL_ONTV, SERIAL_POPULAR, SERIAL_TOP,
        PEOPLE
    }
}