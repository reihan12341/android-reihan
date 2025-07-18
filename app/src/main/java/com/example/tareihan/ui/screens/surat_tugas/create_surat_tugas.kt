package com.example.tareihan.ui.screens.surat_tugas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.tareihan.dto.surat_tugas.surat_tugas
import com.example.tareihan.viewmodel.surat_tugas.SuratTugasViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSuratTugasScreen(
    navController: NavController,
    viewModel: SuratTugasViewModel = koinViewModel()
) {
    var nomorSurat by remember { mutableStateOf("") }
    var tanggalTerbit by remember { mutableStateOf("") }
    var jenis by remember { mutableStateOf("") }
    var perihal by remember { mutableStateOf("") }
    var tujuan by remember { mutableStateOf("") }
    var createdBy by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Surat Tugas", fontWeight = FontWeight.Bold) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF8F9FC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Silakan isi form surat tugas di bawah ini.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    LabeledFieldSuratTugas("Nomor Surat", nomorSurat) { nomorSurat = it }
                    LabeledFieldSuratTugas("Tanggal Terbit (yyyy-mm-dd)", tanggalTerbit) { tanggalTerbit = it }
                    LabeledFieldSuratTugas("Jenis", jenis) { jenis = it }
                    LabeledFieldSuratTugas("Perihal", perihal) { perihal = it }
                    LabeledFieldSuratTugas("Tujuan", tujuan) { tujuan = it }
                    LabeledFieldSuratTugas("Created By", createdBy) { createdBy = it }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (nomorSurat.isBlank() || tanggalTerbit.isBlank() || jenis.isBlank() ||
                        perihal.isBlank() || tujuan.isBlank() || createdBy.isBlank()
                    ) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Semua field wajib diisi!")
                        }
                    } else {
                        val suratTugas = surat_tugas(
//                            id = 0,
                            laporan_dumas_id = "1", // Gantilah dengan ID yang sesuai jika dinamis
                            nomor_surat = nomorSurat,
                            tanggal_terbit = tanggalTerbit,
                            jenis = jenis,
                            perihal = perihal,
                            tujuan = tujuan,
                            created_at = createdBy
                        )

                        viewModel.postSuratTugas(suratTugas)

                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Surat Tugas berhasil dibuat")
                        }

                        // Reset form
                        nomorSurat = ""
                        tanggalTerbit = ""
                        jenis = ""
                        perihal = ""
                        tujuan = ""
                        createdBy = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4CFA)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Simpan", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun LabeledFieldSuratTugas(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun CreateSuratTugasScreenPreview() {
    CreateSuratTugasScreen(rememberNavController())
}
