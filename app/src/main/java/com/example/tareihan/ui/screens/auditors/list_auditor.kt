package com.example.tareihan.ui.screens.auditors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tareihan.routes.routes
import com.example.tareihan.viewmodel.auditor.AuditorViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun list_auditor(
    navController: NavController,
    viewModel: AuditorViewModel = koinViewModel()
) {
    val auditorList = viewModel.auditorList
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    // State untuk modal konfirmasi
    var showDeleteDialog by remember { mutableStateOf(false) }
    var auditorToDelete by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getAuditor()
    }

    // Modal konfirmasi delete
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                auditorToDelete = null
            },
            title = {
                Text(
                    text = "Konfirmasi Hapus",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus data auditor ini? Tindakan ini tidak dapat dibatalkan.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        auditorToDelete?.let { viewModel.deleteAuditor(it) }
                        showDeleteDialog = false
                        auditorToDelete = null
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
                        auditorToDelete = null
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
            Row (
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.End
            ){
                Button(
                    onClick = {
                        navController.navigate(routes.create_auditor)
                    }
                ) {
                    Text("Create Auditor")
                }
            }
            if (isLoading) {
                Text("Memuat data...", fontSize = 16.sp)
            } else if (errorMessage != null) {
                Text("Terjadi kesalahan: $errorMessage", color = Color.Red)
            } else if (auditorList.isEmpty()) {
                Text("Tidak ada data auditor ditemukan.")
            } else {
                LazyColumn {
                    items(auditorList.size) { index ->
                        val auditor = auditorList[index]

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Dumas: ${auditor.dumas}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("User: ${auditor.user}", fontSize = 14.sp)
                                Text("Unit Kerja: ${auditor.unitKerja}", fontSize = 14.sp)

                                Spacer(modifier = Modifier.height(12.dp))

                                Row {
                                    Button(
                                        onClick = {
                                            navController.navigate("edit_auditor/${auditor.id}")
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
                                            auditor.id?.let {
                                                auditorToDelete = it
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


@Preview
@Composable
fun list_auditor_preview() {
    list_auditor(rememberNavController())
}