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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tareihan.R
import com.example.tareihan.dto.surat_tugas.surat_tugas
import com.example.tareihan.viewmodel.surat_tugas.SuratTugasViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSuratTugasScreen(
    navController: NavController,
    viewModel: SuratTugasViewModel = koinViewModel(),
    id: Int
) {
    var nomorSurat by remember { mutableStateOf("") }
    var tanggalTerbit by remember { mutableStateOf("") }
    var jenis by remember { mutableStateOf("") }
    var perihal by remember { mutableStateOf("") }
    var tujuan by remember { mutableStateOf("") }
    var isInitialized by remember { mutableStateOf(false) }

    val surat = viewModel.suratTugasSelected
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val isSuccess = viewModel.successMessage

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Load data
    LaunchedEffect(Unit) {
        viewModel.getSuratTugasById(id)
    }

    // Set fields
    LaunchedEffect(surat?.id) {
        if (surat != null && !isInitialized) {
            nomorSurat = surat.nomor_surat.orEmpty()
            tanggalTerbit = surat.tanggal_terbit.orEmpty()
            jenis = surat.jenis.orEmpty()
            perihal = surat.perihal.orEmpty()
            tujuan = surat.tujuan.orEmpty()
            isInitialized = true
        }
    }

    // Success handler
    LaunchedEffect(isSuccess) {
        if (isSuccess != null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Berhasil memperbarui surat tugas")
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Surat Tugas", fontWeight = FontWeight.Bold) }
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
                "Silakan ubah data surat tugas di bawah ini.",
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
                    LabeledField("Nomor Surat", nomorSurat) { nomorSurat = it }
                    LabeledField("Tanggal Terbit", tanggalTerbit) { tanggalTerbit = it }
                    LabeledField("Jenis", jenis) { jenis = it }
                    LabeledField("Perihal", perihal) { perihal = it }
                    LabeledField("Tujuan", tujuan) { tujuan = it }
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    val updatedSurat = surat_tugas(
                        id = surat?.id,
                        laporan_dumas_id = "1", // Replace with actual if needed
                        nomor_surat = nomorSurat,
                        tanggal_terbit = tanggalTerbit,
                        jenis = jenis,
                        perihal = perihal,
                        tujuan = tujuan,
                        created_at = "1"
                    )
                    viewModel.updateSuratTugas(id, updatedSurat)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4CFA)),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Simpan Perubahan", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun LabeledField(label: String, value: String, onValueChange: (String) -> Unit) {
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
fun EditSuratTugasPreview() {
    EditSuratTugasScreen(navController = rememberNavController(), id = 1)
}
