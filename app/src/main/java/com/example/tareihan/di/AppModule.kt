package com.example.tareihanh.di

import android.content.Context
import com.example.tareihan.di.env
import com.example.tareihan.dto.authinterceptor.AuthInterceptor
import com.example.tareihan.dto.datastore.DataStoreManager
import com.example.tareihan.ui.screens.ScheduleItem.ScheduleViewModel
import com.example.tareihan.viewmodel.*
import com.example.tareihan.viewmodel.auditor.AuditorViewModel
import com.example.tareihan.viewmodel.authviewmodel.AuthViewModel
import com.example.tareihan.viewmodel.disposisi_irbanda.Disposisi_irbandaViewModel
import com.example.tareihan.viewmodel.dumas.DumasViewModel
import com.example.tareihan.viewmodel.surat_tugas.SuratTugasViewModel
import com.example.tareihan.viewmodel.temuan.TemuanViewModel
import com.example.tareihan.viewmodel.unit_kerja.UnitKerjaViewModel
import com.example.tareihan.viewmodel.user.userViewModel
import com.example.tareihanh.dto.retrofit_interface.ApiService
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import com.google.gson.GsonBuilder // <<--- tambahkan import ini

val appModule = module {

    single { (context: Context) -> DataStoreManager(context) }
    single { DataStoreManager(get()) } // get() akan ambil Context

    single {
        var cachedToken: String? = null
//        val dataStoreManager: DataStoreManager = get { parametersOf(androidContext()) }
//        runBlocking {
//            cachedToken = dataStoreManager.authTokenFlow.firstOrNull()
//        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor { cachedToken })
            .addInterceptor(logging)
            .connectTimeout(500, TimeUnit.SECONDS)
            .readTimeout(500, TimeUnit.SECONDS)
            .writeTimeout(500, TimeUnit.SECONDS)
            .protocols(listOf(Protocol.HTTP_1_1)) // prevent HTTP/2 SETTINGS error
            .build()
    }

    single {
        val gson = GsonBuilder()
            .setLenient() // inilah bagian penting yang kamu minta
            .create()

        Retrofit.Builder()
            .baseUrl("${env.url}/api/")// pastikan valid
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    single<ApiService> {
        get<Retrofit>().create(ApiService::class.java)
    }

    // ViewModels
    viewModel { AuditorViewModel(get()) }
    viewModel { DumasViewModel(get(),get()) }
    viewModel { TemuanViewModel(get()) }
    viewModel { UnitKerjaViewModel(get()) }
    viewModel { userViewModel(get()) }
    viewModel { Disposisi_irbandaViewModel(get()) }
    viewModel { SuratTugasViewModel(get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { ScheduleViewModel(get()) }
}
