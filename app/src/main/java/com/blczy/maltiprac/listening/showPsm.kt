package com.blczy.maltiprac.listening

import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.blczy.maltiprac.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode

class PsmViewModel : ViewModel() {
    private val _stops = MutableStateFlow<List<Stop>>(emptyList())
    val stops: StateFlow<List<Stop>> = _stops

    private val _audioPath = MutableStateFlow<String?>(null)
    val audioPath: StateFlow<String?> = _audioPath

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadPsm(context: android.content.Context, id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val psm = Psm(context, id)
                val (stops, audioPath) = psm.getPsm()
                _stops.value = stops
                _audioPath.value = audioPath
            } catch (e: Exception) {
                Log.e("PsmViewModel", "Error loading PSM", e)
                _error.value = "Failed to load PSM: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

@Composable
fun ShowPsm(id: Int, viewModel: PsmViewModel = viewModel()) {
    val context = LocalContext.current
    val stops by viewModel.stops.collectAsState()
    val audioPath by viewModel.audioPath.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Media player state
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var hasCompleted by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableFloatStateOf(0f) }
    var duration by remember { mutableIntStateOf(0) }
    var currentSentenceIndex by remember { mutableIntStateOf(-1) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }

    // Load PSM when composable is first created
    LaunchedEffect(id) {
        viewModel.loadPsm(context, id)
    }

    // Initialize media player when audio path is available
    LaunchedEffect(audioPath) {
        audioPath?.let { path ->
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(path)
                    prepare()
                    duration = this.duration
                    setOnCompletionListener {
                        isPlaying = false
                        hasCompleted = true
                        currentSentenceIndex = -1
                    }
                }
            } catch (e: IOException) {
                Log.e("ShowPsm", "Error initializing MediaPlayer", e)
            }
        }
    }

    // Progress tracking
    LaunchedEffect(isPlaying) {
        if (isPlaying && mediaPlayer != null) {
            hasCompleted = false

            while (isActive && isPlaying) {
                val currentPosition = mediaPlayer?.currentPosition ?: 0
                currentProgress = if (duration > 0) {
                    currentPosition.toFloat() / duration
                } else {
                    0f
                }

                println(currentPosition)

                // Update current sentence based on audio position
                val positionInSeconds = currentPosition / 1000.0
                val newSentenceIndex = stops.indexOfLast { it.audio_stop < positionInSeconds }
                if (newSentenceIndex != -1 && newSentenceIndex != currentSentenceIndex) {
                    currentSentenceIndex = newSentenceIndex
                }

                delay(100)
            }
        }
    }

    // Clean up media player when composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.apply {
                stop()
                release()
            }
            mediaPlayer = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            error != null -> {
                Text(
                    text = error ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            stops.isEmpty() -> {
                Text(
                    text = "No data available for PSM #$id",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                // Main content
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Sentence list
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        items(stops) { stop ->
                            val isCurrentSentence = stops.indexOf(stop) == currentSentenceIndex

                            SentenceButton(
                                sentence = stop.sentence,
                                isHighlighted = isCurrentSentence,
                                onClick = {
                                    mediaPlayer?.let { player ->
                                        val positionMs = (stop.audio_stop * 1000).toInt()
                                        player.seekTo(positionMs + 50)
                                        if (!isPlaying) {
                                            player.start()
                                            isPlaying = true
                                        }
                                        currentSentenceIndex = stops.indexOf(stop)
                                    }
                                }
                            )
                        }
                    }

                    // Audio player
                    AudioPlayer(
                        isPlaying = isPlaying,
                        hasCompleted = hasCompleted,
                        currentProgress = currentProgress,
                        playbackSpeed = playbackSpeed,
                        length = duration,
                        onPlayPauseClick = {
                            mediaPlayer?.let { player ->
                                if (isPlaying) {
                                    player.pause()
                                    isPlaying = false
                                } else {
                                    if (hasCompleted) {
                                        player.seekTo(0)
                                        hasCompleted = false
                                    }
                                    player.start()
                                    isPlaying = true
                                }
                            }
                        },
                        onSeek = { position ->
                            if (!isPlaying) {
                                if (hasCompleted) {
                                    mediaPlayer?.seekTo(0)
                                    hasCompleted = false
                                }
                                mediaPlayer?.start()
                                isPlaying = true
                            }
                            mediaPlayer?.let { player ->
                                val seekPosition = (position * duration).toInt()
                                player.seekTo(seekPosition)
                            }
                        },
                        onJumpForward = {
                            mediaPlayer?.let { player ->
                                val newPosition = minOf(player.currentPosition + 10000, duration)
                                player.seekTo(newPosition)
                            }
                        },
                        onJumpBackward = {
                            mediaPlayer?.let { player ->
                                val newPosition = maxOf(player.currentPosition - 10000, 0)
                                player.seekTo(newPosition)
                            }
                        },
                        onSpeedChange = { speed ->
                            playbackSpeed = speed
                            mediaPlayer?.playbackParams =
                                mediaPlayer?.playbackParams?.setSpeed(speed) ?: return@AudioPlayer
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun SentenceButton(
    sentence: String,
    isHighlighted: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val openUrl: (String) -> Unit = { url ->
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = url.toUri()
        }
        context.startActivity(intent)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isHighlighted) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .padding(16.dp)
            .clickable(enabled = true, onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selectable Text
            Box(modifier = Modifier.weight(1f)) {
                SelectableText(
                    text = sentence,
                    color = if (isHighlighted) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    openUrl = openUrl,
                )
            }

            // Icon Button
            IconButton(
                onClick = onClick,
                modifier = Modifier.semantics {
                    contentDescription = "Play sentence"
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.VolumeUp,
                    contentDescription = null, // already defined in semantics
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SelectableText(
    text: String,
    color: Color,
    openUrl: (String) -> Unit,
) {
    val wiktionarySearchTitle = stringResource(R.string.wiktionary)
    val verbMtSearchTitle = stringResource(R.string.verb_mt)
    AndroidView(factory = { context ->
        TextView(context).apply {
            setText(text)
            setTextColor(color.toArgb())
            setBackgroundColor(Color.Transparent.toArgb())
            isFocusable = false
            isFocusableInTouchMode = false
            isLongClickable = true
            isCursorVisible = false
            isClickable = true
            setTextIsSelectable(true)

            customSelectionActionModeCallback = object : ActionMode.Callback {
                override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    menu?.add(0, 1, 0, wiktionarySearchTitle)
                    menu?.add(0, 2, 1, verbMtSearchTitle)
                    return true  // true to create the action mode
                }

                override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    return false // No need to refresh
                }

                override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                    val start = selectionStart
                    val end = selectionEnd
                    val selected = text.substring(start, end)

                    when (item?.itemId) {
                        1 -> {
                            openUrl(
                                "https://en.wiktionary.org/wiki/${
                                    selected.trim().lowercase()
                                }#Maltese"
                            )
                            mode?.finish()  // Close the selection menu
                            return true
                        }

                        2 -> {
                            openUrl("https://verb.mt/?s=${selected.trim().lowercase()}")
                            mode?.finish()
                            return true
                        }
                    }
                    return false
                }

                override fun onDestroyActionMode(mode: ActionMode?) {}
            }
        }
    })
}

@Composable
fun AudioPlayer(
    isPlaying: Boolean,
    hasCompleted: Boolean,
    currentProgress: Float,
    playbackSpeed: Float,
    length: Int,
    onPlayPauseClick: () -> Unit,
    onSeek: (Float) -> Unit,
    onJumpForward: () -> Unit,
    onJumpBackward: () -> Unit,
    onSpeedChange: (Float) -> Unit
) {
    val speedOptions = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
    var showSpeedMenu by remember { mutableStateOf(false) }

    // Convert milliseconds to seconds for more intuitive display
    val lengthSeconds = length / 1000
    val currentPositionSeconds = (currentProgress * lengthSeconds)
    val roundedCurrentPosition =
        BigDecimal(currentPositionSeconds.toDouble()).setScale(0, RoundingMode.HALF_EVEN)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Progress bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Slider(
                    value = currentProgress,
                    onValueChange = onSeek,
                    modifier = Modifier.weight(1f), // Makes slider fill remaining space
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Display current time / total time in seconds
                Text(
                    text = "${roundedCurrentPosition}s / ${lengthSeconds}s",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Jump backward button
                IconButton(onClick = onJumpBackward) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Jump backward 10 seconds"
                        )
                    }
                }

                // Play/pause button
                IconButton(
                    onClick = onPlayPauseClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = when {
                            hasCompleted -> Icons.Default.Refresh
                            isPlaying -> Icons.Default.Pause
                            else -> Icons.Default.PlayArrow
                        },
                        contentDescription = when {
                            hasCompleted -> "Replay"
                            isPlaying -> "Pause"
                            else -> "Play"
                        },
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Jump forward button
                IconButton(onClick = onJumpForward) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Jump forward 10 seconds"
                        )
                    }
                }

                // Speed button with dropdown menu
                Box {
                    Button(
                        onClick = { showSpeedMenu = true },
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("${playbackSpeed}x")
                    }

                    DropdownMenu(
                        expanded = showSpeedMenu,
                        onDismissRequest = { showSpeedMenu = false }
                    ) {
                        speedOptions.forEach { speed ->
                            DropdownMenuItem(
                                text = { Text("${speed}x") },
                                onClick = {
                                    onSpeedChange(speed)
                                    showSpeedMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}