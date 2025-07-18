package com.example.tareihan.ui.screens.unit_kerja

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tareihan.dto.unit_kerja.unit_kerja
import com.example.tareihan.viewmodel.unit_kerja.UnitKerjaViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUnitKerjaScreen(
    unitKerjaId: String,
    navController: NavController,
    viewModel: UnitKerjaViewModel = koinViewModel()
) {
    val context = LocalContext.current

    val unitKerjaState by viewModel.unitKerja.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val updateSuccess by viewModel.updateSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Form states
    var namaUnit by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }
    var updatedBy by remember { mutableStateOf("1") }

    // Fetch data once
    LaunchedEffect(key1 = unitKerjaId) {
        viewModel.getUnitKerjaById(unitKerjaId)
    }

    // Populate form once data is loaded
    LaunchedEffect(key1 = unitKerjaState) {
        unitKerjaState?.let {
            namaUnit = it.nama_unit ?: ""
            keterangan = it.keterangan ?: ""
        }
    }

    // Handle update success
    LaunchedEffect(key1 = updateSuccess) {
        if (updateSuccess) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Edit Unit Kerja") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = namaUnit,
                onValueChange = { namaUnit = it },
                label = { Text("Nama Unit") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = keterangan,
                onValueChange = { keterangan = it },
                label = { Text("Keterangan") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val unitKerjaToUpdate = unit_kerja(
                        id = unitKerjaId,
                        nama_unit = namaUnit,
                        keterangan = keterangan,
                    )
                    viewModel.putUnitKerja(unitKerjaId, unitKerjaToUpdate)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading && namaUnit.isNotBlank()
            ) {
                Text(text = if (isLoading) "Menyimpan..." else "Simpan", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error message display
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditUnitKerjaPreview() {
    EditUnitKerjaScreen(
        unitKerjaId = "1",
        navController = rememberNavController()
    )
}