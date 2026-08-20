package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE className = :className ORDER BY name ASC")
    fun getStudentsByClass(className: String): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: Long): Student?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_records WHERE date = :date ORDER BY id ASC")
    fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE date = :date AND className = :className")
    fun getAttendanceByDateAndClass(date: String, className: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceForStudent(studentId: Long): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records ORDER BY date DESC")
    fun getAllAttendance(): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(record: AttendanceRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(records: List<AttendanceRecord>)

    @Query("DELETE FROM attendance_records WHERE studentId = :studentId AND date = :date")
    suspend fun deleteAttendanceForDate(studentId: Long, date: String)
}

@Dao
interface GradeDao {
    @Query("SELECT * FROM grade_records ORDER BY date DESC")
    fun getAllGrades(): Flow<List<GradeRecord>>

    @Query("SELECT * FROM grade_records WHERE studentId = :studentId ORDER BY date DESC")
    fun getGradesForStudent(studentId: Long): Flow<List<GradeRecord>>

    @Query("SELECT * FROM grade_records WHERE subject = :subject ORDER BY score DESC")
    fun getGradesBySubject(subject: String): Flow<List<GradeRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grade: GradeRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrades(grades: List<GradeRecord>)

    @Delete
    suspend fun deleteGrade(grade: GradeRecord)
}

@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes ORDER BY createdAt DESC")
    fun getAllQuizzes(): Flow<List<Quiz>>

    @Query("SELECT * FROM quizzes WHERE id = :id")
    suspend fun getQuizById(id: Long): Quiz?

    @Query("SELECT * FROM quizzes WHERE shareCode = :code LIMIT 1")
    suspend fun getQuizByCode(code: String): Quiz?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: Quiz): Long

    @Delete
    suspend fun deleteQuiz(quiz: Quiz)

    // Questions
    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId ORDER BY id ASC")
    fun getQuestionsForQuiz(quizId: Long): Flow<List<QuizQuestion>>

    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId ORDER BY id ASC")
    suspend fun getQuestionsListForQuiz(quizId: Long): List<QuizQuestion>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuizQuestion>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuizQuestion): Long

    // Results
    @Query("SELECT * FROM quiz_results WHERE quizId = :quizId ORDER BY completedAt DESC")
    fun getResultsForQuiz(quizId: Long): Flow<List<QuizResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResult): Long
}

@Dao
interface HomeworkDao {
    @Query("SELECT * FROM homeworks ORDER BY dueDate ASC")
    fun getAllHomework(): Flow<List<Homework>>

    @Query("SELECT * FROM homeworks WHERE className = :className ORDER BY dueDate ASC")
    fun getHomeworkByClass(className: String): Flow<List<Homework>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomework(homework: Homework): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeworkList(homeworks: List<Homework>)

    @Update
    suspend fun updateHomework(homework: Homework)

    @Delete
    suspend fun deleteHomework(homework: Homework)
}

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule_events ORDER BY date ASC, startTime ASC")
    fun getAllScheduleEvents(): Flow<List<ScheduleEvent>>

    @Query("SELECT * FROM schedule_events WHERE date = :date ORDER BY startTime ASC")
    fun getEventsByDate(date: String): Flow<List<ScheduleEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: ScheduleEvent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<ScheduleEvent>)

    @Delete
    suspend fun deleteEvent(event: ScheduleEvent)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE studentId = :studentId ORDER BY timestamp ASC")
    fun getMessagesForStudent(studentId: Long): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessage>)
}

@Dao
interface PushAlertDao {
    @Query("SELECT * FROM push_alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<PushAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: PushAlert): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<PushAlert>)

    @Query("UPDATE push_alerts SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)
}
