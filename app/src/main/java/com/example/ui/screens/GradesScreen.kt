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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GradeRecord
import com.example.data.model.GradeType
import com.example.data.model.Student
import com.example.ui.components.AppTopBar
import com.example.ui.components.ClassFilterChipRow
import com.example.ui.theme.*
import com.example.ui.viewmodel.TeacherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesScreen(
    viewModel: TeacherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val students by viewModel.students.collectAsStateWithLifecycle()
    val allGrades by viewModel.allGrades.collectAsStateWithLifecycle()
    val selectedClass by viewModel.selectedClass.collectAsStateWithLifecycle()

    var selectedSubjectTab by remember { mutableStateOf("Semua Mapel") }
    val subjects = listOf("Semua Mapel", "Matematika", "IPA", "Bahasa Indonesia", "IPS", "PPKn", "Bahasa Inggris")

    val filteredStudents = remember(students, selectedClass) {
        if (selectedClass == "Semua Kelas") students else students.filter { it.className == selectedClass }
    }

    var showAddGradeDialog by remember { mutableStateOf(false) }
    var selectedStudentForDetail by remember { mutableStateOf<Student?>(null) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Manajemen Nilai Siswa",
                subtitle = "Buku Nilai & Hasil Belajar • $selectedClass",
                actions = {
                    IconButton(
                        onClick = { showAddGradeDialog = true },
                        modifier = Modifier.testTag("grades_add_button")
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Tambah Nilai", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddGradeDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("grades_fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Input Nilai Baru")
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
            // Class Filter
            item {
                ClassFilterChipRow(
                    selectedClass = selectedClass,
                    onClassSelected = { viewModel.setSelectedClass(it) }
                )
            }

            // Subject Filter Tabs
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(subjects) { subject ->
                        val isSelected = selectedSubjectTab == subject
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSubjectTab = subject },
                            label = { Text(subject) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }
            }

            // Student Gradebook List
            items(filteredStudents, key = { it.id }) { student ->
                val studentGrades = remember(allGrades, student.id, selectedSubjectTab) {
                    val sGrades = allGrades.filter { it.studentId == student.id }
                    if (selectedSubjectTab == "Semua Mapel") sGrades else sGrades.filter { it.subject == selectedSubjectTab }
                }

                val averageScore = if (studentGrades.isNotEmpty()) studentGrades.map { it.score }.average() else 0.0
                val isTuntas = averageScore >= 75.0

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedStudentForDetail = student },
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
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(if (isTuntas) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = student.name.take(1),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isTuntas) Color(0xFF15803D) else Color(0xFFB91C1C)
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
                                        text = "NIS: ${student.nis} • ${student.className}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }
                            }

                            // Average Score Badge
                            Column(horizontalAlignment = Alignment.End) {
                                Surface(
                                    color = if (isTuntas) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (averageScore > 0) String.format("%.1f", averageScore) else "-",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = if (isTuntas) Color(0xFF15803D) else Color(0xFFB91C1C)
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Text(
                                    text = if (averageScore >= 75) "Tuntas KKM" else if (averageScore > 0) "Remedial" else "Belum Dinilai",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isTuntas) Color(0xFF16A34A) else Color(0xFFDC2626),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Recent scores chips
                        if (studentGrades.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                studentGrades.take(3).forEach { grade ->
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "${grade.subject} (${grade.type}): ${grade.score.toInt()}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                "Belum ada rekapan nilai pada filter ini",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Communication Actions: WhatsApp & Email Laporan
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Kirim ke Orang Tua: ",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            
                            // Send to WA
                            FilledTonalIconButton(
                                onClick = { viewModel.sendWhatsAppGrades(context, student) },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = Color(0xFFDCFCE7),
                                    contentColor = Color(0xFF15803D)
                                ),
                                modifier = Modifier.size(32.dp).testTag("grade_wa_btn_${student.id}")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Kirim WhatsApp", modifier = Modifier.size(16.dp))
                            }
                            
                            Spacer(modifier = Modifier.width(6.dp))

                            // Send to Email
                            FilledTonalIconButton(
                                onClick = { viewModel.sendEmailGrades(context, student) },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = Color(0xFFDBEAFE),
                                    contentColor = Color(0xFF1E40AF)
                                ),
                                modifier = Modifier.size(32.dp).testTag("grade_email_btn_${student.id}")
                            ) {
                                Icon(Icons.Default.Email, contentDescription = "Kirim Email", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Grade Dialog
    if (showAddGradeDialog) {
        AddGradeDialog(
            students = filteredStudents,
            subjects = subjects.filter { it != "Semua Mapel" },
            onDismiss = { showAddGradeDialog = false },
            onSave = { studentId, subject, type, score, notes ->
                viewModel.addGrade(studentId, subject, type, score, 100.0, notes)
                showAddGradeDialog = false
                Toast.makeText(context, "Nilai berhasil disimpan!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Student Detail Gradebook Dialog
    if (selectedStudentForDetail != null) {
        val student = selectedStudentForDetail!!
        val studentAllGrades = allGrades.filter { it.studentId == student.id }
        
        AlertDialog(
            onDismissRequest = { selectedStudentForDetail = null },
            title = {
                Column {
                    Text("Buku Nilai: ${student.name}")
                    Text("NIS: ${student.nis} • ${student.className}", style = MaterialTheme.typography.bodySmall)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 360.dp)) {
                    if (studentAllGrades.isEmpty()) {
                        item {
                            Text("Belum ada riwayat nilai untuk siswa ini.", style = MaterialTheme.typography.bodySmall)
                        }
                    } else {
                        items(studentAllGrades) { grade ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "${grade.subject} - ${grade.type}",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            "${grade.date} • ${grade.notes.ifEmpty { "Tugas Harian" }}",
                                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                    }
                                    Text(
                                        text = "${grade.score.toInt()} / ${grade.maxScore.toInt()}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = if (grade.score >= 75) Color(0xFF15803D) else Color(0xFFB91C1C)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { selectedStudentForDetail = null }) {
                    Text("Tutup")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    viewModel.sendWhatsAppGrades(context, student)
                }) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Kirim WA")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGradeDialog(
    students: List<Student>,
    subjects: List<String>,
    onDismiss: () -> Unit,
    onSave: (studentId: Long, subject: String, type: GradeType, score: Double, notes: String) -> Unit
) {
    var selectedStudentId by remember { mutableStateOf(students.firstOrNull()?.id ?: 0L) }
    var selectedSubject by remember { mutableStateOf(subjects.firstOrNull() ?: "Matematika") }
    var selectedType by remember { mutableStateOf(GradeType.TUGAS) }
    var scoreInput by remember { mutableStateOf("85") }
    var notesInput by remember { mutableStateOf("") }

    var studentDropdownExpanded by remember { mutableStateOf(false) }
    var subjectDropdownExpanded by remember { mutableStateOf(false) }

    val selectedStudent = students.find { it.id == selectedStudentId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Input Nilai Siswa Baru") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Select Student
                ExposedDropdownMenuBox(
                    expanded = studentDropdownExpanded,
                    onExpandedChange = { studentDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedStudent?.name ?: "Pilih Siswa",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Nama Siswa") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = studentDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = studentDropdownExpanded,
                        onDismissRequest = { studentDropdownExpanded = false }
                    ) {
                        students.forEach { s ->
                            DropdownMenuItem(
                                text = { Text("${s.name} (${s.className})") },
                                onClick = {
                                    selectedStudentId = s.id
                                    studentDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Select Subject
                ExposedDropdownMenuBox(
                    expanded = subjectDropdownExpanded,
                    onExpandedChange = { subjectDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSubject,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mata Pelajaran") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = subjectDropdownExpanded,
                        onDismissRequest = { subjectDropdownExpanded = false }
                    ) {
                        subjects.forEach { sub ->
                            DropdownMenuItem(
                                text = { Text(sub) },
                                onClick = {
                                    selectedSubject = sub
                                    subjectDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Grade Type Chips
                Text("Jenis Penilaian:", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    GradeType.values().forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.name, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // Score Input
                OutlinedTextField(
                    value = scoreInput,
                    onValueChange = { scoreInput = it },
                    label = { Text("Nilai Siswa (Skala 0 - 100)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Notes
                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Catatan / Topik Materi (Opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val score = scoreInput.toDoubleOrNull() ?: 0.0
                    onSave(selectedStudentId, selectedSubject, selectedType, score, notesInput)
                }
            ) {
                Text("Simpan Nilai")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
