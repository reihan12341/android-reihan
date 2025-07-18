package com.example.tareihan.ui.screens.unit_kerja

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tareihan.R
import com.example.tareihan.dto.unit_kerja.unit_kerja
import com.example.tareihan.viewmodel.unit_kerja.UnitKerjaViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateUnitKerjaScreen(
    navController: NavController,
    unitKerjaViewModel: UnitKerjaViewModel = koinViewModel()
) {
    var namaUnit by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Unit Kerja",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Fill the form to add a new unit kerja",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // id_namaUnit Input
            OutlinedTextField(
                value = namaUnit,
                onValueChange = { namaUnit = it },
                label = { Text("Nama Unit Kerja") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // id_keterangan Input
            OutlinedTextField(
                value = keterangan,
                onValueChange = { keterangan = it },
                label = { Text("Keterangan") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // id_createdBy Input
            OutlinedTextField(
                value = createdBy,
                onValueChange = { createdBy = it },
                label = { Text("Created By") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(
                onClick = {
                    if (namaUnit.isBlank() || keterangan.isBlank() || createdBy.isBlank()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please fill allfields!")
                        }
                    } else {
                        val unitKerjaRequest = unit_kerja(
                            id = "0",
                            nama_unit = namaUnit,
                            keterangan = keterangan,
                            created_by = createdBy
                        )
                        unitKerjaViewModel.postUnitKerja(unitKerjaRequest)

                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Unit Kerja created successfully!")
                        }

                        // Clear form
                        namaUnit = ""
                        keterangan = ""
                        createdBy = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4CFA)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Created Unit Kerja", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateUnitKerjaScreenPreview() {
    CreateUnitKerjaScreen(rememberNavController())
}
