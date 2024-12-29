package com.evilcorp.anguish

import android.content.Context
import androidx.room.*
import com.evilcorp.anguish.BallNetwork.Section
import com.evilcorp.anguish.BallsActivity.PrintControlPont
import com.evilcorp.anguish.BallsActivity.Rating
import com.evilcorp.anguish.ui.notifications.NotificationsFragment.FacultyDiscipline
import com.evilcorp.anguish.ui.notifications.NotificationsFragment.PrintDiscipline

class RatingSQLite (private val context: Context) {

    @Entity(tableName = "Disciplines")
    data class Discipline(
        @PrimaryKey val id: Int,
        val discipline: String,
        val faculty: String
    )

    @Entity(tableName = "DisciplineDetails",
        foreignKeys = [ForeignKey(
            entity = Discipline::class,
            parentColumns = ["id"],
            childColumns = ["disciplineId"],
            onDelete = ForeignKey.CASCADE)],
        indices = [Index(value = ["disciplineId"])]
    )
    data class DisciplineDetail(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @ColumnInfo(name = "disciplineId") val disciplineId: Int,
        val order: Int,
        val title: String,
        val subTitle: String?,
        val ball: Int,
        val maxBall: Int
    )


    @Database(
        entities = [
            Discipline::class,
            DisciplineDetail::class
                   ],
        version = 1
    )
    abstract class AppDatabase : RoomDatabase() {
        abstract fun ratingDao(): RatingDao

        companion object {
            @Volatile private var instance: AppDatabase? = null

            fun getDatabase(context: Context): AppDatabase {
                return instance ?: synchronized(this) {
                    instance ?: Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "RatingSQL.db"
                    ).build().also { instance = it }
                }
            }
        }
    }

    @Dao
    interface RatingDao {
        @Insert
        suspend fun insertDiscipline(discipline: Discipline)

        @Insert
        suspend fun insertDisciplineDetail(disciplineDetail: DisciplineDetail)

        @Query("SELECT * FROM Disciplines WHERE faculty = :faculty")
        suspend fun getDisciplines(faculty: String): List<Discipline>

        @Query("SELECT * FROM DisciplineDetails WHERE title = :title")
        suspend fun getDisciplineSubTitles(title: String): List<DisciplineDetail>

        @Query("SELECT DISTINCT faculty FROM Disciplines")
        suspend fun getAllFaculties(): List<String>

        @Query("SELECT DISTINCT title FROM DisciplineDetails")
        suspend fun getAllDisciplines(): List<String>

        @Query("DELETE FROM Disciplines")
        suspend fun clearRating()

    }

    private suspend fun dbExtractionDisciplines(faculty: String): List<PrintDiscipline> {
        val db = AppDatabase.getDatabase(context)
        val ratingDao = db.ratingDao()
        val disciplines = ratingDao.getDisciplines(faculty)

        return disciplines.map { t ->
            PrintDiscipline(
                name = t.discipline
            )
        }
    }

    private suspend fun dbExtractionControlPoints(title: String): List<PrintControlPont> {
        val db = AppDatabase.getDatabase(context)
        val ratingDao = db.ratingDao()

        val controlPoints = ratingDao.getDisciplineSubTitles(title)

        return controlPoints.map { t ->
            PrintControlPont(
                name = t.subTitle,
                ball = t.ball,
                maxBall = t.maxBall
            )
        }
    }


    suspend fun dbExtractAllFaculty(): List<FacultyDiscipline> {
        val db = AppDatabase.getDatabase(context)
        val ratingDao = db.ratingDao()

        val uniqueFaculties = ratingDao.getAllFaculties()

        val facultyDisciplines = mutableListOf<FacultyDiscipline>()
        for (faculty in uniqueFaculties) {

            val disciplines = dbExtractionDisciplines(faculty)
            facultyDisciplines.add(FacultyDiscipline(faculty = faculty, disciplines = disciplines))
        }

        return facultyDisciplines
    }

    suspend fun dbExtractAllRating(): List<Rating> {
        val db = AppDatabase.getDatabase(context)
        val ratingDao = db.ratingDao()

        val uniqueTitles = ratingDao.getAllDisciplines()

        val ratings = mutableListOf<Rating>()
        for (title in uniqueTitles) {

            val controlPoints = dbExtractionControlPoints(title)
            ratings.add(Rating(title = title, controlPoints = controlPoints))
        }

        return ratings
    }


    suspend fun dbInsertFaculties(faculties: List<BallNetwork.RecordBooks>): List<Int> {
        val db = AppDatabase.getDatabase(context)
        val ratingDao = db.ratingDao()

        val ids = mutableListOf<Int>()

        for (faculty in faculties) {
            for (discipline in faculty.Disciplines) {
                val facultiesSQL = Discipline(
                    id = discipline.Id,
                    discipline = discipline.Title,
                    faculty = faculty.Faculty
                )
                ids.add(discipline.Id)
                ratingDao.insertDiscipline(facultiesSQL)
            }
        }

        return ids
    }

    suspend fun dbInsertRating(rating: List<Section>, disciplineId: Int) {
        val db = AppDatabase.getDatabase(context)
        val ratingDao = db.ratingDao()

        for (section in rating) {
            for (controlDot in section.ControlDots) {

                val disciplineDetail = DisciplineDetail(
                    disciplineId = disciplineId,
                    order = section.Order,
                    title = section.Title,
                    subTitle = controlDot.Title,
                    ball = controlDot.Mark?.Ball?.toInt() ?: 0,
                    maxBall = controlDot.MaxBall.toInt()
                )

                ratingDao.insertDisciplineDetail(disciplineDetail)
            }
        }

    }

    suspend fun clear() {
        val ratingDao = AppDatabase.getDatabase(context).ratingDao()
        ratingDao.clearRating()
    }


}