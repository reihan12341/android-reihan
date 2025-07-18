package com.example.tareihan.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.tareihan.R
import com.example.tareihan.dto.datastore.DataStoreManager
import com.example.tareihan.viewmodel.authviewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import org.koin.androidx.compose.getKoin
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.tareihan.dto.datastore.NewDataStoreManager
import com.example.tareihan.routes.routes
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = koinViewModel()
) {


//    val dataStoreManager: DataStoreManager = koinInject()

    val context = LocalContext.current
    val dataStoreManager = remember { NewDataStoreManager.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisible by remember { mutableStateOf(false) }

    val loginState by authViewModel.loginState.collectAsState()
    val isLogin = authViewModel.isLogin
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

//        if(isLogin){
//            navController.navigate(routes.dashboard) {
//                popUpTo("login") { inclusive = true }
//            }
//        }
        // Logo dan judul
        Icon(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            tint = Color.Unspecified,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email Input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Toggle Password Visibility"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Login
        Button(
            onClick = {
                authViewModel.login(email.text.trim(), password.text)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4CFA)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Log In", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status login
//        loginState?.let {
//            when {
//                it.isSuccess -> {
//                    val response = it.getOrNull()
//                    LaunchedEffect(response) {
//                        response?.token?.let { token ->
//                            dataStoreManager.saveAuthToken(token)
//                            // Simpan user data
//                            response.user?.let { user ->
//                                coroutineScope.launch {
//                                    dataStoreManager.clearUserData()
//                                    dataStoreManager.clearAuthToken()
//                                }
//
//                                dataStoreManager.saveUserData(
//                                    id = user.id.toString(),
//                                    name = user.name!!,
//                                    email = user.email!!,
//                                    jabatan = user.jabatan!!,
//                                    jenisKelamin = user.jenis_kelamin!!
//                                )
//                            }
//                            // Navigasi setelah login
//                            navController.navigate(routes.dashboard) {
//                                popUpTo("login") { inclusive = true }
//                            }
//                        }
//                    }
//
////                    LaunchedEffect(response) {
////                        response?.token?.let { token ->
////                            dataStoreManager.saveAuthToken(token)
////                            // Navigasi setelah login sukses
////                            navController.navigate(routes.dashboard) {
////                                popUpTo("login") { inclusive = true }
////                            }
////                        }
////                    }
//                }
//                it.isFailure -> {
//                    Text(
//                        text = it.exceptionOrNull()?.localizedMessage ?: "Login gagal",
//                        color = Color.Red,
//                        modifier = Modifier.padding(top = 8.dp)
//                    )
//                }
//            }
//        }

        loginState?.let {
            when {
                it.isSuccess -> {
                    val response = it.getOrNull()
                    LaunchedEffect(response) {
                        if (response != null && response.success) {
//                            // Simpan token
//                            dataStoreManager.saveAuthToken(response.token)
//
//                            // Simpan user data
//                            response.user.let { user ->
//                                dataStoreManager.saveUserData(
//                                    id = user.id.toString(),
//                                    name = user.name.orEmpty(),
//                                    email = user.email.orEmpty(),
//                                    jabatan = user.jabatan.orEmpty(),
//                                    jenisKelamin = user.jenis_kelamin.orEmpty()
//                                )
//                            }
//
//                            Log.d("LoginResponse", "User name: ${response.user.name}")

                            dataStoreManager.saveAuthToken(response.token)
                            dataStoreManager.saveUserData(
                                id = response.user.id.toString(),
                                email = response.user.email ?: "",
                                name = response.user.name ?: "",
                                jabatan = response.user.jabatan ?: "",
                                jenisKelamin = response.user.jenis_kelamin ?: ""
                            )

                            // Navigasi
                            navController.navigate(routes.dashboard) {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                }
                it.isFailure -> {
                    Text(
                        text = it.exceptionOrNull()?.localizedMessage ?: "Login gagal",
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Belum punya akun?",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Daftar di sini",
                color = Color(0xFF6A4CFA),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate("register") // Sesuaikan dengan nama route register kamu
                }
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(rememberNavController())
}
