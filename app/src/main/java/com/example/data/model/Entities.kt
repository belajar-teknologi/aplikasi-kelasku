package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nis: String,
    val name: String,
    val className: String,
    val gender: String, // "L" or "P"
    val parentName: String,
    val parentPhone: String, // e.g. 081234567890
    val parentEmail: String,
    val address: String = "Jl. Pendidikan No. 12",
    val notes: String = ""
)

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val date: String, // YYYY-MM-DD
    val status: AttendanceStatus, // HADIR, SAKIT, IZIN, ALPA
    val notes: String = "",
    val className: String
)

enum class AttendanceStatus {
    HADIR,
    SAKIT,
    IZIN,
    ALPA
}

@Entity(tableName = "grade_records")
data class GradeRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val subject: String, // Matematika, Bahasa Indonesia, IPA, IPS, PPKn, Bahasa Inggris
    val type: GradeType, // TUGAS, KUIS, UTS, UAS, PRAKTIK
    val score: Double,
    val maxScore: Double = 100.0,
    val date: String,
    val notes: String = ""
)

enum class GradeType {
    TUGAS,
    KUIS,
    UTS,
    UAS,
    PRAKTIK
}

@Entity(tableName = "quizzes")
data class Quiz(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val className: String,
    val durationMinutes: Int = 15,
    val shareCode: String, // e.g. "KUIS-5A-MAT"
    val materialSummary: String = "",
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_questions")
data class QuizQuestion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quizId: Long,
    val questionText: String,
    val imageUrl: String? = null,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int, // 0=A, 1=B, 2=C, 3=D
    val explanation: String = ""
)

@Entity(tableName = "quiz_results")
data class QuizResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quizId: Long,
    val studentId: Long,
    val studentName: String,
    val score: Double,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "homeworks")
data class Homework(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val className: String,
    val description: String,
    val dueDate: String, // YYYY-MM-DD
    val dueTime: String = "23:59",
    val isCompleted: Boolean = false,
    val syncedToCalendar: Boolean = true
)

@Entity(tableName = "schedule_events")
data class ScheduleEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val eventType: EventType, // JADWAL_BELAJAR, UJIAN, TENGGAT_TUGAS, RAPAT_WALI
    val subject: String = "",
    val className: String = "",
    val date: String, // YYYY-MM-DD
    val startTime: String = "08:00",
    val endTime: String = "09:30",
    val location: String = "Ruang Kelas 5A",
    val reminderEnabled: Boolean = true
)

enum class EventType {
    JADWAL_BELAJAR,
    UJIAN,
    TENGGAT_TUGAS,
    RAPAT_WALI,
    KEGIATAN_SEKOLAH
}

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val parentName: String,
    val studentName: String,
    val message: String,
    val isFromTeacher: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val tag: String = "KONSULTASI" // KONSULTASI, NILAI, KEHADIRAN, TUGAS
)

@Entity(tableName = "push_alerts")
data class PushAlert(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val type: String, // UJIAN, TUGAS, ABSENSI, LAPORAN
    val targetDate: String,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
