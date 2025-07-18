package com.example.tareihan.ui.screens.temuan

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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tareihan.R
import com.example.tareihan.dto.temuan.temuan
import com.example.tareihan.viewmodel.temuan.TemuanViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditTemuanScreen(
    navController: NavController,
    viewModel: TemuanViewModel = koinViewModel(),
    id: Int
) {
    // Load data saat pertama kali tampil
    LaunchedEffect(id) {
        viewModel.getTemuanById(id)
    }

    val temuan = viewModel.temuanShow
    var idAuditor by remember { mutableStateOf("") }
    var totalAnggaran by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf("") }
    var tahun by remember { mutableStateOf("") }
    var updatedBy by remember { mutableStateOf("1") }

    LaunchedEffect(temuan) {
        temuan?.let {
            idAuditor = it.id_auditor.toString()
            totalAnggaran = it.total_anggaran ?: ""
            detail = it.detail ?: ""
            tahun = it.tahun ?: ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Icon(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            tint = Color.Unspecified,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Edit Data Temuan",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Field Input
        OutlinedTextField(
            value = idAuditor,
            onValueChange = { idAuditor = it },
            label = { Text("ID Auditor") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = totalAnggaran,
            onValueChange = { totalAnggaran = it },
            label = { Text("Total Anggaran") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = detail,
            onValueChange = { detail = it },
            label = { Text("Detail") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tahun,
            onValueChange = { tahun = it },
            label = { Text("Tahun") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = updatedBy,
            onValueChange = { updatedBy = it },
            label = { Text("Updated By") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val temuan = temuan(
                    id_auditor = (idAuditor.toIntOrNull() ?: 0).toString(),
                    total_anggaran = totalAnggaran,
                    detail = detail,
                    tahun = tahun,
                )
                viewModel.putTemuan(
                    id = id,
                    temuan
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4CFA)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Update", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.isLoading) {
            CircularProgressIndicator()
        }

        if (viewModel.errorMessage?.isNotEmpty() == true) {
            viewModel.errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        if (viewModel.isUpdateSuccess) {
            Text(
                text = "Data berhasil diperbarui!",
                color = Color.Green,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Delay untuk kembali ke halaman sebelumnya
            LaunchedEffect(Unit) {
                delay(1000)
                navController.popBackStack()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditTemuanPreview() {
    EditTemuanScreen(rememberNavController(), id = 1)
}
