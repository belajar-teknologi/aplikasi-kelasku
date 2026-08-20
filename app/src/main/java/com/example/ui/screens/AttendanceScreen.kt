package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.Student
import com.example.ui.components.AppTopBar
import com.example.ui.components.ClassFilterChipRow
import com.example.ui.components.WhatsAppActionButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.TeacherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    viewModel: TeacherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val students by viewModel.students.collectAsStateWithLifecycle()
    val allAttendance by viewModel.allAttendance.collectAsStateWithLifecycle()
    val selectedClass by viewModel.selectedClass.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

    val filteredStudents = remember(students, selectedClass) {
        if (selectedClass == "Semua Kelas") students else students.filter { it.className == selectedClass }
    }

    val todayAttendanceMap = remember(allAttendance, selectedDate) {
        allAttendance.filter { it.date == selectedDate }.associateBy { it.studentId }
    }

    var showNotesDialogForStudent by remember { mutableStateOf<Student?>(null) }
    var noteText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Presensi Digital Harian",
                subtitle = "Tanggal: $selectedDate",
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.batchMarkAllPresent(selectedClass, selectedDate)
                            Toast.makeText(context, "Semua siswa ditandai HADIR ✅", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("attendance_batch_all_present")
                    ) {
                        Icon(Icons.Default.DoneAll, contentDescription = "Tandai Semua Hadir", tint = Color(0xFF10B981))
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Class Filter
            item {
                ClassFilterChipRow(
                    selectedClass = selectedClass,
                    onClassSelected = { viewModel.setSelectedClass(it) }
                )
            }

            // Quick Info & Batch Action Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Presensi Kelas: $selectedClass",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${filteredStudents.size} Total Siswa Terdaftar",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.batchMarkAllPresent(selectedClass, selectedDate)
                                Toast.makeText(context, "Semua siswa ditandai HADIR", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Semua Hadir", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            // Student Attendance List
            items(filteredStudents, key = { it.id }) { student ->
                val currentRecord = todayAttendanceMap[student.id]
                val currentStatus = currentRecord?.status ?: AttendanceStatus.HADIR

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (student.gender == "L") Color(0xFFDBEAFE) else Color(0xFFFCE7F3)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = student.name.take(1),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (student.gender == "L") Color(0xFF1E40AF) else Color(0xFF9D174D)
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = student.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "NIS: ${student.nis} • Wali: ${student.parentName}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }
                            }

                            // Direct WhatsApp button to inform parent
                            IconButton(
                                onClick = {
                                    viewModel.sendWhatsAppAttendance(context, student, selectedDate)
                                },
                                modifier = Modifier.testTag("student_attendance_whatsapp_${student.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Kirim WhatsApp",
                                    tint = WhatsAppGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 4 Status Toggle Buttons: HADIR, SAKIT, IZIN, ALPA
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AttendanceStatusButton(
                                label = "Hadir",
                                isSelected = currentStatus == AttendanceStatus.HADIR,
                                activeColor = PresentGreen,
                                onClick = {
                                    viewModel.updateAttendanceStatus(student.id, AttendanceStatus.HADIR, "Hadir")
                                },
                                modifier = Modifier.weight(1f)
                            )
                            AttendanceStatusButton(
                                label = "Sakit",
                                isSelected = currentStatus == AttendanceStatus.SAKIT,
                                activeColor = SickYellow,
                                onClick = {
                                    showNotesDialogForStudent = student
                                    noteText = currentRecord?.notes ?: "Sakit demam/flu"
                                },
                                modifier = Modifier.weight(1f)
                            )
                            AttendanceStatusButton(
                                label = "Izin",
                                isSelected = currentStatus == AttendanceStatus.IZIN,
                                activeColor = PermitBlue,
                                onClick = {
                                    showNotesDialogForStudent = student
                                    noteText = currentRecord?.notes ?: "Keperluan keluarga"
                                },
                                modifier = Modifier.weight(1f)
                            )
                            AttendanceStatusButton(
                                label = "Alpa",
                                isSelected = currentStatus == AttendanceStatus.ALPA,
                                activeColor = AbsentRed,
                                onClick = {
                                    viewModel.updateAttendanceStatus(student.id, AttendanceStatus.ALPA, "Tanpa Keterangan")
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (!currentRecord?.notes.isNullOrEmpty() && currentStatus != AttendanceStatus.HADIR) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Catatan: ${currentRecord?.notes}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog for Entering Notes when Sakit/Izin
    if (showNotesDialogForStudent != null) {
        val targetStudent = showNotesDialogForStudent!!
        AlertDialog(
            onDismissRequest = { showNotesDialogForStudent = null },
            title = { Text("Keterangan Absensi: ${targetStudent.name}") },
            text = {
                Column {
                    Text("Pilih alasan dan tulis catatan untuk orang tua:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text("Keterangan/Alasan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val status = if (noteText.contains("sakit", ignoreCase = true)) AttendanceStatus.SAKIT else AttendanceStatus.IZIN
                        viewModel.updateAttendanceStatus(targetStudent.id, status, noteText)
                        showNotesDialogForStudent = null
                        Toast.makeText(context, "Status absensi diperbarui", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Simpan Status")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotesDialogForStudent = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun AttendanceStatusButton(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) activeColor else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .height(36.dp)
            .testTag("attendance_btn_$label")
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            )
        }
    }
}
