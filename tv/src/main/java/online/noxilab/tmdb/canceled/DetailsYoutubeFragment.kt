//package online.noxilab.tmdb.ui.fragments
//
//import androidx.fragment.app.Fragment
//import android.os.Bundle
//import android.view.ViewGroup
//import android.view.LayoutInflater
//import android.view.View
//import androidx.annotation.NonNull
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
//import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
//import online.noxilab.tmdb.R
//
//
//class DetailsYoutubeFragment: Fragment() {
//    lateinit var yotubeView: YouTubePlayerView
//    var mYouTubePlayer: YouTubePlayer? = null
//    var tracker: YouTubePlayerTracker? = null
//
//    fun newInstance(): DetailsYoutubeFragment {
//        val fragment = DetailsYoutubeFragment()
//        val args = Bundle()
////        args.putSerializable(PARAM_CARD, card)
//        fragment.arguments = args
//        return fragment
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
////        val arg = arguments
////        if (arg != null) {
////            card = arg.getSerializable(PARAM_CARD) as ObjectCard
////        }
//
//
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.detail_youtube_fragment, null)
//
//        yotubeView = view.findViewById(R.id.youtube_view)
//        lifecycle.addObserver(yotubeView)
//
//        tracker = YouTubePlayerTracker()
//        yotubeView.getPlayerUiController().showFullscreenButton(false)
//        yotubeView.getPlayerUiController().showVideoTitle(false)
//        yotubeView.getPlayerUiController().showUi(false)
//        yotubeView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
//            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
//                mYouTubePlayer = youTubePlayer
//                mYouTubePlayer?.addListener(tracker!!)
//            }
//        })
//        yotubeView.enterFullScreen()
//
//        return view
//    }
//
//    fun pause() {
//        mYouTubePlayer?.pause()
//    }
//
//    fun play() {
//        mYouTubePlayer?.play()
//    }
//}