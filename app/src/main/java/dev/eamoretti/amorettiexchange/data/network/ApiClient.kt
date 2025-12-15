package dev.eamoretti.amorettiexchange.data.network

import android.content.Context
import android.content.SharedPreferences
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // URL DE TU SERVIDOR EN DIGITALOCEAN
    private const val BASE_URL = "https://api.amorettiexchange.tech/"

    private const val PREFS_NAME = "CambistaCache"
    private const val KEY_TOKEN = "auth_token"

    private var retrofit: Retrofit? = null

    fun getClient(context: Context): Retrofit {
        if (retrofit == null) {

            // Interceptor para agregar el Token automáticamente
            val clientBuilder = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()

                    // Leer Token guardado
                    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    val token = prefs.getString(KEY_TOKEN, null)

                    if (token != null) {
                        requestBuilder.header("Authorization", "Bearer $token")
                    }

                    val request = requestBuilder.build()
                    chain.proceed(request)
                }

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}