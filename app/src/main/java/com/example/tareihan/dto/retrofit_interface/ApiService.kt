package com.example.tareihanh.dto.retrofit_interface

import com.example.tareihan.dto.LoginResponse.LoginResponse
import com.example.tareihan.dto.LoginResponse.RegisterResponse
import com.example.tareihan.dto.Schedule.ScheduleResponse
import com.example.tareihan.dto.apiresponse.DataResponse
import com.example.tareihan.dto.auditor.Auditor
import com.example.tareihan.dto.disposisi_irbanda.disposisi_irbanda
import com.example.tareihan.dto.dumas.dumas
import com.example.tareihan.dto.surat_tugas.surat_tugas
import com.example.tareihan.dto.temuan.temuan
import com.example.tareihan.dto.unit_kerja.unit_kerja
import com.example.tareihan.dto.user.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ----------------------- Auditor -----------------------
    @GET("auditors")
    suspend fun get_auditor(): Response<DataResponse<List<Auditor>>>

    @POST("auditors")
    suspend fun post_auditor(@Body auditor: Auditor): Response<DataResponse<Unit>>

    @GET("auditors/{id}")
    suspend fun get_auditor_by_id(@Path("id") id: Int): Response<DataResponse<Auditor>>

    @PUT("auditors/{id}")
    suspend fun update_auditor(@Path("id") id: Int, @Body auditor: Auditor): Response<DataResponse<Unit>>

    @DELETE("auditors/{id}")
    suspend fun delete_auditor(@Path("id") id: Int): Response<DataResponse<Unit>>

    // ----------------------- Temuan -----------------------
    @GET("temuans")
    suspend fun get_temuan(): Response<DataResponse<List<temuan>>>

    @POST("temuans")
    suspend fun post_temuan(@Body temuan: temuan): Response<DataResponse<Unit>>

    @GET("temuans/{id}")
    suspend fun get_temuan_by_id(@Path("id") id: Int): Response<DataResponse<temuan>>

    @PUT("temuans/{id}")
    suspend fun update_temuan(@Path("id") id: Int, @Body temuan: temuan): Response<DataResponse<Unit>>

    @DELETE("temuans/{id}")
    suspend fun delete_temuan(@Path("id") id: String): Response<DataResponse<Unit>>

    // ----------------------- Dumas -----------------------
    @GET("dumas")
    suspend fun get_dumas(): Response<DataResponse<List<dumas>>>

    @GET("dumas/{id}")
    suspend fun get_dumas_by_id(@Path("id") id: Int): Response<DataResponse<dumas>>

    @Multipart
    @POST("dumas")
    suspend fun post_dumas(
        @Part("dumas") dumas: RequestBody,
        @Part file: MultipartBody.Part?
    ): Response<DataResponse<Unit>>

    @PUT("dumas/{id}")
    suspend fun update_dumas(@Path("id") id: Int, @Body dumas: dumas): Response<DataResponse<Unit>>

    @DELETE("dumas/{id}")
    suspend fun delete_dumas(@Path("id") id: Int): Response<DataResponse<Unit>>

    // ----------------------- Unit Kerja -----------------------
    @GET("unitkerja")
    suspend fun get_unit_kerja(): Response<DataResponse<List<unit_kerja>>>

    @GET("unitkerja/{id}")
    suspend fun get_unit_kerja_by_id(@Path("id") id: String): Response<DataResponse<unit_kerja>>

    @POST("unitkerja")
    suspend fun post_unit_kerja(@Body unitkerja: unit_kerja): Response<DataResponse<Unit>>

    @PUT("unitkerja/{id}")
    suspend fun update_unit_kerja(@Path("id") id: String, @Body unitkerja: unit_kerja): Response<DataResponse<Unit>>

    @DELETE("unitkerja/{id}")
    suspend fun delete_unit_kerja(@Path("id") id: String): Response<DataResponse<Unit>>

    // ----------------------- User -----------------------
    @GET("users")
    suspend fun get_user(): Response<DataResponse<List<User>>>

    @GET("users/{id}")
    suspend fun get_user_by_id(@Path("id") id: String): Response<DataResponse<User>>

    @POST("users")
    suspend fun post_user(@Body user: User): Response<DataResponse<Unit>>

    @PUT("users/{id}")
    suspend fun update_user(@Path("id") id: Int, @Body user: User): Response<DataResponse<Unit>>

    @DELETE("users/{id}")
    suspend fun delete_user(@Path("id") id: Int): Response<DataResponse<Unit>>

    // ----------------------- Auth -----------------------
    @POST("login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @POST("register")
    @FormUrlEncoded
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("jenis_kelamin") jenis_kelamin: String,
        @Field("password") password: String
    ): Response<RegisterResponse>



    // ----------------------- Disposisi Irbanda -----------------------
    @GET("DisposisiIrbanda")
    suspend fun get_DisposisiIrbanda(): Response<DataResponse<List<disposisi_irbanda>>>

    @GET("DisposisiIrbanda/{id}")
    suspend fun get_DisposisiIrbanda_by_id(@Path("id") id: Int): Response<DataResponse<disposisi_irbanda>>

    @POST("DisposisiIrbanda")
    suspend fun post_DisposisiIrbanda(@Body data: disposisi_irbanda): Response<DataResponse<Unit>>

    @PUT("DisposisiIrbanda/{id}")
    suspend fun update_DisposisiIrbanda(@Path("id") id: Int, @Body data: disposisi_irbanda): Response<DataResponse<Unit>>

    @DELETE("DisposisiIrbanda/{id}")
    suspend fun delete_DisposisiIrbanda(@Path("id") id: Int): Response<DataResponse<Unit>>

    // ----------------------- Surat Tugas -----------------------
    @GET("surat-tugas")
    suspend fun get_surat_tugas(): Response<DataResponse<List<surat_tugas>>>

    @GET("surat-tugas/{id}")
    suspend fun get_surat_tugas_by_id(@Path("id") id: Int): Response<DataResponse<surat_tugas>>

    @POST("surat-tugas")
    suspend fun post_surat_tugas(@Body data: surat_tugas): Response<DataResponse<Unit>>

    @PUT("surat-tugas/{id}")
    suspend fun update_surat_tugas(@Path("id") id: Int, @Body data: surat_tugas): Response<DataResponse<Unit>>

    @DELETE("surat-tugas/{id}")
    suspend fun delete_surat_tugas(@Path("id") id: Int): Response<DataResponse<Unit>>

    @GET("schedule/generate")
    suspend fun getSchedules(): ScheduleResponse


}
