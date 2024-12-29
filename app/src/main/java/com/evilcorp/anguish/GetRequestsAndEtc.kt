package com.evilcorp.anguish

import TokenManager
import android.content.Context
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.io.File
import java.net.URL
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Base64


class GetRequestAndEtc(private val context: Context) {

    private lateinit var tokenManager: TokenManager
    private lateinit var timeTableSQLite: TimeTableSQLite
    private lateinit var ballNetwork: BallNetwork
    private val client = HttpClient(CIO) {
        install(ContentNegotiation){
            json(Json {
                coerceInputValues = true
                ignoreUnknownKeys = true
            })
        }
    }

    @Serializable
    data class UserProfileClass(
        val FIO: String,
        val BirthDate: String,
        val Email: String,
        val Photo: Photo
    )

    @Serializable
    data class Photo(
        val UrlSmall: String,
        val UrlMedium: String,
        val UrlSource: String
    )

    @Serializable
    data class TimeTableClass(
        val Group: String,
        val TimeTable: TimeTable
    )

    @Serializable
    data class TimeTable(
        val Date: String,
        val Lessons: List<Lesson>
    )

    @Serializable
    data class Lesson(
        val Number: Int,
        val SubgroupCount: Int,
        val Disciplines: List<Discipline>
    )

    @Serializable
    data class Discipline(
        val Title: String,
        val Group: String,
        val SubgroupNumber: Int,
        val Teacher: Teacher,
        val Auditorium: Auditorium
    )

    @Serializable
    data class Teacher(
        val FIO: String
    )

    @Serializable
    data class Auditorium(
        val Number: String,
        val CampusTitle: String
    )


    suspend fun authenticateUser(username: String, password: String): String {

        try {
            val clientId = "8"
            val clientSecret = "qweasd"
            val authHeader = "Basic " + Base64.getEncoder().encodeToString("$clientId:$clientSecret".toByteArray())
            val response: HttpResponse = client.post("https://p.mrsu.ru/OAuth/Token") {
                contentType(ContentType.Application.FormUrlEncoded)
                header(HttpHeaders.Authorization, authHeader)
                setBody (FormDataContent(Parameters.build {
                    append("grant_type", "password")
                    append("username", username)
                    append("password", password)
                }))
            }

            if (response.status == HttpStatusCode.OK) {

                val jsonResponse = JSONObject(response.bodyAsText())
                val accessToken = jsonResponse.getString("access_token")
                val refreshToken = jsonResponse.getString("refresh_token")

                tokenManager = TokenManager(context)
                tokenManager.saveTokens(accessToken, refreshToken)
                tokenManager.saveCredentials(username, password)

                getUserProfile(accessToken)
                getTimeTable(accessToken)

                saveDataToTextFile("UpdateDate", ZonedDateTime.now(ZoneId.of("UTC")).format(formatter).toString())

                return "Success"

            } else {
                return "Wrong Login or Password"
            }
        } catch (e: Exception) {
            return e.message.toString()
        }
    }

    suspend fun getUserProfile(token: String): String {
            try {
                val userData: UserProfileClass = client.get("https://papi.mrsu.ru/v1/User") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }.body()

                saveDataToTextFile("UserData", "${userData.FIO} ${userData.BirthDate}")
                downloadAndSavePhoto(userData.Photo.UrlMedium)

                return "Success"

            } catch (e: Exception) {
                return e.message.toString()
                Log.e("getProf", e.message.toString())
            }
    }

    suspend fun getUserBalls(token: String): String {

        ballNetwork = BallNetwork(context, client)

        if (ballNetwork.getUserRating(token) != "Success") {
            return "Some Error in getUserDisciplines"
        }

        return "Success"
    }


    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    suspend fun getTimeTableFor2Weeks(token: String) {
        timeTableSQLite = TimeTableSQLite(context)
        for (i in 0..13) {
            val date = ZonedDateTime.now(ZoneId.of("UTC")).plusDays(i.toLong()).format(formatter)
            try {
                val timeTable: List<TimeTableClass> = client.get("https://papi.mrsu.ru/v1/StudentTimeTable?date=$date") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }.body()

                timeTableSQLite.dbInsert(timeTable, date)

            } catch (e: Exception) {
                println("Error fetching time table for $date: $e")
            }
        }
        saveDataToTextFile("UpdateDate", ZonedDateTime.now(ZoneId.of("UTC")).format(formatter))
    }

    fun needUpdateTimeTable(): Boolean {
        val currentYear = ZonedDateTime.now(ZoneId.of("UTC")).year
        val updDate = LocalDate.parse(File(context.filesDir, "UpdateDate.txt").readText(), formatter)

        if (updDate.year != currentYear) {
            return true
        }

        val period1Start = LocalDate.of(currentYear, 9, 1)
        val period1End = LocalDate.of(currentYear + 1, 1, 31)
        val period2Start = LocalDate.of(currentYear, 2, 1)
        val period2End = LocalDate.of(currentYear, 8, 31)

        return (updDate.isBefore(period1Start) || updDate.isAfter(period1End)) &&
                (updDate.isBefore(period2Start) || updDate.isAfter(period2End))
    }


    suspend fun getTimeTable(token: String) {

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDateTime = ZonedDateTime.now(ZoneId.of("UTC")).format(formatter)

        try {
            val timeTable: List<TimeTableClass> = client.get("https://papi.mrsu.ru/v1/StudentTimeTable?date=$currentDateTime") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }.body()

            saveTimeTableToFile("TimeTable_$currentDateTime", timeTable)

        } catch (e: Exception) {
            Log.e("MyApp", e.message.toString())
        }
    }

    fun getTimeTablePosition(): ZonedDateTime {

        val now = ZonedDateTime.now(ZoneId.of("UTC"))
        val updDateStr = File(context.filesDir, "UpdateDate.txt").readText()
        val updDate = ZonedDateTime.of(LocalDate.parse(updDateStr, formatter), LocalTime.MIN, ZoneId.of("UTC"))

        val difference = ChronoUnit.DAYS.between(now, updDate)
        val remainder = difference % 14

        val newUpdateDate = updDate.plusDays(remainder.toLong())

        return newUpdateDate

    }

    fun saveDataToTextFile(fileName: String, dataText: String) {
        try {
            File(context.filesDir, "$fileName.txt").writeText(dataText)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun downloadAndSavePhoto(photoUrl: String) {
        try {
            val photoBytes = URL(photoUrl).readBytes()
            File(context.filesDir, "profile_photo.jpg").writeBytes(photoBytes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun saveTimeTableToFile(fileName: String, data: List<TimeTableClass>) {
        val json = Json { prettyPrint = true }
        val jsonData = json.encodeToString(ListSerializer(TimeTableClass.serializer()), data)

        try {
            File(context.filesDir, "$fileName.json").writeText(jsonData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
