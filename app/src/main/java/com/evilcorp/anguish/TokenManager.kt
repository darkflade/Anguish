import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.evilcorp.anguish.RatingSQLite
import com.evilcorp.anguish.TimeTableSQLite


class TokenManager(private val context: Context) {

    private lateinit var timeTableSQLite: TimeTableSQLite
    private lateinit var ratingSQLite: RatingSQLite

    //val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
/*
    // Initialize/open an instance of EncryptedSharedPreferences on below line.
    val sharedPreferences = EncryptedSharedPreferences.create(
        // passing a file name to share a preferences
        "preferences",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    */
    private val sharedPreferences = EncryptedSharedPreferences.create(
        "secure_prefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCredentials(username: String, password: String) {
        with(sharedPreferences.edit()) {
            putString("USERNAME", username)
            putString("PASSWORD", password)
            apply()
        }
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        with(sharedPreferences.edit()) {
            putString("ACCESS_TOKEN", accessToken)
            putString("REFRESH_TOKEN", refreshToken)
            apply()
        }

    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("ACCESS_TOKEN", null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("REFRESH_TOKEN", null)
    }
    /*
    val response: HttpResponse = client.post("https://p.mrsu.ru/OAuth/Token") {
    contentType(ContentType.Application.FormUrlEncoded)
    header(HttpHeaders.Authorization, authHeader)
    setBody(FormDataContent(Parameters.build {              для рефреша
        append("grant_type", "refresh_token")
        append("refresh_token", refreshToken)
    }))
}
     */

    fun getUsername(): String? {
        return sharedPreferences.getString("USERNAME", null)
    }

    fun getPassword(): String? {
        return sharedPreferences.getString("PASSWORD", null)
    }

    suspend fun clearCredentials() {
        timeTableSQLite = TimeTableSQLite(context)
        ratingSQLite = RatingSQLite(context)

        timeTableSQLite.clear()
        ratingSQLite.clear()

        with(sharedPreferences.edit()) {
            remove("USERNAME")
            remove("PASSWORD")
            remove("ACCESS_TOKEN")
            remove("REFRESH_TOKEN")
            apply()
        }
    }

    suspend fun clearToken() {
        timeTableSQLite = TimeTableSQLite(context)
        timeTableSQLite.clear()
        with(sharedPreferences.edit()) {
            remove("ACCESS_TOKEN")
            remove("REFRESH_TOKEN")
            apply()
        }
    }

}
