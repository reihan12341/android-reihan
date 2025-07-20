package com.example.tareihan.ui.screens.dumas

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
fun CreateDumasScreen(
    navController: NavController,
    dumasViewModel: DumasViewModel = koinViewModel()
) {
    var judul by remember { mutableStateOf("") }
    var isiPengadu by remember { mutableStateOf("") }
    var namaPengadu by remember { mutableStateOf("") }
    var nomorHpPengadu by remember { mutableStateOf("") }
    var emailPengadu by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        selectedFileName = uri?.let {
            context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex)
            } ?: "Unknown file"
        } ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Form Pengaduan", fontWeight = FontWeight.Bold) }
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
                "Silakan isi form pengaduan di bawah ini dengan lengkap.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    SectionTitle("Informasi Pengadu")
                    LabeledTextField("Judul", judul) { judul = it }
                    LabeledTextField("Isi Pengaduan", isiPengadu, maxLines = 3) { isiPengadu = it }
                    LabeledTextField("Nama Pengadu", namaPengadu) { namaPengadu = it }
                    LabeledTextField("Nomor HP", nomorHpPengadu) { nomorHpPengadu = it }
                    LabeledTextField("Email", emailPengadu) { emailPengadu = it }
                    LabeledTextField("Keterangan Tambahan", keterangan, maxLines = 2) { keterangan = it }

                    SectionTitle("Upload File")
                    Button(
                        onClick = { filePickerLauncher.launch("*/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4CFA))
                    ) {
                        Text("Pilih File", color = Color.White)
                    }
                    if (selectedFileName.isNotEmpty()) {
                        Text(
                            text = "File terpilih: $selectedFileName",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()

                    val fields = listOf(
                        judul, isiPengadu, namaPengadu, nomorHpPengadu, emailPengadu, keterangan
                    )

                    if (fields.any { it.isBlank() }) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Semua field wajib diisi!")
                        }
                        return@Button
                    }

                    if (selectedFileUri == null) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Silakan pilih file sebelum mengirim!")
                        }
                        return@Button
                    }

                    val request = dumas(
                        id = 0,
                        judul = judul,
                        isi_pengaduan = isiPengadu,
                        nama_pengadu = namaPengadu,
                        nomorhp_pengadu = nomorHpPengadu,
                        email_pengadu = emailPengadu,
                        keterangan = keterangan,
                        created_by = "1"
                    )

                    coroutineScope.launch {
                        dumasViewModel.postDumas(request, selectedFileUri)
                        if (dumasViewModel.isSuccess) {
                            snackbarHostState.showSnackbar("Pengaduan dan file berhasil dikirim.")
                            // Reset fields
                            judul = ""
                            isiPengadu = ""
                            namaPengadu = ""
                            nomorHpPengadu = ""
                            emailPengadu = ""
                            keterangan = ""
                            selectedFileUri = null
                            selectedFileName = ""
                        } else {
                            snackbarHostState.showSnackbar(
                                dumasViewModel.errorMessage ?: "Gagal mengirim pengaduan."
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4CFA))
            ) {
                Text("Kirim Pengaduan", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
fun LabeledTextField(
    label: String,
    value: String,
    maxLines: Int = 1,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = maxLines == 1,
        maxLines = maxLines,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateDumasScreen() {
    CreateDumasScreen(rememberNavController())
}