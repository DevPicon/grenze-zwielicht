package la.devpicon.android.grenzezwielicht.original.userfeedback

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import la.devpicon.android.grenzezwielicht.original.ACTION_THUMB_DOWN
import la.devpicon.android.grenzezwielicht.original.ACTION_THUMB_UP
import la.devpicon.android.grenzezwielicht.original.EXTRA_NOTIFICATION_ID
import la.devpicon.android.grenzezwielicht.original.EXTRA_TRANSITION_TYPE
import la.devpicon.android.grenzezwielicht.original.EXTRA_TRANSITION_UUID
import la.devpicon.android.grenzezwielicht.original.Feedback
import la.devpicon.android.grenzezwielicht.original.data.repository.GeofenceRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationFeedbackActionReceiver : BroadcastReceiver() {
    private val job = Job()
    private val dispatchers: Dispatchers by lazy { Dispatchers }
    private val scope = CoroutineScope(job + dispatchers.IO)

    @Inject
    lateinit var repository: GeofenceRepository

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val transitionType = intent.getIntExtra(EXTRA_TRANSITION_TYPE, -1)
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
        val transitionUuid = intent.getStringExtra(EXTRA_TRANSITION_UUID).orEmpty()

        // Dismiss the notification if the ID is valid
        if (notificationId != -1) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
        }

        when (action) {
            ACTION_THUMB_UP -> {
                scope.launch {
                    repository.insertFeedback(
                        isAccurate = true,
                        transitionType = transitionType
                    )
                    repository.updateTransitionFeedback(
                        geofenceTransitionUuid = transitionUuid,
                        feedback = Feedback.GOOD
                    )
                }
            }
            ACTION_THUMB_DOWN -> {
                scope.launch {
                    repository.insertFeedback(
                        isAccurate = false,
                        transitionType = transitionType
                    )
                    repository.updateTransitionFeedback(
                        geofenceTransitionUuid = transitionUuid,
                        feedback = Feedback.BAD
                    )
                }
            }
        }

    }
}