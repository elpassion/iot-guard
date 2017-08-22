package pl.elpassion.iotguard.streaming

import android.app.Activity
import me.kevingleason.pnwebrtc.PnPeer
import me.kevingleason.pnwebrtc.PnRTCListener
import org.webrtc.MediaStream
import org.webrtc.VideoRenderer
import org.webrtc.VideoRendererGui

class RtcListener(private val activity: Activity,
                  private val localRenderer: VideoRenderer.Callbacks?,
                  private val remoteRenderer: VideoRenderer.Callbacks?) : PnRTCListener() {

    override fun onLocalStream(localStream: MediaStream) {
        super.onLocalStream(localStream)
        activity.runOnUiThread {
            localStream.videoTracks[0].addRenderer(VideoRenderer(localRenderer))
        }
    }

    override fun onAddRemoteStream(remoteStream: MediaStream, peer: PnPeer) {
        super.onAddRemoteStream(remoteStream, peer)
        activity.runOnUiThread {
            remoteStream.videoTracks[0].addRenderer(VideoRenderer(remoteRenderer))
            updateRenderer(remoteRenderer, 0, 0, 100, 100)
            updateRenderer(localRenderer, 72, 72, 25, 25)
        }
    }

    private fun updateRenderer(renderer: VideoRenderer.Callbacks?,
                               x: Int, y: Int, width: Int, height: Int) {
        val scalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL
        VideoRendererGui.update(renderer, x, y, width, height, scalingType, false)
    }

    override fun onMessage(peer: PnPeer?, message: Any?) {
        super.onMessage(peer, message)
    }

    override fun onPeerConnectionClosed(peer: PnPeer?) {
        super.onPeerConnectionClosed(peer)
        activity.finish()
    }
}