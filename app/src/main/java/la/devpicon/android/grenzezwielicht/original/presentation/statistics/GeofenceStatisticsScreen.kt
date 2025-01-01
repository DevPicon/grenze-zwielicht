package la.devpicon.android.grenzezwielicht.original.presentation.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import la.devpicon.android.grenzezwielicht.latest.ui.theme.GrenzezwielichtTheme
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeofenceStatisticsScreen(
    statisticsViewModel: StatisticsViewModel = viewModel(),
    popBackStack: () -> Unit,
    shareStatistics: (String) -> Unit,
    modifier: Modifier = Modifier) {

    LaunchedEffect(Unit) {
        statisticsViewModel.loadData()
    }

    val state: StatisticsState by statisticsViewModel.viewState.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Statistics"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = popBackStack,
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = ""
                            )
                        }
                    )
                },
                actions = {
                    if (state is StatisticsViewState) {
                        IconButton(onClick = {
                            val statistics = (state as StatisticsViewState).statisticList
                            val csvData = statisticsViewModel.statisticsToCsv(statistics)
                            shareStatistics(csvData)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Statistics"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        StatisticsBody(paddingValues, state, modifier)
    }
}



@Composable
private fun StatisticsBody(
    paddingValues: PaddingValues,
    state: StatisticsState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        when (val currentState = state) {
            is LoadingState -> {}
            is StatisticsViewState -> {
                LazyColumn(
                    modifier = modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(currentState.statisticList) { item ->
                        when (item) {
                            is Statistic -> StatisticRow(
                                label = item.label,
                                count = item.count,
                                percentage = item.percentage
                            )
                            is Spacer -> Spacer(modifier = Modifier.size(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticRow(
    modifier: Modifier = Modifier,
    label: String,
    count: Int,
    percentage: Float
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label,
            textAlign = TextAlign.Left,
            modifier = modifier.weight(
                weight = 1f, fill = true
            )
        )
        Text(text = "$count",
            modifier = modifier.width(48.dp), textAlign = TextAlign.Right)
        Spacer(modifier = modifier.size(8.dp))
        Text(text = "→", style = MaterialTheme.typography.labelMedium, modifier = Modifier.align(Alignment.CenterVertically))
        Spacer(modifier = modifier.size(8.dp))
        Text(text = percentage.toPercentageString(),
            modifier = modifier.width(48.dp), textAlign = TextAlign.Right

        )
    }
}

@Composable
fun Float.toPercentageString(): String {
    val percentage = ceil(this * 100).toInt()
    return "$percentage%"
}

@Preview
@Composable
private fun StatisticRowPreview() {
    GrenzezwielichtTheme {
        StatisticRow(
            label = "Enter",
            count = 0,
            percentage = 0f
        )
    }
}

@Preview
@Composable
private fun GeofenceStatisticsScreenPreview() {
    GrenzezwielichtTheme {
        StatisticsBody(
            paddingValues = PaddingValues(8.dp),
            state = StatisticsViewState(
                listOf()
            ),
        )
    }
}