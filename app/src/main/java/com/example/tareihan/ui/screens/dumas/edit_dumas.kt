package com.example.tareihan.ui.screens.dumas

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tareihan.dto.dumas.dumas
import com.example.tareihan.viewmodel.dumas.DumasViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDumasScreen(
    navController: NavController,
    viewModel: DumasViewModel = koinViewModel(),
    id: Int = 0
) {
    var judul by remember { mutableStateOf("") }
    var isiPengaduan by remember { mutableStateOf("") }
    var namaPengadu by remember { mutableStateOf("") }
    var nomorHpPengadu by remember { mutableStateOf("") }
    var emailPengadu by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var isInitialized by remember { mutableStateOf(false) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var isSuccess = viewModel.isSuccess

    val dumas = viewModel.dumasShow
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Static status options
    val statusOptions = listOf("Terima", "Tidak Diterima")
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getDumas(id)
    }

    LaunchedEffect(dumas?.id) {
        if (dumas != null && !isInitialized) {
            judul = dumas.judul.orEmpty()
            isiPengaduan = dumas.isi_pengaduan.orEmpty()
            namaPengadu = dumas.nama_pengadu.orEmpty()
            nomorHpPengadu = dumas.nomorhp_pengadu.orEmpty()
            emailPengadu = dumas.email_pengadu.orEmpty()
            keterangan = dumas.keterangan.orEmpty()
            // Note: Status is not part of the dumas data class; handle separately if needed
            isInitialized = true
        }
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess && !showSuccessSnackbar) {
            showSuccessSnackbar = true
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Data berhasil diperbarui.")
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Dumas", fontWeight = FontWeight.Bold) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF2F4F7)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Silakan perbarui data pengaduan di bawah ini.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    SectionHeader("Informasi Pengadu")
                    LabeledField("Judul", judul) { judul = it }
                    LabeledField("Isi Pengaduan", isiPengaduan, 3) { isiPengaduan = it }
                    LabeledField("Nama Pengadu", namaPengadu) { namaPengadu = it }
                    LabeledField("Nomor HP", nomorHpPengadu) { nomorHpPengadu = it }
                    LabeledField("Email", emailPengadu) { emailPengadu = it }
                    LabeledField("Keterangan", keterangan, 2) { keterangan = it }

                    SectionHeader("Status")
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = { },
                            label = { Text("Status") },
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            statusOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        status = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    val updated = dumas?.id?.let {
                        dumas(
                            id = it,
                            judul = judul,
                            isi_pengaduan = isiPengaduan,
                            nama_pengadu = namaPengadu,
                            nomorhp_pengadu = nomorHpPengadu,
                            email_pengadu = emailPengadu,
                            keterangan = keterangan,
                            created_by = "1"
                        )
                    }
                    if (updated != null) {
                        viewModel.putDumas(id, updated)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4CFA))
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
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
fun LabeledField(
    label: String,
    value: String,
    maxLines: Int = 1,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        singleLine = maxLines == 1,
        maxLines = maxLines
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewEditDumasScreen() {
    EditDumasScreen(navController = rememberNavController())
}