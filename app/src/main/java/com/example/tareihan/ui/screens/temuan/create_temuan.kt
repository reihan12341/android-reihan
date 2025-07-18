package com.example.tareihan.ui.screens.temuan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tareihan.R
import com.example.tareihan.dto.temuan.temuan
import com.example.tareihan.viewmodel.temuan.TemuanViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateTemuanScreen(
    navController: NavController,
    temuanViewModel: TemuanViewModel = koinViewModel()
) {
    var idAuditor by remember { mutableStateOf("") }
    var totalAnggaran by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf("") }
    var tahun by remember { mutableStateOf("") }
    var createdBy by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Tambah Temuan",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Lengkapi form berikut untuk menambahkan data Temuan baru.",
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            OutlinedTextField(
                value = idAuditor,
                onValueChange = { idAuditor = it },
                label = { Text("ID Auditor") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = totalAnggaran,
                onValueChange = { totalAnggaran = it },
                label = { Text("Total Anggaran") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Filled.AccountBox, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = detail,
                onValueChange = { detail = it },
                label = { Text("Detail Temuan") },
                leadingIcon = { Icon(Icons.Filled.Info, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = tahun,
                onValueChange = { tahun = it },
                label = { Text("Tahun") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Filled.Call, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = createdBy,
                onValueChange = { createdBy = it },
                label = { Text("Created By") },
                leadingIcon = { Icon(Icons.Filled.AccountCircle, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (idAuditor.isBlank() || totalAnggaran.isBlank() || detail.isBlank() || tahun.isBlank() || createdBy.isBlank()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Mohon lengkapi semua field!")
                        }
                    } else {
                        val temuanRequest = temuan(
                            id = 0,
                            id_auditor = idAuditor,
                            total_anggaran = totalAnggaran,
                            detail = detail,
                            tahun = tahun,
                            created_by = createdBy
                        )
                        temuanViewModel.postTemuan(temuanRequest)

                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Temuan berhasil ditambahkan!")
                        }

                        idAuditor = ""
                        totalAnggaran = ""
                        detail = ""
                        tahun = ""
                        createdBy = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4CFA)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Simpan Temuan",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateTemuanScreenPreview() {
    CreateTemuanScreen(rememberNavController())
}
