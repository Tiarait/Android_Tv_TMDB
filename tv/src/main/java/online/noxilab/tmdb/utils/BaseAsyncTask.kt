package online.noxilab.tmdb.utils

interface ProgressListener {
    // callback for start
    fun onStarted()

    // callback on success
    fun onCompleted()

}

interface CompletedListener {
    // callback on success
    fun onCompleted()

}
interface ListListener {
    // callback on success
    fun onResult(list: ArrayList<Any>)

}