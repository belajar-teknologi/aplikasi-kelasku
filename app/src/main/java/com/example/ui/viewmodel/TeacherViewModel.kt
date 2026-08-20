package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.TeacherRepository
import com.example.util.EmailHelper
import com.example.util.NotificationHelper
import com.example.util.PdfGenerator
import com.example.util.WhatsAppHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AppNavDestination {
    DASHBOARD,
    ATTENDANCE,
    GRADES,
    QUIZ,
    HOMEWORK,
    SCHEDULE,
    CHAT,
    REPORT
}

data class DashboardAnalytics(
    val totalStudents: Int = 0,
    val averageClassScore: Double = 0.0,
    val attendanceRatePercentage: Double = 0.0,
    val totalHadir: Int = 0,
    val totalSakit: Int = 0,
    val totalIzin: Int = 0,
    val totalAlpa: Int = 0,
    val atRiskStudents: List<Student> = emptyList(),
    val topStudents: List<Pair<Student, Double>> = emptyList(),
    val subjectAverages: Map<String, Double> = emptyMap(),
    val gradeDistribution: Map<String, Int> = emptyMap() // "A (>=85)", "B (75-84)", "C (65-74)", "D (<65)"
)

class TeacherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TeacherRepository
    
    val currentScreen = MutableStateFlow(AppNavDestination.DASHBOARD)
    val selectedClass = MutableStateFlow("Kelas 5A")
    val selectedDate = MutableStateFlow(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    
    // Search queries
    val searchQuery = MutableStateFlow("")

    // Active Quiz Session (for interactive student test mode)
    val activeQuiz = MutableStateFlow<Quiz?>(null)
    val activeQuizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val currentQuestionIndex = MutableStateFlow(0)
    val selectedAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap()) // QuestionIndex -> OptionIndex
    val quizCompleted = MutableStateFlow(false)
    val quizScore = MutableStateFlow(0.0)
    val testTakerStudent = MutableStateFlow<Student?>(null)

    // Chat Selected Student
    val selectedChatStudent = MutableStateFlow<Student?>(null)

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TeacherRepository(db)
        NotificationHelper.createNotificationChannels(application)

        viewModelScope.launch {
            repository.initializeDummyDataIfEmpty()
        }
    }

    // Flows
    val students: StateFlow<List<Student>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttendance: StateFlow<List<AttendanceRecord>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGrades: StateFlow<List<GradeRecord>> = repository.allGrades
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allQuizzes: StateFlow<List<Quiz>> = repository.allQuizzes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHomework: StateFlow<List<Homework>> = repository.allHomework
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allScheduleEvents: StateFlow<List<ScheduleEvent>> = repository.allScheduleEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allChatMessages: StateFlow<List<ChatMessage>> = repository.allChatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPushAlerts: StateFlow<List<PushAlert>> = repository.allPushAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard Analytics State
    val analytics: StateFlow<DashboardAnalytics> = combine(
        students,
        allAttendance,
        allGrades,
        selectedClass
    ) { studentList, attendanceList, gradeList, cls ->
        val filteredStudents = if (cls == "Semua Kelas") studentList else studentList.filter { it.className == cls }
        val studentIds = filteredStudents.map { it.id }.toSet()
        val filteredAttendance = attendanceList.filter { studentIds.contains(it.studentId) }
        val filteredGrades = gradeList.filter { studentIds.contains(it.studentId) }

        val totalStudents = filteredStudents.size
        val avgScore = if (filteredGrades.isNotEmpty()) filteredGrades.map { it.score }.average() else 0.0

        val hadir = filteredAttendance.count { it.status == AttendanceStatus.HADIR }
        val sakit = filteredAttendance.count { it.status == AttendanceStatus.SAKIT }
        val izin = filteredAttendance.count { it.status == AttendanceStatus.IZIN }
        val alpa = filteredAttendance.count { it.status == AttendanceStatus.ALPA }
        val totalAtt = filteredAttendance.size
        val attRate = if (totalAtt > 0) (hadir.toDouble() / totalAtt * 100.0) else 100.0

        // Student Averages & Ranking
        val studentAvgMap = filteredStudents.map { student ->
            val sGrades = filteredGrades.filter { it.studentId == student.id }
            val studentAvg = if (sGrades.isNotEmpty()) sGrades.map { it.score }.average() else 0.0
            student to studentAvg
        }

        val topStudents = studentAvgMap.filter { it.second > 0 }.sortedByDescending { it.second }.take(5)
        val atRisk = studentAvgMap.filter { it.second > 0 && it.second < 75.0 }.map { it.first }

        // Subject Averages
        val subjectMap = filteredGrades.groupBy { it.subject }.mapValues { entry ->
            entry.value.map { it.score }.average()
        }

        // Grade Distribution
        val gradeDist = mutableMapOf(
            "A (>=85)" to 0,
            "B (75-84)" to 0,
            "C (65-74)" to 0,
            "D (<65)" to 0
        )
        filteredGrades.forEach { g ->
            when {
                g.score >= 85 -> gradeDist["A (>=85)"] = (gradeDist["A (>=85)"] ?: 0) + 1
                g.score >= 75 -> gradeDist["B (75-84)"] = (gradeDist["B (75-84)"] ?: 0) + 1
                g.score >= 65 -> gradeDist["C (65-74)"] = (gradeDist["C (65-74)"] ?: 0) + 1
                else -> gradeDist["D (<65)"] = (gradeDist["D (<65)"] ?: 0) + 1
            }
        }

        DashboardAnalytics(
            totalStudents = totalStudents,
            averageClassScore = avgScore,
            attendanceRatePercentage = attRate,
            totalHadir = hadir,
            totalSakit = sakit,
            totalIzin = izin,
            totalAlpa = alpa,
            atRiskStudents = atRisk,
            topStudents = topStudents,
            subjectAverages = subjectMap,
            gradeDistribution = gradeDist
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardAnalytics())

    // Actions: Navigation
    fun navigateTo(screen: AppNavDestination) {
        currentScreen.value = screen
    }

    fun setSelectedClass(cls: String) {
        selectedClass.value = cls
    }

    fun setSelectedDate(date: String) {
        selectedDate.value = date
    }

    // Actions: Attendance
    fun updateAttendanceStatus(studentId: Long, status: AttendanceStatus, notes: String = "") {
        viewModelScope.launch {
            val student = repository.getStudentById(studentId) ?: return@launch
            val record = AttendanceRecord(
                studentId = studentId,
                date = selectedDate.value,
                status = status,
                notes = notes,
                className = student.className
            )
            repository.saveAttendance(record)
        }
    }

    fun batchMarkAllPresent(cls: String, date: String) {
        viewModelScope.launch {
            val targetStudents = students.value.filter { if (cls == "Semua Kelas") true else it.className == cls }
            val records = targetStudents.map { student ->
                AttendanceRecord(
                    studentId = student.id,
                    date = date,
                    status = AttendanceStatus.HADIR,
                    notes = "Hadir",
                    className = student.className
                )
            }
            repository.saveAttendanceList(records)
        }
    }

    // Actions: Grade Management
    fun addGrade(
        studentId: Long,
        subject: String,
        type: GradeType,
        score: Double,
        maxScore: Double = 100.0,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val record = GradeRecord(
                studentId = studentId,
                subject = subject,
                type = type,
                score = score,
                maxScore = maxScore,
                date = selectedDate.value,
                notes = notes
            )
            repository.insertGrade(record)
        }
    }

    fun deleteGrade(grade: GradeRecord) {
        viewModelScope.launch {
            repository.deleteGrade(grade)
        }
    }

    // Actions: Student Management
    fun addStudent(student: Student) {
        viewModelScope.launch {
            repository.insertStudent(student)
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            repository.updateStudent(student)
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            repository.deleteStudent(student)
        }
    }

    // Actions: Quizzes & Interactive Quiz Runner
    fun createQuiz(
        title: String,
        subject: String,
        className: String,
        durationMinutes: Int,
        shareCode: String,
        materialSummary: String,
        imageUrl: String?,
        questions: List<QuizQuestion>
    ) {
        viewModelScope.launch {
            val quiz = Quiz(
                title = title,
                subject = subject,
                className = className,
                durationMinutes = durationMinutes,
                shareCode = shareCode,
                materialSummary = materialSummary,
                imageUrl = imageUrl
            )
            val quizId = repository.insertQuiz(quiz)
            val questionsWithId = questions.map { it.copy(quizId = quizId) }
            repository.insertQuestions(questionsWithId)
        }
    }

    fun deleteQuiz(quiz: Quiz) {
        viewModelScope.launch {
            repository.deleteQuiz(quiz)
        }
    }

    fun startQuizSession(quiz: Quiz, student: Student?) {
        viewModelScope.launch {
            activeQuiz.value = quiz
            testTakerStudent.value = student
            val qList = repository.getQuestionsListForQuiz(quiz.id)
            activeQuizQuestions.value = qList
            currentQuestionIndex.value = 0
            selectedAnswers.value = emptyMap()
            quizCompleted.value = false
            quizScore.value = 0.0
        }
    }

    fun selectAnswer(questionIndex: Int, optionIndex: Int) {
        val current = selectedAnswers.value.toMutableMap()
        current[questionIndex] = optionIndex
        selectedAnswers.value = current
    }

    fun submitQuiz() {
        viewModelScope.launch {
            val questions = activeQuizQuestions.value
            val answers = selectedAnswers.value
            var correct = 0

            questions.forEachIndexed { index, q ->
                if (answers[index] == q.correctOptionIndex) {
                    correct++
                }
            }

            val total = questions.size
            val calculatedScore = if (total > 0) (correct.toDouble() / total * 100.0) else 0.0

            quizScore.value = calculatedScore
            quizCompleted.value = true

            val quiz = activeQuiz.value
            val student = testTakerStudent.value
            if (quiz != null && student != null) {
                // Save result
                repository.insertQuizResult(
                    QuizResult(
                        quizId = quiz.id,
                        studentId = student.id,
                        studentName = student.name,
                        score = calculatedScore,
                        totalQuestions = total,
                        correctAnswers = correct
                    )
                )

                // Also record in grades
                repository.insertGrade(
                    GradeRecord(
                        studentId = student.id,
                        subject = quiz.subject,
                        type = GradeType.KUIS,
                        score = calculatedScore,
                        maxScore = 100.0,
                        date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                        notes = "Hasil Kuis Online: ${quiz.title}"
                    )
                )
            }
        }
    }

    fun closeQuizSession() {
        activeQuiz.value = null
        activeQuizQuestions.value = emptyList()
        quizCompleted.value = false
        selectedAnswers.value = emptyMap()
    }

    // Actions: Homework
    fun addHomework(
        title: String,
        subject: String,
        className: String,
        description: String,
        dueDate: String,
        dueTime: String,
        syncToCalendar: Boolean
    ) {
        viewModelScope.launch {
            val hw = Homework(
                title = title,
                subject = subject,
                className = className,
                description = description,
                dueDate = dueDate,
                dueTime = dueTime,
                isCompleted = false,
                syncedToCalendar = syncToCalendar
            )
            repository.insertHomework(hw)

            // Trigger notification reminder
            NotificationHelper.showPushNotification(
                getApplication(),
                "Tugas Baru Dibuat: $title",
                "Tenggat waktu: $dueDate pukul $dueTime ($className)",
                "TUGAS"
            )
        }
    }

    fun toggleHomeworkCompleted(homework: Homework) {
        viewModelScope.launch {
            repository.updateHomework(homework.copy(isCompleted = !homework.isCompleted))
        }
    }

    fun deleteHomework(homework: Homework) {
        viewModelScope.launch {
            repository.deleteHomework(homework)
        }
    }

    // Actions: Schedule & Calendar
    fun addScheduleEvent(
        title: String,
        eventType: EventType,
        subject: String,
        className: String,
        date: String,
        startTime: String,
        endTime: String,
        location: String
    ) {
        viewModelScope.launch {
            val event = ScheduleEvent(
                title = title,
                eventType = eventType,
                subject = subject,
                className = className,
                date = date,
                startTime = startTime,
                endTime = endTime,
                location = location,
                reminderEnabled = true
            )
            repository.insertScheduleEvent(event)

            if (eventType == EventType.UJIAN) {
                NotificationHelper.showPushNotification(
                    getApplication(),
                    "Jadwal Ujian Baru: $title",
                    "Tanggal $date pada $startTime - $endTime WIB di $location",
                    "UJIAN"
                )
            }
        }
    }

    fun deleteScheduleEvent(event: ScheduleEvent) {
        viewModelScope.launch {
            repository.deleteScheduleEvent(event)
        }
    }

    // Actions: Chat
    fun sendChatMessage(studentId: Long, messageText: String, isFromTeacher: Boolean = true, tag: String = "KONSULTASI") {
        viewModelScope.launch {
            val student = repository.getStudentById(studentId) ?: return@launch
            val chat = ChatMessage(
                studentId = studentId,
                parentName = student.parentName,
                studentName = student.name,
                message = messageText,
                isFromTeacher = isFromTeacher,
                tag = tag
            )
            repository.sendChatMessage(chat)
        }
    }

    fun setSelectedChatStudent(student: Student?) {
        selectedChatStudent.value = student
    }

    // Actions: WhatsApp, Email & PDF Helpers
    fun sendWhatsAppAttendance(context: Context, student: Student, date: String) {
        val record = allAttendance.value.find { it.studentId == student.id && it.date == date }
            ?: AttendanceRecord(studentId = student.id, date = date, status = AttendanceStatus.HADIR, className = student.className)
        WhatsAppHelper.sendAttendanceNotification(context, student, record)
    }

    fun sendWhatsAppGrades(context: Context, student: Student) {
        val studentGrades = allGrades.value.filter { it.studentId == student.id }
        WhatsAppHelper.sendGradeReport(context, student, studentGrades)
    }

    fun sendEmailGrades(context: Context, student: Student) {
        val studentGrades = allGrades.value.filter { it.studentId == student.id }
        EmailHelper.sendGradeReportEmail(context, student, studentGrades)
    }

    fun sendWhatsAppHomeworkReminder(context: Context, student: Student, hw: Homework) {
        WhatsAppHelper.sendHomeworkReminder(
            context,
            student,
            hw.title,
            hw.subject,
            hw.dueDate,
            hw.dueTime,
            hw.description
        )
    }

    fun sendWhatsAppQuizLink(context: Context, student: Student, quiz: Quiz) {
        WhatsAppHelper.sendQuizLink(context, student, quiz.title, quiz.subject, quiz.shareCode)
    }

    fun exportWeeklyReportPdf(context: Context, className: String) {
        val currentStudents = students.value.filter { if (className == "Semua Kelas") true else it.className == className }
        val currentAttendance = allAttendance.value
        val currentGrades = allGrades.value
        PdfGenerator.generateAndShareReportPdf(
            context = context,
            className = className,
            students = currentStudents,
            attendanceList = currentAttendance,
            gradesList = currentGrades
        )
    }
}
