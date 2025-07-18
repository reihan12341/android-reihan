package com.example.tareihan.ui.screens.disposisi_irbanda

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tareihan.dto.disposisi_irbanda.disposisi_irbanda
import com.example.tareihan.viewmodel.disposisi_irbanda.Disposisi_irbandaViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun Listdisposisi_irbandaScreen(
    navController: NavController,
    viewModel: Disposisi_irbandaViewModel = koinViewModel()
) {
    val disposisiIrbandaList = viewModel.disposisiIrbandaList
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val context = LocalContext.current

    // State untuk modal konfirmasi
    var showDeleteDialog by remember { mutableStateOf(false) }
    var disposisiToDelete by remember { mutableStateOf<Int?>(null) }
    var disposisiDetailToDelete by remember { mutableStateOf<String?>(null) }

    // Memanggil API saat pertama kali masuk ke screen
    LaunchedEffect(Unit) {
        viewModel.getAllDisposisiIrbanda()
    }

    // Modal konfirmasi delete
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                disposisiToDelete = null
                disposisiDetailToDelete = null
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
                        text = "Apakah Anda yakin ingin menghapus disposisi irbanda ini?",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    disposisiDetailToDelete?.let { detail ->
                        Text(
                            text = "Detail: $detail",
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
                        disposisiToDelete?.let { viewModel.deleteDisposisiIrbanda(it) }
                        showDeleteDialog = false
                        disposisiToDelete = null
                        disposisiDetailToDelete = null
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
                        disposisiToDelete = null
                        disposisiDetailToDelete = null
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
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                !errorMessage.isNullOrEmpty() -> {
                    Text("Error: $errorMessage", color = Color.Red)
                }
                disposisiIrbandaList.isEmpty() -> {
                    Text("Tidak ada data disposisi irbanda.", fontWeight = FontWeight.Bold)
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(disposisiIrbandaList) { item ->
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
                                        text =  "Detail : ${item.kepada}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Tahun: ${item.laporan_dumas_id ?: "-"}")

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Button(
                                            onClick = {
                                                navController.navigate("edit_disposisi_irbanda/${item.id}")
                                                Toast.makeText(context,"Click",Toast.LENGTH_SHORT).show()
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
                                                item.id?.let {
                                                    disposisiToDelete = it
                                                    disposisiDetailToDelete = item.kepada
                                                    showDeleteDialog = true
                                                }
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
    Listdisposisi_irbandaScreen(rememberNavController())
}