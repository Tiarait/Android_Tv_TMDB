package online.noxilab.tmdb.ui.fragments

import android.animation.Animator
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.util.Log
import android.widget.*
import online.noxilab.tmdb.ui.activities.DetailsActivity
import online.noxilab.tmdb.R
import online.noxilab.tmdb.utils.UtilsText


class DetailsVideoFragment: Fragment() {
    private lateinit var videoView: VideoView
    private lateinit var iconView: ImageView
    private lateinit var textView: TextView
    private lateinit var curtimeView: TextView
    private lateinit var durationView: TextView
    private lateinit var seekContainerView: View
    private lateinit var seekBar: SeekBar
    private var curtime = 0
    private var mp: MediaPlayer? = null
    private val handler = Handler()
    private val runnableIcon = Runnable {
        iconView.animate()
            .alpha(0f)
            .setDuration(200)
            .start()
    }
    private val runnableText = Runnable {
        textView.animate()
            .alpha(0f)
            .setDuration(200)
            .start()
    }
    private val runnableSeek = Runnable {
        seekContainerView.animate()
            .alpha(0f)
            .setDuration(400)
            .start()
    }
    private val onEverySecond = object : Runnable {
        override fun run() {
            seekBar.progress = videoView.currentPosition
            curtimeView.text = UtilsText().msToStringTime(videoView.currentPosition)
            checkMute()
            if (isPlaying()) {
                seekBar.postDelayed(this, 1000)
            }

        }
    }

    private fun checkMute() {
        if (activity is DetailsActivity) {
            if ((activity as DetailsActivity).detailsHiden)
                unmute()
            else mute()
        }
    }

    fun newInstance(): DetailsVideoFragment {
        val fragment = DetailsVideoFragment()
        val args = Bundle()
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.detail_video_fragment, null)
        videoView = view.findViewById(R.id.video_view)
        iconView = view.findViewById(R.id.video_action_icon)
        textView = view.findViewById(R.id.video_action_text)
        durationView = view.findViewById(R.id.video_duration)
        curtimeView = view.findViewById(R.id.video_curtime)
        seekContainerView = view.findViewById(R.id.video_seek_container)
        seekBar = view.findViewById(R.id.video_seekbar)

        curtimeView.text = "00:00"
        durationView.text = "00:00"
        seekContainerView.alpha = 0f

        videoView.setOnPreparedListener { mPlayer ->
            mp = mPlayer
            mute()

            seekBar.max = videoView.duration
            seekBar.postDelayed(onEverySecond, 1000)
            durationView.text = UtilsText().msToStringTime(videoView.duration)

            mp?.setOnBufferingUpdateListener { mp, percent ->
                val p = percent*seekBar.max/100
                if (p < seekBar.max) {
                    seekBar.secondaryProgress = p
                } else
                    seekBar.secondaryProgress = seekBar.max
            }
        }
        videoView.setOnErrorListener { mp, what, extra ->
            if (activity is DetailsActivity) {
                (activity as DetailsActivity).progressMain.visibility = View.GONE
                (activity as DetailsActivity).bgContainerView.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()
            }
            true
        }

        return view
    }

    fun setVideo(url: String) {
        videoView.setVideoPath(url)
        mute()
        videoView.start()
        mp?.start()
        mute()

        seekBar.max = videoView.duration
        durationView.text = UtilsText().msToStringTime(videoView.duration)
        seekContainerView.alpha = 0f

        mp?.setOnCompletionListener {
            setVideo(url)
        }
        if (activity is DetailsActivity)
            (activity as DetailsActivity).bgContainerView.animate()
                .alpha(0f)
                .setDuration(200)
                .start()

    }

    private fun showCenterIcon(icPlayerActionPause: Int) {
        iconView.setImageResource(icPlayerActionPause)
        textView.alpha = 0f
        iconView.animate().cancel()
        handler.removeCallbacks(runnableIcon)
        handler.removeCallbacks(runnableText)
        iconView.animate()
            .alpha(1f)
            .setDuration(200)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    handler.postDelayed(runnableIcon, 1500)
                }

                override fun onAnimationCancel(animator: Animator) {
                    iconView.alpha = 0f
                    handler.removeCallbacks(runnableIcon)
                    handler.removeCallbacks(runnableText)
                }

                override fun onAnimationRepeat(animator: Animator) {

                }
            })
            .start()
    }

    private fun showCenterText(s: String) {
        textView.text = s
        textView.animate().cancel()
        iconView.alpha = 0f
        handler.removeCallbacks(runnableIcon)
        handler.removeCallbacks(runnableText)
        textView.animate()
            .alpha(1f)
            .setDuration(200)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    handler.postDelayed(runnableText, 1500)
                }

                override fun onAnimationCancel(animator: Animator) {
                    textView.alpha = 0f
                    handler.removeCallbacks(runnableIcon)
                    handler.removeCallbacks(runnableText)
                }

                override fun onAnimationRepeat(animator: Animator) {

                }
            })
            .start()
    }

    private fun showSeekContainer() {
        handler.removeCallbacks(runnableSeek)
        seekContainerView.animate().cancel()
        seekContainerView.animate()
            .alpha(1f)
            .setDuration(400)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    handler.postDelayed(runnableSeek, 5000)
                }

                override fun onAnimationCancel(animator: Animator) {
                    seekContainerView.alpha = 1f
                    handler.removeCallbacks(runnableSeek)
                }

                override fun onAnimationRepeat(animator: Animator) {

                }
            })
            .start()
    }

    fun isPlaying(): Boolean {
        return videoView.isPlaying
    }

    fun pause() {
        pause(true)
    }

    fun pause(b: Boolean) {
        if (isPlaying()) {
            showSeekContainer()
            mp?.pause()
            curtime = mp!!.currentPosition
            checkMute()
//        Log.e("test","pause curtime="+mp!!.currentPosition)
            if (b) showCenterIcon(R.drawable.ic_player_action_pause)
        }
    }

    fun play() {
        play(true)
        seekBar.postDelayed(onEverySecond, 1000)
    }

    fun play(b: Boolean) {
        mp?.start()
        checkMute()
//        Log.e("test","play curtime="+mp!!.currentPosition)
        if (b) showCenterIcon(R.drawable.ic_player_action_play)
    }

    fun mute() {
        if (isPlaying()) {
            mp?.setVolume(0f, 0f)
        }
    }

    fun unmute() {
        if (isPlaying()) {
            mp?.setVolume(1f, 1f)
        }
    }

    fun seek() {
        seekBar.progress = curtime
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mp?.seekTo(curtime.toLong(), MediaPlayer.SEEK_CLOSEST)
        } else mp?.seekTo(curtime)
        curtimeView.text = UtilsText().msToStringTime(curtime)
//        Log.e("test","seek curtime="+curtime)
    }

    fun seekPlus(ms: Int) {
        if (mp != null) {
            if(mp!!.duration > mp!!.currentPosition + ms) {
                showSeekContainer()
                curtime = mp!!.currentPosition + ms
                showCenterText("+${ms/1000}s")
                seek()
            }
        }
    }

    fun seekMinus(ms: Int) {
        if (mp != null) {
            showSeekContainer()
            if (mp!!.currentPosition > ms) {
                curtime = mp!!.currentPosition - ms
                showCenterText("-${ms/1000}s")
            } else {
                curtime = 0
                showCenterText("-${(ms-mp!!.currentPosition)/1000}s")
            }
            seek()
        }
    }
}