package online.noxilab.tmdb.utils

import android.os.AsyncTask

class DoAsync(private val pListener: ProgressListener?,
              private val cListener: CompletedListener?,  val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
    constructor(handler: () -> Unit) : this(null, null, handler)
    constructor(listener: CompletedListener, handler: () -> Unit) : this(null, listener, handler)
    constructor(listener: ProgressListener, handler: () -> Unit) : this(listener, null, handler)

    init {
        execute()
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        pListener?.onCompleted()
        cListener?.onCompleted()
    }

    override fun onPreExecute() {
        super.onPreExecute()
        pListener?.onStarted()
    }

    override fun doInBackground(vararg params: Void?): Void? {
        handler()
        return null
    }
}