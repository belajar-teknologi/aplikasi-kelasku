package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.*

class Converters {
    @TypeConverter
    fun fromAttendanceStatus(status: AttendanceStatus): String = status.name

    @TypeConverter
    fun toAttendanceStatus(value: String): AttendanceStatus =
        try { AttendanceStatus.valueOf(value) } catch (e: Exception) { AttendanceStatus.HADIR }

    @TypeConverter
    fun fromGradeType(type: GradeType): String = type.name

    @TypeConverter
    fun toGradeType(value: String): GradeType =
        try { GradeType.valueOf(value) } catch (e: Exception) { GradeType.TUGAS }

    @TypeConverter
    fun fromEventType(type: EventType): String = type.name

    @TypeConverter
    fun toEventType(value: String): EventType =
        try { EventType.valueOf(value) } catch (e: Exception) { EventType.JADWAL_BELAJAR }
}

@Database(
    entities = [
        Student::class,
        AttendanceRecord::class,
        GradeRecord::class,
        Quiz::class,
        QuizQuestion::class,
        QuizResult::class,
        Homework::class,
        ScheduleEvent::class,
        ChatMessage::class,
        PushAlert::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun gradeDao(): GradeDao
    abstract fun quizDao(): QuizDao
    abstract fun homeworkDao(): HomeworkDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun chatDao(): ChatDao
    abstract fun pushAlertDao(): PushAlertDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "guru_pintar_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
