package la.devpicon.android.grenzezwielicht.original.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import la.devpicon.android.grenzezwielicht.original.data.repository.GeofenceRepository
import la.devpicon.android.grenzezwielicht.original.data.repository.GeofenceStatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class StatisticsViewModel @Inject constructor(
    private val statsRepository: GeofenceStatsRepository,
    private val geofenceRepository: GeofenceRepository
) : ViewModel() {
    private val _viewState = MutableStateFlow<StatisticsState>(LoadingState)
    val viewState = _viewState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _viewState.value = LoadingState
            combine(
                geofenceRepository
                    .retrieveAllGeofenceEventEntryList(),
                statsRepository
                    .retrieveStats()
            ) { geofenceEntries, stats ->
                Pair(geofenceEntries, stats)
            }
                .map { pair ->
                    val geofenceEvents = pair.first
                    val statistics = pair.second

                    val eventTotal = geofenceEvents.count()
                    val enterEventCount = geofenceEvents.count { it.transitionType == "GEOFENCE_TRANSITION_ENTER" }
                    val exitEventCount = geofenceEvents.count { it.transitionType == "GEOFENCE_TRANSITION_EXIT" }
                    val highAccurateFeedbackEnterEventCount = statistics.count { it.transitionType == "GEOFENCE_TRANSITION_ENTER" && it.feedback }
                    val lowAccurateFeedbackEnterEventCount = statistics.count { it.transitionType == "GEOFENCE_TRANSITION_ENTER" && !it.feedback }
                    val highAccurateFeedbackExitEventCount = statistics.count { it.transitionType == "GEOFENCE_TRANSITION_EXIT" && it.feedback }
                    val lowAccurateFeedbackExitEventCount = statistics.count { it.transitionType == "GEOFENCE_TRANSITION_EXIT" && !it.feedback }
                    val highAccurateFeedbackTotalCount = statistics.count { it.feedback }
                    val lowAccurateFeedbackTotalCount = statistics.count { !it.feedback }
                    val feedbackTotal = statistics.count()

                    val statisticList: MutableList<StatisticRow> = mutableListOf(
                        Statistic("Enter:", enterEventCount, enterEventCount * 1f / eventTotal),
                        Statistic("Exit:", exitEventCount, exitEventCount * 1f / eventTotal),
                        Statistic("Total events:", enterEventCount, 1f),
                        Spacer,
                        Statistic("Enter event / Good", highAccurateFeedbackEnterEventCount, highAccurateFeedbackEnterEventCount * 1f / feedbackTotal),
                        Statistic("Exit event / Good", highAccurateFeedbackExitEventCount, highAccurateFeedbackExitEventCount * 1f / feedbackTotal),
                        Statistic("Enter event / Bad", lowAccurateFeedbackEnterEventCount, lowAccurateFeedbackEnterEventCount * 1f / feedbackTotal),
                        Statistic("Exit event / Bad", lowAccurateFeedbackExitEventCount, lowAccurateFeedbackExitEventCount * 1f / feedbackTotal),
                        Statistic("Total Good", highAccurateFeedbackTotalCount, highAccurateFeedbackTotalCount * 1f / feedbackTotal),
                        Statistic("Total Bad", lowAccurateFeedbackTotalCount, lowAccurateFeedbackTotalCount * 1f / feedbackTotal),
                        Spacer,
                        Statistic("Total", feedbackTotal, 1f),
                    )
                    statisticList
                }
                .collect { statisticList ->
                    _viewState.update {
                        StatisticsViewState(
                            statisticList = statisticList
                        )
                    }
                }
        }
    }

    fun statisticsToCsv(statistics: List<StatisticRow>): String {
        val header = "Label, Count, Percentage"
        val rows = statistics
            .filterIsInstance<Statistic>()
            .joinToString(separator = "\n") { statistic ->
                "${statistic.label}, ${statistic.count}, ${"%.2f".format(statistic.percentage * 100)}"
            }

        return "$header\n$rows"
    }

}

sealed interface StatisticsState
data object LoadingState : StatisticsState
data class StatisticsViewState(
    val statisticList: List<StatisticRow>
) : StatisticsState

sealed interface StatisticRow
data class Statistic(val label: String, val count: Int, val percentage: Float) : StatisticRow
data object Spacer : StatisticRow