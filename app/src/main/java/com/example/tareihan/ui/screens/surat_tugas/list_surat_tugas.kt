package com.example.tareihan.ui.screens.surat_tugas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.tareihan.routes.routes
import com.example.tareihan.viewmodel.surat_tugas.SuratTugasViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun list_surat_tugas(
    navController: NavController,
    viewModel: SuratTugasViewModel = koinViewModel()
) {
    val suratList = viewModel.suratTugasList
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    LaunchedEffect(Unit) {
        viewModel.getSuratTugasList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Surat Tugas", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = Color(0xFFF8F9FC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        navController.navigate(routes.create_surat_tugas)
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Tambah Surat Tugas")
                }
            }

            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                }
                errorMessage != null -> {
                    Text(
                        "Terjadi kesalahan: $errorMessage",
                        color = Color.Red,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                suratList.isEmpty() -> {
                    Text(
                        "Tidak ada data surat tugas ditemukan.",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(suratList.size) { index ->
                            val surat = suratList[index]

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(6.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = surat.nomor_surat ?: "-",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Tanggal Terbit: ${surat.tanggal_terbit ?: "-"}", fontSize = 14.sp)
                                    Text("Jenis: ${surat.jenis ?: "-"}", fontSize = 14.sp)
                                    Text("Perihal: ${surat.perihal ?: "-"}", fontSize = 14.sp)
                                    Text("Tujuan: ${surat.tujuan ?: "-"}", fontSize = 14.sp)

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Button(
                                            onClick = {
                                                navController.navigate("edit_surat_tugas/${surat.id}")
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF4C73FF),
                                                contentColor = Color.White
                                            )
                                        ) {
                                            Text("Lihat", fontSize = 14.sp)
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Button(
                                            onClick = {
                                                surat.id?.let { viewModel.deleteSuratTugas(it) }
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFFF4C4C),
                                                contentColor = Color.White
                                            )
                                        ) {
                                            Text("Hapus", fontSize = 14.sp)
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
fun list_surat_tugas_preview() {
    list_surat_tugas(rememberNavController())
}
