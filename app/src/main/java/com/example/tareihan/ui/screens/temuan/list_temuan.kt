package com.example.tareihan.ui.screens.temuan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tareihan.routes.routes
import com.example.tareihan.viewmodel.temuan.TemuanViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ListTemuanScreen(
    navController: NavController,
    viewModel: TemuanViewModel = koinViewModel()
) {
    val temuanList = viewModel.temuanList
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    // State untuk dialog konfirmasi delete
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<Any?>(null) }

    // Memanggil API saat pertama kali masuk ke screen
    LaunchedEffect(Unit) {
        viewModel.getTemuan()
    }

    // Dialog konfirmasi delete
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                itemToDelete = null
            },
            title = {
                Text(
                    text = "Konfirmasi Hapus",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Apakah Anda yakin ingin menghapus temuan ini? Tindakan ini tidak dapat dibatalkan.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        itemToDelete?.let { item ->
                            val id = when (item) {
                                is Any -> {
                                    // Asumsi item memiliki property id
                                    val idField = item.javaClass.getDeclaredField("id")
                                    idField.isAccessible = true
                                    idField.get(item)
                                }
                                else -> null
                            }
                            id?.let { viewModel.deleteTemuan(it.toString()) }
                        }
                        showDeleteDialog = false
                        itemToDelete = null
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
                        itemToDelete = null
                    }
                ) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    navController.navigate(routes.create_temuan)
                }
            ) {
                Text("Create Temuan")
            }
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                !errorMessage.isNullOrEmpty() -> {
                    Text("Error: $errorMessage", color = Color.Red)
                }
                temuanList.isEmpty() -> {
                    Text("Tidak ada data temuan.", fontWeight = FontWeight.Bold)
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(temuanList) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text =  "Detail : ${item.detail}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Tahun: ${item.tahun ?: "-"}")

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Button(
                                            onClick = {
                                                navController.navigate("edit_temuan/${item.id}")
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF6200EE),
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("Show", fontSize = 16.sp)
                                        }

                                        Button(
                                            onClick = {
                                                itemToDelete = item
                                                showDeleteDialog = true
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Red,
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("Delete", fontSize = 16.sp)
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
fun ListTemuanPreview() {
    ListTemuanScreen(rememberNavController())
}