package com.example.tareihan.ui.screens.dumas

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tareihan.routes.routes
import com.example.tareihan.viewmodel.dumas.DumasViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import com.example.tareihan.di.env
import com.example.tareihan.dto.datastore.NewDataStoreManager
import com.example.tareihan.dto.dumas.dumas
import com.example.tareihan.dto.dumas.update_audit_disposisi
import com.example.tareihan.dto.dumas.update_disposisi_request
import com.example.tareihan.viewmodel.authviewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDumasScreen(
    navController: NavController,
    viewModel: DumasViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    // State Management
    val dumasList = viewModel.dumasList
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val isSuccess = viewModel.isSuccess
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Dialog States
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showVerifyDialog by remember { mutableStateOf(false) }
    var showDisposisiDialog by remember { mutableStateOf(false) }
    var showAuditDialog by remember { mutableStateOf(false) }

    // Selected Dumas
    var selectedDumasId by remember { mutableStateOf<Int?>(null) }
    var selectedDumasTitle by remember { mutableStateOf<String?>(null) }

    // Form States
    var disposisiTarget by remember { mutableStateOf("") }
    var disposisiNotes by remember { mutableStateOf("") }
    var disposisiBy by remember { mutableStateOf("") }
    var auditTo by remember { mutableStateOf("") }
    var auditNotes by remember { mutableStateOf("") }
    var auditDate by remember { mutableStateOf("") }
    var nilaiAudit by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Baru") }

    // User Data
    val context = LocalContext.current
    val dsm = remember { NewDataStoreManager.getInstance(context) }
    val userName = dsm.nameFlow.collectAsState(initial = "")
    val userJabatan = dsm.jabatanFlow.collectAsState(initial = "")

    // Status Options
    val statusOptions = listOf("Baru", "Terverifikasi", "Disposisi", "Diproses", "Selesai", "Ditolak")

    // Load Data
    LaunchedEffect(Unit) {
        viewModel.getDumas()
    }

    // Handle API Responses
    LaunchedEffect(isSuccess, errorMessage) {
        if (isSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = errorMessage ?: "Operasi berhasil!",
                    duration = SnackbarDuration.Short
                )
            }
            viewModel.resetSuccessState()
        } else if (errorMessage != null) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    duration = SnackbarDuration.Long
                )
            }
            viewModel.resetSuccessState()
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Apakah Anda yakin ingin menghapus data ini?")
                    Spacer(Modifier.height(8.dp))
                    selectedDumasTitle?.let {
                        Text("Judul: $it", fontWeight = FontWeight.Medium)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDumasId?.let { viewModel.deleteDumas(it) }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) { Text("Batal") }
            }
        )
    }

    // Verification Dialog
    if (showVerifyDialog) {
        AlertDialog(
            onDismissRequest = { showVerifyDialog = false },
            title = { Text("Ubah Status", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    statusOptions.forEach { status ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedStatus == status,
                                onClick = { selectedStatus = status }
                            )
                            Text(status, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedDumasId?.let {
                            viewModel.update_status_dumas(it, selectedStatus)
                            scope.launch {
                                snackbarHostState.showSnackbar("Status berhasil diubah")
                            }
                        }
                        showVerifyDialog = false
                    }
                ) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showVerifyDialog = false }
                ) { Text("Batal") }
            }
        )
    }

    // Disposisi Dialog
    if (showDisposisiDialog) {
        LaunchedEffect(showDisposisiDialog) {
            if (showDisposisiDialog) {
                disposisiBy = userName.value ?: ""
            }
        }

        AlertDialog(
            onDismissRequest = {
                showDisposisiDialog = false
                disposisiTarget = ""
                disposisiNotes = ""
            },
            title = { Text("Form Disposisi", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = disposisiBy,
                        onValueChange = { disposisiBy = it },
                        label = { Text("Disposisi Oleh") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = disposisiTarget,
                        onValueChange = { disposisiTarget = it },
                        label = { Text("Tujuan Disposisi") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = disposisiNotes,
                        onValueChange = { disposisiNotes = it },
                        label = { Text("Catatan") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedDumasId?.let { id ->
                            val request = update_disposisi_request(
                                disposisi_by = disposisiBy,
                                disposisi_to = disposisiTarget,
                                catatan = disposisiNotes
                            )
                            viewModel.update_disposisi(id, request)
                            viewModel.update_status_dumas(id, "Disposisi")
                            scope.launch {
                                snackbarHostState.showSnackbar("Disposisi berhasil dikirim")
                            }
                        }
                        showDisposisiDialog = false
                    },
                    enabled = disposisiTarget.isNotEmpty() && disposisiNotes.isNotEmpty()
                ) { Text("Kirim") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDisposisiDialog = false }
                ) { Text("Batal") }
            }
        )
    }

    // Audit Dialog
    if (showAuditDialog) {
        AlertDialog(
            onDismissRequest = {
                showAuditDialog = false
                auditTo = ""
                auditNotes = ""
                auditDate = ""
                nilaiAudit = ""
            },
            title = { Text("Form Audit", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = auditDate,
                        onValueChange = { auditDate = it },
                        label = { Text("Tanggal Audit") },
                        placeholder = { Text("DD/MM/YYYY") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = auditTo,
                        onValueChange = { auditTo = it },
                        label = { Text("Kepada") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nilaiAudit,
                        onValueChange = { nilaiAudit = it },
                        label = { Text("Nilai Audit (1-100)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = auditNotes,
                        onValueChange = { auditNotes = it },
                        label = { Text("Catatan") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedDumasId?.let { id ->
                            val request = update_audit_disposisi(
                                tanggal_audit = auditDate,
                                nilai_audit = nilaiAudit,
                                kepada = auditTo,
                                catatan = auditNotes
                            )
                            viewModel.update_disposisi_audit(id, request)
                            scope.launch {
                                snackbarHostState.showSnackbar("Audit berhasil disimpan")
                            }
                        }
                        showAuditDialog = false
                    },
                    enabled = auditDate.isNotEmpty() && auditTo.isNotEmpty() && nilaiAudit.isNotEmpty()
                ) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAuditDialog = false }
                ) { Text("Batal") }
            }
        )
    }

    // Main UI
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Data Dumas", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { viewModel.getDumas() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(routes.create_dumas_route) },
                icon = { Icon(Icons.Default.Add, "Tambah Dumas") },
                text = { Text("Buat Dumas") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
        ) {
            when {
                isLoading -> LoadingState()
                errorMessage != null -> ErrorState(errorMessage) { viewModel.getDumas() }
                dumasList.isEmpty() -> EmptyState { navController.navigate(routes.create_dumas_route) }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(dumasList) { dumas ->
                        DumasItemCard(
                            dumas = dumas,
                            status = dumas.status ?: "Baru",
                            onEdit = { navController.navigate("edit_dumas/${dumas.id}") },
                            onVerify = {
                                selectedDumasId = dumas.id
                                selectedDumasTitle = dumas.judul
                                selectedStatus = dumas.status ?: "Baru"
                                showVerifyDialog = true
                            },
                            onDelete = {
                                selectedDumasId = dumas.id
                                selectedDumasTitle = dumas.judul
                                showDeleteDialog = true
                            },
                            onDisposisi = {
                                selectedDumasId = dumas.id
                                selectedDumasTitle = dumas.judul
                                showDisposisiDialog = true
                            },
                            onAudit = {
                                selectedDumasId = dumas.id
                                selectedDumasTitle = dumas.judul
                                showAuditDialog = true
                            },
                            userJabatan = userJabatan
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DumasItemCard(
    dumas: dumas,
    status: String,
    onEdit: () -> Unit,
    onVerify: () -> Unit,
    onDelete: () -> Unit,
    onDisposisi: () -> Unit,
    onAudit: () -> Unit,
    userJabatan: State<String?>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dumas.judul ?: "Tanpa Judul",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(status = status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            dumas.file_uri?.let { fileUrl ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("${env.url}/${fileUrl}")
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto Dumas",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Column {
                InfoRow(label = "Pengadu", value = dumas.nama_pengadu ?: "-")
                Spacer(modifier = Modifier.height(4.dp))
                InfoRow(label = "No HP", value = dumas.nomorhp_pengadu ?: "-")
                Spacer(modifier = Modifier.height(4.dp))
                InfoRow(label = "Tanggal", value = dumas.tanggal_audit ?: "-")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (userJabatan.value == "admin") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconActionButton(
                        icon = Icons.Default.Edit,
                        contentDescription = "Edit",
                        onClick = onEdit,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    IconActionButton(
                        icon = Icons.Default.Settings,
                        contentDescription = "Status",
                        onClick = onVerify,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    if (status == "Terverifikasi") {
                        IconActionButton(
                            icon = Icons.Default.Send,
                            contentDescription = "Disposisi",
                            onClick = onDisposisi,
                            containerColor = Color(0xFFFFA000).copy(alpha = 0.2f),
                            contentColor = Color(0xFFE65100)
                        )
                    }
                    IconActionButton(
                        icon = Icons.Rounded.MoreVert,
                        contentDescription = "Audit",
                        onClick = onAudit,
                        containerColor = Color(0xFF2196F3).copy(alpha = 0.2f),
                        contentColor = Color(0xFF0D47A1)
                    )
                    IconActionButton(
                        icon = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        onClick = onDelete,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
fun IconActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun StatusBadge(status: String) {
    val (containerColor, contentColor) = when (status) {
        "Terverifikasi" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "Disposisi" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        "Diproses" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        "Selesai" -> Color(0xFF4CAF50).copy(alpha = 0.2f) to Color(0xFF2E7D32)
        "Ditolak" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Memuat data...")
        }
    }
}

@Composable
fun ErrorState(errorMessage: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(Icons.Default.Close, "Error", tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Terjadi kesalahan", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Text("Coba Lagi")
            }
        }
    }
}

@Composable
fun EmptyState(onCreateNew: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(Icons.Default.Info, "Empty")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Tidak ada data dumas", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Belum ada data dumas yang tersedia")
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onCreateNew) {
                Text("Buat Dumas Baru")
            }
        }
    }
}

@Preview
@Composable
fun ListDumasScreenPreview() {
    MaterialTheme {
        ListDumasScreen(rememberNavController())
    }
}