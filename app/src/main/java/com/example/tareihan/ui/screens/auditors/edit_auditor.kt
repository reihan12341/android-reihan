package com.example.tareihan.ui.screens.auditors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tareihan.dto.auditor.Auditor
import com.example.tareihan.viewmodel.auditor.AuditorViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAuditorScreen(
    navController: NavController,
    viewModel: AuditorViewModel = koinViewModel(),
    id: Int = 0
) {
    var idDumas by remember { mutableStateOf("") }
    var idUser by remember { mutableStateOf("") }
    var idUnitKerja by remember { mutableStateOf("") }
    var isInitialized by remember { mutableStateOf(false) }

    val auditor = viewModel.auditorShow
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val isUpdateSuccess = viewModel.isUpdateSuccess
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) { viewModel.getAuditorById(id) }

    LaunchedEffect(auditor?.id) {
        if (auditor != null && !isInitialized) {
            idDumas = auditor.id_dumas.orEmpty()
            idUser = auditor.id_user.orEmpty()
            idUnitKerja = auditor.id_unit_kerja.orEmpty()
            isInitialized = true
        }
    }

    LaunchedEffect(isUpdateSuccess) {
        if (isUpdateSuccess) navController.popBackStack()
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Auditor", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF9F9FC)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Formulir Data Auditor",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = "Silakan ubah informasi auditor di bawah ini.",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = idDumas,
                        onValueChange = { idDumas = it },
                        label = { Text("ID Dumas") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = idUser,
                        onValueChange = { idUser = it },
                        label = { Text("ID User") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = idUnitKerja,
                        onValueChange = { idUnitKerja = it },
                        label = { Text("ID Unit Kerja") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    val updatedAuditor = Auditor(
                        id = id,
                        id_dumas = idDumas,
                        id_user = idUser,
                        id_unit_kerja = idUnitKerja
                    )
                    viewModel.putAuditor(id, updatedAuditor)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
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

@Preview(showBackground = true)
@Composable
fun EditAuditorPreview() {
    EditAuditorScreen(navController = rememberNavController())
}
