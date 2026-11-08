package com.neonexalabs.playreview

import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot

/**
 * Google Play's rating card (the In-App Review API, com.google.android.play:review) for a Godot 4 game: one singleton, two methods, one signal.
 *
 * - `prepare()` asks Google for a ReviewInfo ahead of the moment (Google asks apps to, once they are sure to launch the flow soon).
 * - `launch()` launches the card with that ReviewInfo, asking for one first when there is none, and then says `review_flow_finished(ok)` exactly once.
 * - `review_flow_finished(ok)`: the flow is over. `ok` only says that nothing failed; Google never says whether the card was shown or what the player did.
 *
 * Nothing runs when the game opens: no Google code is called before `prepare()` or `launch()`. The plugin reads nothing, keeps nothing and sends nothing
 * of its own: the card and the review are the Play Store's.
 */
class PlayReviewPlugin(godot: Godot) : GodotPlugin(godot) {

    private var manager: ReviewManager? = null
    private var info: ReviewInfo? = null  // a prepared card, used by the next launch only
    private var requesting = false
    private var launchWanted = false  // launch() came while the ReviewInfo was being asked for

    override fun getPluginName(): String = "PlayReview"

    override fun getPluginSignals(): Set<SignalInfo> = setOf(SignalInfo("review_flow_finished", Boolean::class.javaObjectType))

    @UsedByGodot
    fun prepare() {
        runOnUiThread { request() }
    }

    @UsedByGodot
    fun launch() {
        runOnUiThread {
            val ready = info
            if (ready != null) {
                start(ready)
            } else {
                launchWanted = true
                request()
            }
        }
    }

    private fun reviewManager(): ReviewManager? {
        val host = activity ?: return null
        return manager ?: ReviewManagerFactory.create(host.applicationContext).also { manager = it }
    }

    private fun request() {
        if (requesting || info != null) {
            return
        }
        val reviews = reviewManager()
        if (reviews == null) {
            if (launchWanted) {
                launchWanted = false
                finish(false)
            }
            return
        }
        requesting = true
        reviews.requestReviewFlow().addOnCompleteListener { task ->
            requesting = false
            info = if (task.isSuccessful) task.result else null
            if (launchWanted) {
                launchWanted = false
                val ready = info
                if (ready != null) start(ready) else finish(false)
            }
        }
    }

    private fun start(ready: ReviewInfo) {
        info = null
        val host = activity
        val reviews = reviewManager()
        if (host == null || reviews == null) {
            finish(false)
            return
        }
        reviews.launchReviewFlow(host, ready).addOnCompleteListener { task -> finish(task.isSuccessful) }
    }

    private fun finish(ok: Boolean) {
        emitSignal("review_flow_finished", ok)
    }
}
