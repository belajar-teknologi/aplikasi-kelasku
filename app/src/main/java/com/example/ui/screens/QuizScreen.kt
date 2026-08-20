package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.model.Quiz
import com.example.data.model.QuizQuestion
import com.example.data.model.Student
import com.example.ui.components.AppTopBar
import com.example.ui.components.ClassFilterChipRow
import com.example.ui.theme.*
import com.example.ui.viewmodel.TeacherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: TeacherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val quizzes by viewModel.allQuizzes.collectAsStateWithLifecycle()
    val students by viewModel.students.collectAsStateWithLifecycle()
    val selectedClass by viewModel.selectedClass.collectAsStateWithLifecycle()

    // Quiz Session States
    val activeQuiz by viewModel.activeQuiz.collectAsStateWithLifecycle()
    val activeQuizQuestions by viewModel.activeQuizQuestions.collectAsStateWithLifecycle()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val selectedAnswers by viewModel.selectedAnswers.collectAsStateWithLifecycle()
    val quizCompleted by viewModel.quizCompleted.collectAsStateWithLifecycle()
    val quizScore by viewModel.quizScore.collectAsStateWithLifecycle()
    val testTakerStudent by viewModel.testTakerStudent.collectAsStateWithLifecycle()

    var showCreateQuizDialog by remember { mutableStateOf(false) }
    var selectedQuizForStudentDialog by remember { mutableStateOf<Quiz?>(null) }

    // If student is currently taking a quiz, render the interactive Quiz Player View
    if (activeQuiz != null) {
        QuizPlayerView(
            quiz = activeQuiz!!,
            questions = activeQuizQuestions,
            currentIndex = currentQuestionIndex,
            selectedAnswers = selectedAnswers,
            isCompleted = quizCompleted,
            score = quizScore,
            student = testTakerStudent,
            onSelectAnswer = { qIdx, optIdx -> viewModel.selectAnswer(qIdx, optIdx) },
            onNextQuestion = {
                if (currentQuestionIndex < activeQuizQuestions.size - 1) {
                    viewModel.currentQuestionIndex.value = currentQuestionIndex + 1
                }
            },
            onPrevQuestion = {
                if (currentQuestionIndex > 0) {
                    viewModel.currentQuestionIndex.value = currentQuestionIndex - 1
                }
            },
            onSubmitQuiz = { viewModel.submitQuiz() },
            onCloseQuiz = { viewModel.closeQuizSession() }
        )
        return
    }

    val filteredQuizzes = remember(quizzes, selectedClass) {
        if (selectedClass == "Semua Kelas") quizzes else quizzes.filter { it.className == selectedClass }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Kuis & Evaluasi Daring",
                subtitle = "Uji Pemahaman Materi Pelajaran",
                actions = {
                    IconButton(
                        onClick = { showCreateQuizDialog = true },
                        modifier = Modifier.testTag("quiz_create_button")
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Buat Kuis Baru", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateQuizDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("quiz_fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Buat Kuis")
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

            // Info Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Kuis Interaktif Daring Mandiri",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                            Text(
                                text = "Bagikan link/kode ke siswa via WhatsApp. Siswa mengerjakan & nilai otomatis tercatat ke buku nilai!",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                            )
                        }
                    }
                }
            }

            // Quizzes List
            items(filteredQuizzes, key = { it.id }) { quiz ->
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
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${quiz.subject} • ${quiz.className}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = quiz.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Durasi: ${quiz.durationMinutes} Menit • Disertai Pembahasan Materi",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }

                            // Share Code Pill
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Key, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = quiz.shareCode,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFB45309)
                                        )
                                    )
                                }
                            }
                        }

                        if (quiz.materialSummary.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "📖 Ringkasan Materi: ${quiz.materialSummary}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Actions Row: Salin Link, Bagikan WA, Kerjakan Simulasi
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Copy Link
                            TextButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Link Kuis", "https://gurupintar.sch.id/quiz?code=${quiz.shareCode}")
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Link kuis disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Salin Link", style = MaterialTheme.typography.labelMedium)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                // Bagikan WhatsApp
                                FilledTonalButton(
                                    onClick = {
                                        val firstStudent = students.find { it.className == quiz.className } ?: students.firstOrNull()
                                        if (firstStudent != null) {
                                            viewModel.sendWhatsAppQuizLink(context, firstStudent, quiz)
                                        }
                                    },
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = Color(0xFFDCFCE7),
                                        contentColor = Color(0xFF15803D)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Bagikan WA", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }

                                // Interactive Student Test Simulation
                                Button(
                                    onClick = {
                                        selectedQuizForStudentDialog = quiz
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("quiz_take_simulation_${quiz.id}")
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Kerjakan Kuis", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog for picking which student is taking the quiz simulation
    if (selectedQuizForStudentDialog != null) {
        val currentQuiz = selectedQuizForStudentDialog!!
        var chosenStudentId by remember { mutableStateOf(students.firstOrNull()?.id ?: 0L) }

        AlertDialog(
            onDismissRequest = { selectedQuizForStudentDialog = null },
            title = { Text("Mulai Mengerjakan Kuis") },
            text = {
                Column {
                    Text(
                        "Kuis: ${currentQuiz.title}\nPilih profil siswa untuk mencatat hasil penilaian secara langsung:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    students.filter { if (currentQuiz.className.isNotEmpty()) it.className == currentQuiz.className else true }.forEach { student ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { chosenStudentId = student.id }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = chosenStudentId == student.id,
                                onClick = { chosenStudentId = student.id }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${student.name} (${student.nis})", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val s = students.find { it.id == chosenStudentId }
                        viewModel.startQuizSession(currentQuiz, s)
                        selectedQuizForStudentDialog = null
                    }
                ) {
                    Text("Mulai Ujian")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedQuizForStudentDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }

    // Create Quiz Dialog
    if (showCreateQuizDialog) {
        CreateQuizDialog(
            selectedClass = selectedClass,
            onDismiss = { showCreateQuizDialog = false },
            onSave = { title, subject, cls, duration, code, summary, questions ->
                viewModel.createQuiz(title, subject, cls, duration, code, summary, null, questions)
                showCreateQuizDialog = false
                Toast.makeText(context, "Kuis berhasil dibuat & kode akses aktif!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun QuizPlayerView(
    quiz: Quiz,
    questions: List<QuizQuestion>,
    currentIndex: Int,
    selectedAnswers: Map<Int, Int>,
    isCompleted: Boolean,
    score: Double,
    student: Student?,
    onSelectAnswer: (questionIndex: Int, optionIndex: Int) -> Unit,
    onNextQuestion: () -> Unit,
    onPrevQuestion: () -> Unit,
    onSubmitQuiz: () -> Unit,
    onCloseQuiz: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = quiz.title,
                subtitle = "Siswa: ${student?.name ?: "Siswa"} • ${quiz.subject}",
                navigationIcon = Icons.Default.Close,
                onNavigationClick = onCloseQuiz
            )
        }
    ) { innerPadding ->
        if (questions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Kuis ini belum memiliki daftar pertanyaan.")
            }
            return@Scaffold
        }

        if (isCompleted) {
            // Quiz Results & Score Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(if (score >= 75) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (score >= 75) Icons.Default.EmojiEvents else Icons.Default.Star,
                        contentDescription = null,
                        tint = if (score >= 75) Color(0xFF15803D) else Color(0xFFB45309),
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (score >= 75) "Selamat! Pemahaman Sangat Baik!" else "Hasil Kuis Disimpan!",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "Nilai Akhir: ${String.format("%.1f", score)} / 100",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Nilai telah otomatis tersinkronisasi ke Buku Nilai siswa (${student?.name}) untuk mata pelajaran ${quiz.subject}.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.padding(horizontal = 20.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onCloseQuiz,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kembali ke Daftar Kuis")
                }
            }
            return@Scaffold
        }

        val currentQ = questions[currentIndex]
        val currentSelected = selectedAnswers[currentIndex]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Question Progress Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pertanyaan ${currentIndex + 1} dari ${questions.size}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "⏱️ ${quiz.durationMinutes} Menit",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { ((currentIndex + 1).toFloat() / questions.size) },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Question Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = currentQ.questionText,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val options = listOf(currentQ.optionA, currentQ.optionB, currentQ.optionC, currentQ.optionD)
                    val optionLabels = listOf("A", "B", "C", "D")

                    options.forEachIndexed { optIndex, optText ->
                        val isSelected = currentSelected == optIndex
                        Surface(
                            onClick = { onSelectAnswer(currentIndex, optIndex) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = optionLabels[optIndex],
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else Color.Black
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = optText,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onPrevQuestion,
                    enabled = currentIndex > 0
                ) {
                    Text("Sebelumnya")
                }

                if (currentIndex == questions.size - 1) {
                    Button(
                        onClick = onSubmitQuiz,
                        colors = ButtonDefaults.buttonColors(containerColor = PresentGreen)
                    ) {
                        Text("Selesai & Kumpulkan")
                    }
                } else {
                    Button(onClick = onNextQuestion) {
                        Text("Selanjutnya")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuizDialog(
    selectedClass: String,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        subject: String,
        className: String,
        durationMinutes: Int,
        shareCode: String,
        materialSummary: String,
        questions: List<QuizQuestion>
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Matematika") }
    var materialSummary by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableStateOf("15") }
    var questionText by remember { mutableStateOf("") }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var optionC by remember { mutableStateOf("") }
    var optionD by remember { mutableStateOf("") }
    var correctIndex by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Buat Kuis Daring Baru") },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Judul Kuis") },
                        placeholder = { Text("Kuis Bab Pecahan Desimal") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Mata Pelajaran") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = durationMinutes,
                        onValueChange = { durationMinutes = it },
                        label = { Text("Durasi (Menit)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = materialSummary,
                        onValueChange = { materialSummary = it },
                        label = { Text("Ringkasan Materi Pengantar") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("Pertanyaan 1:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    OutlinedTextField(
                        value = questionText,
                        onValueChange = { questionText = it },
                        label = { Text("Teks Soal") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(value = optionA, onValueChange = { optionA = it }, label = { Text("Pilihan A") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = optionB, onValueChange = { optionB = it }, label = { Text("Pilihan B") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = optionC, onValueChange = { optionC = it }, label = { Text("Pilihan C") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = optionD, onValueChange = { optionD = it }, label = { Text("Pilihan D") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Text("Kunci Jawaban Benar:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("A", "B", "C", "D").forEachIndexed { idx, label ->
                            FilterChip(
                                selected = correctIndex == idx,
                                onClick = { correctIndex = idx },
                                label = { Text("Opsi $label") }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty() && questionText.isNotEmpty()) {
                        val generatedCode = "KUIS-${subject.take(3).uppercase()}-${(100..999).random()}"
                        val q = QuizQuestion(
                            quizId = 0,
                            questionText = questionText,
                            optionA = optionA.ifEmpty { "Pilihan A" },
                            optionB = optionB.ifEmpty { "Pilihan B" },
                            optionC = optionC.ifEmpty { "Pilihan C" },
                            optionD = optionD.ifEmpty { "Pilihan D" },
                            correctOptionIndex = correctIndex,
                            explanation = "Pembahasan materi terkait soal ini."
                        )
                        onSave(
                            title,
                            subject,
                            selectedClass,
                            durationMinutes.toIntOrNull() ?: 15,
                            generatedCode,
                            materialSummary,
                            listOf(q)
                        )
                    }
                }
            ) {
                Text("Simpan & Aktifkan Kuis")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
