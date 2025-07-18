package com.example.tareihan.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tareihan.viewmodel.user.userViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ListUserScreen(navController: NavController, userViewModel: userViewModel = koinViewModel()) {
    LaunchedEffect(Unit) {
        userViewModel.getuser()
    }

    // State untuk modal konfirmasi
    var showDeleteDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<Int?>(null) }
    var userDetailToDelete by remember { mutableStateOf<String?>(null) }
    val userList = userViewModel.userList
    val isLoading = userViewModel.isLoading
    val errorMessage = userViewModel.errorMessage

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                userToDelete = null
                userDetailToDelete = null
            },
            title = {
                Text(
                    text = "Konfirmasi Hapus",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Apakah Anda yakin ingin menghapus data dumas ini?",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    userToDelete?.let { detail ->
                        Text(
                            text = "Judul: $detail",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tindakan ini tidak dapat dibatalkan.",
                        fontSize = 12.sp,
                        color = Color.Red
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        userToDelete?.let { userViewModel.deleteUserKerja(it) }
                        showDeleteDialog = false
                        userToDelete = null
                        userDetailToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        userToDelete = null
                        userDetailToDelete = null
                    }
                ) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Show loading indicator
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF6200EE)
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
                        onClick = { userViewModel.getuser() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                    ) {
                        Text("Retry", color = Color.White)
                    }
                }
            }

            // Show user list
            if (!isLoading && errorMessage == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header
                    Text(
                        text = "Daftar User",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (userList.isEmpty()) {
                        // Empty state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tidak ada data user",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        // User list
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(userList) { user ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        // User Name
                                        Text(
                                            text = user.name ?: "Nama tidak tersedia",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // User Details
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "NIP: ${user.nip ?: "-"}",
                                                    fontSize = 14.sp,
                                                    color = Color.Gray
                                                )
                                                Text(
                                                    text = "Email: ${user.email ?: "-"}",
                                                    fontSize = 14.sp,
                                                    color = Color.Gray
                                                )
                                                Text(
                                                    text = "Jabatan: ${user.jabatan ?: "-"}",
                                                    fontSize = 14.sp,
                                                    color = Color.Gray
                                                )
                                                Text(
                                                    text = "Jenis Kelamin: ${user.jenis_kelamin ?: "-"}",
                                                    fontSize = 14.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Action buttons
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
//                                            Button(
//                                                onClick = {
//                                                    // Navigate to show/detail user
//                                                    navController.navigate("user_detail/${user.id}")
//                                                },
//                                                modifier = Modifier
//                                                    .weight(1f)
//                                                    .padding(end = 4.dp),
//                                                colors = ButtonDefaults.buttonColors(
//                                                    containerColor = Color(0xFF6200EE),
//                                                    contentColor = Color.White
//                                                ),
//                                                shape = RoundedCornerShape(8.dp)
//                                            ) {
//                                                Text(text = "Show", fontSize = 14.sp)
//                                            }

                                            Button(
                                                onClick = {
                                                    // Navigate to edit user
                                                    navController.navigate("edit_user/${user.id}")
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(horizontal = 4.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF4CAF50),
                                                    contentColor = Color.White
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(text = "Edit", fontSize = 14.sp)
                                            }

                                            Button(
                                                onClick = {
                                                    // Handle delete user
//                                                    user.id?.let { userId ->
//                                                        userViewModel.deleteUserKerja(userId)
//                                                    }
                                                    user.id?.let {
                                                        userToDelete = it
                                                        userDetailToDelete = user.name ?: "Tanpa Nama"
                                                        showDeleteDialog = true
                                                    }
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(start = 4.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFFFF0000),
                                                    contentColor = Color.White
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(text = "Delete", fontSize = 14.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListUserPreview() {
    ListUserScreen(navController = rememberNavController())
}