package com.evilcorp.anguish

import android.content.Context
import androidx.room.*
import com.evilcorp.anguish.GetRequestAndEtc.TimeTableClass
import com.evilcorp.anguish.TimeTableWeekActivity.DaySchedule
import com.evilcorp.anguish.TimeTableWeekActivity.PrintTimeTableClass
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class TimeTableSQLite (private val context: Context) {

    @Entity(tableName = "TimeTable")
    data class TimeTableClassSQL(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val date: String,
        val number: Int,
        val subgroupCount: Int,
        val title: String,
        val group: String,
        val subgroupNumber: Int,
        val teacher: String,
        val auditNumber: String,
        val campusTitle: String
    )



    @Database(
        entities = [
            TimeTableClassSQL::class
                   ],
        version = 1
    )
    abstract class AppDatabase : RoomDatabase() {
        abstract fun timeTableDao(): TimeTableDao

        companion object {
            @Volatile private var instance: AppDatabase? = null

            fun getDatabase(context: Context): AppDatabase {
                return instance ?: synchronized(this) {
                    instance ?: Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "TimeTableSQL.db"
                    ).build().also { instance = it }
                }
            }
        }
    }

    @Dao
    interface TimeTableDao {
        @Insert
        suspend fun insertTimeTable(timeTableClassSQL: TimeTableClassSQL)

        @Query("SELECT * FROM TimeTable WHERE date = :date")
        suspend fun getTimeTableByDate(date: String): List<TimeTableClassSQL>?

        @Query("SELECT DISTINCT date FROM TimeTable")
        suspend fun getAllDates(): List<String>

        @Query("DELETE FROM TimeTable")
        suspend fun clearTimeTable()

    }

    private suspend fun dbExtraction(date: String): List<PrintTimeTableClass> {
        val db = AppDatabase.getDatabase(context)
        val timeTableDao = db.timeTableDao()

        val timeTableSQL = timeTableDao.getTimeTableByDate(date) ?: return emptyList()

        return timeTableSQL.map { t ->
            PrintTimeTableClass(
                number = t.number,
                subgroupCount = t.subgroupCount,
                title = t.title,
                group = t.group,
                subgroupNumber = t.subgroupNumber,
                teacher = t.teacher,
                auditNumber = t.auditNumber,
                campusTitle = t.campusTitle
            )
        }
    }

    suspend fun dbExtractTwoDays(): List<DaySchedule> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDateTime = ZonedDateTime.now(ZoneId.of("UTC"))
        val now = currentDateTime
        val tomorrow = currentDateTime.plusDays(1)

        val lessonsToday = dbExtraction(now.format(formatter))
        val lessonsTomorrow = dbExtraction(tomorrow.format(formatter))

        val nowDisplay = DayOfWeek.from(now).getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())
        val tomorrowDisplay = DayOfWeek.from(tomorrow).getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())

        return listOf(
            DaySchedule(dayTitle = nowDisplay, lessons = lessonsToday),
            DaySchedule(dayTitle = tomorrowDisplay, lessons = lessonsTomorrow)
        )
    }


    suspend fun dbExtractAll(): List<DaySchedule> {
        val db = AppDatabase.getDatabase(context)
        val timeTableDao = db.timeTableDao()

        val uniqueDates = timeTableDao.getAllDates()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val daySchedules = mutableListOf<DaySchedule>()
        for (date in uniqueDates) {

            val lessons = dbExtraction(date)
            val localDate = LocalDate.parse(date, formatter)
            val dayDisplayName = DayOfWeek.from(localDate).getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())

            daySchedules.add(DaySchedule(dayTitle = dayDisplayName, lessons = lessons))
        }

        return daySchedules
    }


    suspend fun dbInsert(timeTables: List<TimeTableClass>, date: String) {
        val db = AppDatabase.getDatabase(context)
        val timeTableDao = db.timeTableDao()

        for (timeTable in timeTables) {
            for (lesson in timeTable.TimeTable.Lessons) {
                for (discipline in lesson.Disciplines) {
                    val timeTableSQL = TimeTableClassSQL(
                        date = date,
                        number = lesson.Number,
                        subgroupCount = lesson.SubgroupCount,
                        title = discipline.Title,
                        group = discipline.Group,
                        subgroupNumber = discipline.SubgroupNumber,
                        teacher = discipline.Teacher.FIO,
                        auditNumber = discipline.Auditorium.Number,
                        campusTitle = discipline.Auditorium.CampusTitle
                    )

                    timeTableDao.insertTimeTable(timeTableSQL)
                }
            }
        }
    }

    suspend fun clear() {
        val timeTableDao = AppDatabase.getDatabase(context).timeTableDao()
        timeTableDao.clearTimeTable()
    }


}