package com.example.tareihan.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tareihan.R
import com.example.tareihan.dto.user.User
import com.example.tareihan.viewmodel.user.userViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun EditUserScreen(navController: NavController, id: String, userViewModel: userViewModel = koinViewModel()) {
    // State untuk form fields
    var userId by remember { mutableStateOf("") }
    var nama by remember { mutableStateOf("") }
    var nip by remember { mutableStateOf("") }
    var jenis_kelamin by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var jabatan by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // State untuk tracking apakah data sudah di-load
    var isDataLoaded by remember { mutableStateOf(false) }

    // Load user data saat screen pertama kali dibuka
    LaunchedEffect(id) {
        userViewModel.getUserById(id)
    }

    // Observe data dari ViewModel
    val user by userViewModel::userShow
    val isLoading by userViewModel::isLoading
    val errorMessage by userViewModel::errorMessage

    // Update form fields ketika data user berhasil di-load
    LaunchedEffect(user) {
        user?.let { userData ->
            if (!isDataLoaded) {
                userId = userData.id?.toString() ?: ""
                nama = userData.name ?: ""
                nip = userData.nip ?: ""
                jenis_kelamin = userData.jenis_kelamin ?: ""
                email = userData.email ?: ""
                jabatan = userData.jabatan ?: ""
                password = "" // Password tetap kosong untuk keamanan
                isDataLoaded = true

                // Debug: Print data yang diterima
                println("Debug - User data loaded:")
                println("ID: ${userData.id}")
                println("Name: ${userData.name}")
                println("NIP: ${userData.nip}")
                println("Email: ${userData.email}")
                println("Jabatan: ${userData.jabatan}")
                println("Jenis Kelamin: ${userData.jenis_kelamin}")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Show loading indicator
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF6A4CFA)
            )
        }

        // Show error message
        errorMessage?.let { error ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Error: $error",
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        isDataLoaded = false
                        userViewModel.getUserById(id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4CFA))
                ) {
                    Text("Retry", color = Color.White)
                }
            }
        }

        // Main content
        if (!isLoading && errorMessage == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Logo
                Icon(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Edit User",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Update user information",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                // Debug info - hapus setelah testing
                if (user != null) {
                    Text(
                        text = "Debug: User ID = ${user?.id}, Name = ${user?.name}",
                        fontSize = 12.sp,
                        color = Color.Blue,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ID Input (Read-only)
                OutlinedTextField(
                    value = userId,
                    onValueChange = { }, // Read-only
                    label = { Text("ID") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Nama Input
                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // NIP Input
                OutlinedTextField(
                    value = nip,
                    onValueChange = { nip = it },
                    label = { Text("NIP") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Jenis Kelamin Input
                OutlinedTextField(
                    value = jenis_kelamin,
                    onValueChange = { jenis_kelamin = it },
                    label = { Text("Jenis Kelamin") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email Input
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Jabatan Input
                OutlinedTextField(
                    value = jabatan,
                    onValueChange = { jabatan = it },
                    label = { Text("Jabatan") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Input (Optional - kosongkan jika tidak ingin mengubah)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password Baru (Opsional)") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Kosongkan password jika tidak ingin mengubah",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Update Button
                Button(
                    onClick = {
                        // Handle update user
                        val updatedUser = User(
                            id = userId.toIntOrNull(),
                            name = nama.ifBlank { null },
                            nip = nip.ifBlank { null },
                            jenis_kelamin = jenis_kelamin.ifBlank { null },
                            jabatan = jabatan.ifBlank { null },
                            email = email.ifBlank { null },
//                            password = if (password.isNotBlank()) password else null
                        )

                        // Debug print
                        println("Debug - Updating user with data:")
                        println("ID: ${updatedUser.id}")
                        println("Name: ${updatedUser.name}")
                        println("Email: ${updatedUser.email}")

                        userViewModel.putuser(
                            id = id.toInt(),
                            updatedUser
                        )

                        // Navigate back setelah update
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4CFA)),
                    shape = RoundedCornerShape(8.dp),
                    enabled = nama.isNotBlank() && email.isNotBlank()
                ) {
                    Text(text = "Update User", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cancel Button
                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Cancel", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditUserPreview() {
    EditUserScreen(
        rememberNavController(),
        id = "1",
    )
}