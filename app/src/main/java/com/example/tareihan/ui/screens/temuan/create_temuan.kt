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
import com.example.tareihan.viewmodel.auditor.AuditorViewModel
import com.example.tareihan.viewmodel.temuan.TemuanViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTemuanScreen(
    navController: NavController,
    temuanViewModel: TemuanViewModel = koinViewModel(),
    auditorViewModel: AuditorViewModel = koinViewModel()
) {
    // Form states
    var idAuditor by remember { mutableStateOf("") }
    var selectedAuditorName by remember { mutableStateOf("") }
    var totalAnggaran by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf("") }
    var tahun by remember { mutableStateOf("") }
    var createdBy by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // UI states
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val auditor = auditorViewModel.auditorList

    // Fetch auditors on first load
    LaunchedEffect(Unit) {
        auditorViewModel.getAuditor()
    }

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
            // Header Section
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

            // Auditor Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedAuditorName.ifEmpty { "Pilih Auditor" },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Auditor") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.exposedDropdownSize()
                ) {
                    auditor.forEach { auditorItem ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = auditorItem.user ?: "Unknown User",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            onClick = {
                                idAuditor = auditorItem.id.toString()
                                selectedAuditorName = auditorItem.user ?: ""
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total Anggaran Field
            OutlinedTextField(
                value = totalAnggaran,
                onValueChange = { totalAnggaran = it },
                label = { Text("Total Anggaran") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Detail Field
            OutlinedTextField(
                value = detail,
                onValueChange = { detail = it },
                label = { Text("Detail Temuan") },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tahun Field
            OutlinedTextField(
                value = tahun,
                onValueChange = { tahun = it },
                label = { Text("Tahun") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(
                onClick = {
                    if (idAuditor.isBlank() || totalAnggaran.isBlank() ||
                        detail.isBlank() || tahun.isBlank()) {
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
                            // Clear form after successful submission
                            idAuditor = ""
                            selectedAuditorName = ""
                            totalAnggaran = ""
                            detail = ""
                            tahun = ""
                            createdBy = ""
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6A4CFA),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Simpan Temuan",
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