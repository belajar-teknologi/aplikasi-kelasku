package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.EventType
import com.example.data.model.ScheduleEvent
import com.example.ui.components.AppTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.TeacherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: TeacherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val events by viewModel.allScheduleEvents.collectAsStateWithLifecycle()
    val selectedClass by viewModel.selectedClass.collectAsStateWithLifecycle()

    var selectedTypeFilter by remember { mutableStateOf("Semua Agenda") }
    val eventFilterOptions = listOf("Semua Agenda", "UJIAN", "JADWAL_BELAJAR", "TENGGAT_TUGAS", "RAPAT_WALI")

    var showAddEventDialog by remember { mutableStateOf(false) }

    val filteredEvents = remember(events, selectedTypeFilter) {
        if (selectedTypeFilter == "Semua Agenda") events else events.filter { it.eventType.name == selectedTypeFilter }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Kalender Belajar & Ujian",
                subtitle = "Jadwal Terpadu, PTS, PAS & Tugas",
                actions = {
                    IconButton(
                        onClick = { showAddEventDialog = true },
                        modifier = Modifier.testTag("schedule_add_button")
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Tambah Jadwal", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddEventDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("schedule_fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Acara")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Calendar Event Types Tabs
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(eventFilterOptions) { filter ->
                        val isSelected = selectedTypeFilter == filter
                        val displayLabel = when (filter) {
                            "Semua Agenda" -> "Semua"
                            "UJIAN" -> "Ujian & PTS"
                            "JADWAL_BELAJAR" -> "Jadwal Pelajaran"
                            "TENGGAT_TUGAS" -> "Tenggat PR"
                            "RAPAT_WALI" -> "Pertemuan Ortu"
                            else -> filter
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTypeFilter = filter },
                            label = { Text(displayLabel) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Timeline / Event Cards
            if (filteredEvents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Tidak ada jadwal kegiatan untuk filter ini.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            } else {
                items(filteredEvents, key = { it.id }) { event ->
                    val (badgeBg, badgeText, icon) = when (event.eventType) {
                        EventType.UJIAN -> Triple(Color(0xFFFEE2E2), Color(0xFFB91C1C), Icons.Default.Warning)
                        EventType.JADWAL_BELAJAR -> Triple(Color(0xFFDBEAFE), Color(0xFF1E40AF), Icons.Default.MenuBook)
                        EventType.TENGGAT_TUGAS -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), Icons.Default.Assignment)
                        EventType.RAPAT_WALI -> Triple(Color(0xFFFCE7F3), Color(0xFF9D174D), Icons.Default.Groups)
                        EventType.KEGIATAN_SEKOLAH -> Triple(Color(0xFFDCFCE7), Color(0xFF15803D), Icons.Default.School)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Date Column Box
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(badgeBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(imageVector = icon, contentDescription = null, tint = badgeText, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = event.date.takeLast(2),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = badgeText
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = badgeBg,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = event.eventType.name.replace("_", " "),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = badgeText
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = event.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "⏰ ${event.startTime} - ${event.endTime} WIB • ${event.location}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }

                            IconButton(
                                onClick = {
                                    viewModel.deleteScheduleEvent(event)
                                    Toast.makeText(context, "Agenda dihapus", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Schedule Dialog
    if (showAddEventDialog) {
        AddEventDialog(
            selectedClass = selectedClass,
            onDismiss = { showAddEventDialog = false },
            onSave = { title, type, subject, cls, date, start, end, loc ->
                viewModel.addScheduleEvent(title, type, subject, cls, date, start, end, loc)
                showAddEventDialog = false
                Toast.makeText(context, "Jadwal baru berhasil disimpan!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(
    selectedClass: String,
    onDismiss: () -> Unit,
    onSave: (title: String, type: EventType, subject: String, className: String, date: String, start: String, end: String, location: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(EventType.UJIAN) }
    var subject by remember { mutableStateOf("Matematika") }
    var date by remember { mutableStateOf("2026-08-28") }
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("09:30") }
    var location by remember { mutableStateOf("Ruang Kelas 5A") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Agenda / Jadwal Ujian") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Agenda / Ujian") },
                    placeholder = { Text("Penilaian Tengah Semester") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Kategori Agenda:", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(EventType.UJIAN, EventType.JADWAL_BELAJAR, EventType.RAPAT_WALI).forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.name.replace("_", " "), style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Tanggal (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Mulai (HH:mm)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("Selesai (HH:mm)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lokasi / Ruangan") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty()) {
                        onSave(title, selectedType, subject, selectedClass, date, startTime, endTime, location)
                    }
                }
            ) {
                Text("Simpan Jadwal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
