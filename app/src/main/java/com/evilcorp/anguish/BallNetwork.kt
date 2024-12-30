package com.evilcorp.anguish

import android.content.Context
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.Serializable

class BallNetwork(private val context: Context, private val client: HttpClient) {

    private lateinit var ratingSQLite: RatingSQLite


    @Serializable
    data class FacultyRecord(
        val RecordBooks: List<RecordBooks>
    )

    @Serializable
    data class RecordBooks(
        val Faculty: String,
        val Disciplines: List<Discipline>
    )

    @Serializable
    data class Discipline(
        val Id: Int,
        val Title: String
    )

//------------------------------------------------------------------------------------------------------------
    @Serializable
    data class Assessment(
            val Sections: List<Section>
            )

    @Serializable
    data class Section(
        val ControlDots: List<ControlDot>,
        val Title: String,
        val Order: Int
    )

    @Serializable
    data class ControlDot(
        val Mark: Mark? = null,
        val Title: String? = "Artem ate title",
        val MaxBall: Double,
        val Order: Int

    )

    @Serializable
    data class Mark(
        val Ball: Double
    )

    suspend fun getUserRating(token: String): String {
        ratingSQLite = RatingSQLite(context)
        try {
            val userDisciplines: FacultyRecord = client.get("https://papi.mrsu.ru/v1/StudentSemester?selector=current") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }.body()

            val disciplineIds = ratingSQLite.dbInsertFaculties(userDisciplines.RecordBooks)

            for (id in disciplineIds) {
                val userBalls: Assessment =
                    client.get("https://papi.mrsu.ru/v1/StudentRatingPlan/$id") {
                        headers {
                            append(HttpHeaders.Authorization, "Bearer $token")
                        }
                    }.body()
                ratingSQLite.dbInsertRating(userBalls.Sections, id)
            }


            return "Success"

        } catch (e: ClientRequestException) {
            Log.e("getUserRating", "HTTP error: ${e.response.status.value} - ${e.message}")
            return e.message
        }
    }
}