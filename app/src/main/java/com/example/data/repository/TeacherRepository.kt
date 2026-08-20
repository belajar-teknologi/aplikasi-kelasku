package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.util.DummyDataGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TeacherRepository(private val database: AppDatabase) {

    private val studentDao = database.studentDao()
    private val attendanceDao = database.attendanceDao()
    private val gradeDao = database.gradeDao()
    private val quizDao = database.quizDao()
    private val homeworkDao = database.homeworkDao()
    private val scheduleDao = database.scheduleDao()
    private val chatDao = database.chatDao()
    private val pushAlertDao = database.pushAlertDao()

    // Students
    val allStudents: Flow<List<Student>> = studentDao.getAllStudents()
    fun getStudentsByClass(className: String): Flow<List<Student>> = studentDao.getStudentsByClass(className)
    suspend fun getStudentById(id: Long): Student? = studentDao.getStudentById(id)
    suspend fun insertStudent(student: Student): Long = studentDao.insertStudent(student)
    suspend fun updateStudent(student: Student) = studentDao.updateStudent(student)
    suspend fun deleteStudent(student: Student) = studentDao.deleteStudent(student)

    // Attendance
    val allAttendance: Flow<List<AttendanceRecord>> = attendanceDao.getAllAttendance()
    fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>> = attendanceDao.getAttendanceByDate(date)
    fun getAttendanceByDateAndClass(date: String, className: String): Flow<List<AttendanceRecord>> =
        attendanceDao.getAttendanceByDateAndClass(date, className)
    fun getAttendanceForStudent(studentId: Long): Flow<List<AttendanceRecord>> = attendanceDao.getAttendanceForStudent(studentId)
    suspend fun saveAttendance(record: AttendanceRecord): Long = attendanceDao.insertAttendance(record)
    suspend fun saveAttendanceList(records: List<AttendanceRecord>) = attendanceDao.insertAttendanceList(records)

    // Grades
    val allGrades: Flow<List<GradeRecord>> = gradeDao.getAllGrades()
    fun getGradesForStudent(studentId: Long): Flow<List<GradeRecord>> = gradeDao.getGradesForStudent(studentId)
    fun getGradesBySubject(subject: String): Flow<List<GradeRecord>> = gradeDao.getGradesBySubject(subject)
    suspend fun insertGrade(grade: GradeRecord): Long = gradeDao.insertGrade(grade)
    suspend fun deleteGrade(grade: GradeRecord) = gradeDao.deleteGrade(grade)

    // Quizzes
    val allQuizzes: Flow<List<Quiz>> = quizDao.getAllQuizzes()
    suspend fun getQuizById(id: Long): Quiz? = quizDao.getQuizById(id)
    suspend fun getQuizByCode(code: String): Quiz? = quizDao.getQuizByCode(code)
    suspend fun insertQuiz(quiz: Quiz): Long = quizDao.insertQuiz(quiz)
    suspend fun deleteQuiz(quiz: Quiz) = quizDao.deleteQuiz(quiz)

    // Quiz Questions
    fun getQuestionsForQuiz(quizId: Long): Flow<List<QuizQuestion>> = quizDao.getQuestionsForQuiz(quizId)
    suspend fun getQuestionsListForQuiz(quizId: Long): List<QuizQuestion> = quizDao.getQuestionsListForQuiz(quizId)
    suspend fun insertQuestions(questions: List<QuizQuestion>) = quizDao.insertQuestions(questions)
    suspend fun insertQuestion(question: QuizQuestion): Long = quizDao.insertQuestion(question)

    // Quiz Results
    fun getResultsForQuiz(quizId: Long): Flow<List<QuizResult>> = quizDao.getResultsForQuiz(quizId)
    suspend fun insertQuizResult(result: QuizResult): Long = quizDao.insertQuizResult(result)

    // Homework
    val allHomework: Flow<List<Homework>> = homeworkDao.getAllHomework()
    fun getHomeworkByClass(className: String): Flow<List<Homework>> = homeworkDao.getHomeworkByClass(className)
    suspend fun insertHomework(homework: Homework): Long {
        val hwId = homeworkDao.insertHomework(homework)
        if (homework.syncedToCalendar) {
            // Auto sync to schedule events
            scheduleDao.insertEvent(
                ScheduleEvent(
                    title = "Tenggat PR: ${homework.title}",
                    eventType = EventType.TENGGAT_TUGAS,
                    subject = homework.subject,
                    className = homework.className,
                    date = homework.dueDate,
                    startTime = homework.dueTime,
                    endTime = homework.dueTime,
                    location = "Pengumpulan Kelas",
                    reminderEnabled = true
                )
            )
        }
        return hwId
    }
    suspend fun updateHomework(homework: Homework) = homeworkDao.updateHomework(homework)
    suspend fun deleteHomework(homework: Homework) = homeworkDao.deleteHomework(homework)

    // Schedule
    val allScheduleEvents: Flow<List<ScheduleEvent>> = scheduleDao.getAllScheduleEvents()
    fun getEventsByDate(date: String): Flow<List<ScheduleEvent>> = scheduleDao.getEventsByDate(date)
    suspend fun insertScheduleEvent(event: ScheduleEvent): Long = scheduleDao.insertEvent(event)
    suspend fun deleteScheduleEvent(event: ScheduleEvent) = scheduleDao.deleteEvent(event)

    // Chat
    val allChatMessages: Flow<List<ChatMessage>> = chatDao.getAllMessages()
    fun getMessagesForStudent(studentId: Long): Flow<List<ChatMessage>> = chatDao.getMessagesForStudent(studentId)
    suspend fun sendChatMessage(message: ChatMessage): Long = chatDao.insertMessage(message)

    // Alerts
    val allPushAlerts: Flow<List<PushAlert>> = pushAlertDao.getAllAlerts()
    suspend fun insertAlert(alert: PushAlert): Long = pushAlertDao.insertAlert(alert)
    suspend fun markAlertAsRead(id: Long) = pushAlertDao.markAsRead(id)

    suspend fun initializeDummyDataIfEmpty() = withContext(Dispatchers.IO) {
        val existingStudents = studentDao.getAllStudents().firstOrNull()
        if (existingStudents.isNullOrEmpty()) {
            studentDao.insertStudents(DummyDataGenerator.getInitialStudents())
            
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            attendanceDao.insertAttendanceList(DummyDataGenerator.getInitialAttendance(todayStr))
            
            gradeDao.insertGrades(DummyDataGenerator.getInitialGrades())
            
            val quizzes = DummyDataGenerator.getInitialQuizzes()
            quizzes.forEach { quiz ->
                quizDao.insertQuiz(quiz)
            }
            quizDao.insertQuestions(DummyDataGenerator.getInitialQuizQuestions())
            
            homeworkDao.insertHomeworkList(DummyDataGenerator.getInitialHomeworks())
            scheduleDao.insertEvents(DummyDataGenerator.getInitialScheduleEvents())
            chatDao.insertMessages(DummyDataGenerator.getInitialChatMessages())
            pushAlertDao.insertAlerts(DummyDataGenerator.getInitialAlerts())
        }
    }
}
