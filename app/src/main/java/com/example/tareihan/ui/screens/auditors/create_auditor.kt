package com.example.tareihan.ui.screens.auditors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Person
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
import com.example.tareihan.dto.auditor.Auditor
import com.example.tareihan.dto.dumas.dumas
import com.example.tareihan.viewmodel.auditor.AuditorViewModel
import com.example.tareihan.viewmodel.dumas.DumasViewModel
import com.example.tareihan.viewmodel.unit_kerja.UnitKerjaViewModel
import com.example.tareihan.viewmodel.user.userViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAuditorScreen(
    navController: NavController,
    auditorViewModel: AuditorViewModel = koinViewModel(),
    dumasViewModel: DumasViewModel = koinViewModel(),
    unitKerjaViewModel: UnitKerjaViewModel = koinViewModel(),
    userViewModel: userViewModel = koinViewModel()
) {
    val dumasList = dumasViewModel.dumasList
    val unitkerjalist by unitKerjaViewModel.unitKerjaList.collectAsState()
    val userList = userViewModel.userList

    var selectedDumas by remember { mutableStateOf<dumas?>(null) }
    // Perbaikan: Gunakan tipe data yang spesifik sesuai dengan DTO
    var selectedUser by remember { mutableStateOf<Any?>(null) } // User object
    var selectedUnitKerja by remember { mutableStateOf<Any?>(null) } // UnitKerja object

    var expandedDumas by remember { mutableStateOf(false) }
    var expandedUser by remember { mutableStateOf(false) }
    var expandedUnitKerja by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        dumasViewModel.getDumas()
        unitKerjaViewModel.getUnitKerja()
        userViewModel.getuser()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F6FA)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Icon(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(72.dp)
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = "Auditor",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                Text(
                    text = "Isi form di bawah untuk menambahkan auditor baru",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Dropdown Dumas
                        ExposedDropdownMenuBox(
                            expanded = expandedDumas,
                            onExpandedChange = { expandedDumas = !expandedDumas },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedDumas?.let { "${it.id} - ${it.judul}" } ?: "",
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Pilih Dumas") },
                                leadingIcon = {
                                    Icon(Icons.Default.Create, contentDescription = null)
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDumas)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = expandedDumas,
                                onDismissRequest = { expandedDumas = false }
                            ) {
                                dumasList.forEach { dumas ->
                                    DropdownMenuItem(
                                        text = {
                                            Text("${dumas.id} - ${dumas.judul}")
                                        },
                                        onClick = {
                                            selectedDumas = dumas
                                            expandedDumas = false
                                        }
                                    )
                                }
                            }
                        }

                        // Dropdown User - PERBAIKAN
                        ExposedDropdownMenuBox(
                            expanded = expandedUser,
                            onExpandedChange = { expandedUser = !expandedUser },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedUser?.let { user ->
                                    // Akses properti menggunakan reflection atau casting
                                    try {
                                        val id = user::class.java.getDeclaredField("id").apply { isAccessible = true }.get(user)
                                        val name = try {
                                            user::class.java.getDeclaredField("name").apply { isAccessible = true }.get(user)
                                        } catch (e: Exception) {
                                            user::class.java.getDeclaredField("username").apply { isAccessible = true }.get(user)
                                        }
                                        "$id - $name"
                                    } catch (e: Exception) {
                                        user.toString()
                                    }
                                } ?: "",
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Pilih User") },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null)
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUser)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = expandedUser,
                                onDismissRequest = { expandedUser = false }
                            ) {
                                userList.forEach { user ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                try {
                                                    val id = user::class.java.getDeclaredField("id").apply { isAccessible = true }.get(user)
                                                    val name = try {
                                                        user::class.java.getDeclaredField("name").apply { isAccessible = true }.get(user)
                                                    } catch (e: Exception) {
                                                        user::class.java.getDeclaredField("username").apply { isAccessible = true }.get(user)
                                                    }
                                                    "$id - $name"
                                                } catch (e: Exception) {
                                                    user.toString()
                                                }
                                            )
                                        },
                                        onClick = {
                                            selectedUser = user
                                            expandedUser = false
                                        }
                                    )
                                }
                            }
                        }

                        // Dropdown Unit Kerja
                        ExposedDropdownMenuBox(
                            expanded = expandedUnitKerja,
                            onExpandedChange = { expandedUnitKerja = !expandedUnitKerja },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedUnitKerja?.let { unitKerja ->
                                    try {
                                        val id = unitKerja::class.java.getDeclaredField("id").apply { isAccessible = true }.get(unitKerja)
                                        val nama = try {
                                            unitKerja::class.java.getDeclaredField("nama_unit").apply { isAccessible = true }.get(unitKerja)
                                        } catch (e: Exception) {
                                            unitKerja::class.java.getDeclaredField("nama").apply { isAccessible = true }.get(unitKerja)
                                        }
                                        "$id - $nama"
                                    } catch (e: Exception) {
                                        unitKerja.toString()
                                    }
                                } ?: "",
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Pilih Unit Kerja") },
                                leadingIcon = {
                                    Icon(Icons.Default.Create, contentDescription = null)
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnitKerja)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = expandedUnitKerja,
                                onDismissRequest = { expandedUnitKerja = false }
                            ) {
                                unitkerjalist.forEach { unitKerja ->
                                    DropdownMenuItem(
                                        text = {
                                            Text("${unitKerja.id} - ${unitKerja.nama_unit}")
                                        },
                                        onClick = {
                                            selectedUnitKerja = unitKerja
                                            expandedUnitKerja = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (selectedDumas == null || selectedUser == null || selectedUnitKerja == null) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Mohon pilih semua opsi.")
                            }
                        } else {
                            // Perbaikan: Ekstrak ID dengan reflection
                            val userId = try {
                                selectedUser?.let { user ->
                                    user::class.java.getDeclaredField("id").apply { isAccessible = true }.get(user).toString()
                                } ?: ""
                            } catch (e: Exception) {
                                ""
                            }

                            val unitKerjaId = try {
                                selectedUnitKerja?.let { unitKerja ->
                                    unitKerja::class.java.getDeclaredField("id").apply { isAccessible = true }.get(unitKerja).toString()
                                } ?: ""
                            } catch (e: Exception) {
                                ""
                            }

                            val auditorRequest = Auditor(
                                id = null,
                                id_dumas = selectedDumas?.id?.toString() ?: "",
                                id_user = userId,
                                id_unit_kerja = unitKerjaId
                            )
                            auditorViewModel.postAuditor(auditorRequest)

                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Auditor berhasil ditambahkan!")
                            }

                            selectedDumas = null
                            selectedUser = null
                            selectedUnitKerja = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4CFA)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Simpan", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateAuditorScreenPreview() {
    CreateAuditorScreen(rememberNavController())
}