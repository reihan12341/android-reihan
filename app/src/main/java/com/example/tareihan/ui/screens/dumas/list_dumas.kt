package com.example.tareihan.ui.screens.dumas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.tareihan.routes.routes
import com.example.tareihan.viewmodel.dumas.DumasViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun list_dumas(
    navController: NavController,
    viewModel: DumasViewModel = koinViewModel()
) {
    val dumasList = viewModel.dumasList
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    // State untuk modal konfirmasi
    var showDeleteDialog by remember { mutableStateOf(false) }
    var dumasToDelete by remember { mutableStateOf<Int?>(null) }
    var dumasDetailToDelete by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getDumas()
    }

    // Modal konfirmasi delete
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                dumasToDelete = null
                dumasDetailToDelete = null
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
                    dumasDetailToDelete?.let { detail ->
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
                        dumasToDelete?.let { viewModel.deleteDumas(it) }
                        showDeleteDialog = false
                        dumasToDelete = null
                        dumasDetailToDelete = null
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
                        dumasToDelete = null
                        dumasDetailToDelete = null
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
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = {
                    navController.navigate(routes.create_dumas_route)
                }) {
                    Text("Create Dumas")
                }
            }

            when {
                isLoading -> {
                    Text("Memuat data...", fontSize = 16.sp)
                }
                errorMessage != null -> {
                    Text("Terjadi kesalahan: $errorMessage", color = Color.Red)
                }
                dumasList.isEmpty() -> {
                    Text("Tidak ada data dumas ditemukan.")
                }
                else -> {
                    LazyColumn {
                        items(dumasList.size) { index ->
                            val dumas = dumasList[index]

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {

                                    // ✅ Tampilkan gambar dari URL (jika tersedia)
                                    dumas.attachments?.let { url ->
                                        if (url.isEmpty()) {
                                            AsyncImage(
                                                model = url,
                                                contentDescription = "Foto Dumas",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(180.dp),
                                                contentScale = ContentScale.Crop
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }

                                    Text("Judul: ${dumas.judul ?: "-"}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("Pengadu: ${dumas.nama_pengadu ?: "-"}", fontSize = 14.sp)
                                    Text("No HP: ${dumas.nomorhp_pengadu ?: "-"}", fontSize = 14.sp)

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row {
                                        Button(
                                            onClick = {
                                                navController.navigate("edit_dumas/${dumas.id}")
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF6200EE),
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("Show", fontSize = 14.sp)
                                        }

                                        Button(
                                            onClick = {
                                                dumas.id?.let {
                                                    dumasToDelete = it
                                                    dumasDetailToDelete = dumas.judul ?: "Tanpa judul"
                                                    showDeleteDialog = true
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFFF0000),
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("Delete", fontSize = 14.sp)
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

@Preview
@Composable
fun list_dumas_preview() {
    list_dumas(rememberNavController())
}
