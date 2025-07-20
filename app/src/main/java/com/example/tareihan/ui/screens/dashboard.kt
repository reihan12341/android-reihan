package com.example.tareihan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tareihan.R
import com.example.tareihan.dto.datastore.NewDataStoreManager
import com.example.tareihan.routes.routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class MenuItem(
    val label: String,
    val icon: Int,
    val route: String,
    val allowedRoles: List<String> = emptyList()
)

val menuItems = listOf(
    MenuItem("Auditor", R.drawable.auditor, routes.list_auditor),
    MenuItem("Surat Tugas", R.drawable.auditor, routes.list_surat_tugas),
    MenuItem("Schedule", R.drawable.temuan, routes.list_Scheduleltem),
    MenuItem("Dumas", R.drawable.dumas_foto, routes.list_dumas, allowedRoles = listOf("masyarakat", "admin", "auditor")),
    MenuItem("Temuan", R.drawable.temuan, routes.list_temuan),
    MenuItem("Unit Kerja", R.drawable.unit_kerja, routes.list_unit_keja),
    MenuItem("User", R.drawable.user, routes.list_user)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val dsm = remember { NewDataStoreManager.getInstance(context) }

    val userName = dsm.nameFlow.collectAsState(initial = "")
    val userJabatan = dsm.jabatanFlow.collectAsState(initial = "")
    val userEmail = dsm.emailFlow.collectAsState(initial = "")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4A90E2),
                        Color(0xFF357ABD),
                        Color(0xFFF5F7FA)
                    ),
                    startY = 0f,
                    endY = 800f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            userName.value?.let { name ->
                userJabatan.value?.let { jabatan ->
                    HeaderSection(
                        navController = navController,
                        userName = name,
                        userJabatan = jabatan,
                        dsm = dsm,
                        coroutineScope = coroutineScope
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                StatsHeaderCard()

                Spacer(modifier = Modifier.height(28.dp))

                userJabatan.value?.let { GridStats(navController, it) }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HeaderSection(
    navController: NavController,
    userName: String,
    userJabatan: String,
    dsm: NewDataStoreManager,
    coroutineScope: CoroutineScope
) {
    val expanded = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                color = Color.White
            )
            Text(
                text = "Halo, $userName",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Text(
                text = userJabatan,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.85f)
            )
        }

        Box {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { expanded.value = !expanded.value },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.uppercase() ?: "U",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A90E2)
                    )
                )
            }

            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Logout") },
                    onClick = {
                        expanded.value = false
                        coroutineScope.launch {
                            dsm.clearAll()
                            navController.navigate("login") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.logout),
                            modifier = Modifier.size(50.dp),
                            contentDescription = "Logout Icon",
                            tint = Color(0xFF4A90E2)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun StatsHeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Selamat Datang",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = Color(0xFF2D3748)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column {
                    Text(
                        text = "Audit Pengaduan",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF48BB78)
                        )
                    )
                    Text(
                        text = "Apps",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF718096)
                    )
                }

            }
        }
    }
}

@Composable
private fun GridStats(navController: NavController, userJabatan: String) {
    if (userJabatan.isBlank()) {
        Text(
            text = "Role not specified",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF718096),
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    val verticalSpacing = 16.dp
    val filteredMenuItems = menuItems.filter {
        if (userJabatan == "masyarakat") {
            it.allowedRoles.contains(userJabatan)
        } else {
            it.allowedRoles.isEmpty() || it.allowedRoles.contains(userJabatan)
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        modifier = Modifier.fillMaxWidth()
    ) {
        filteredMenuItems.chunked(3).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { item ->
                    LargeStatCard(
                        label = item.label,
                        value = "",
                        icon = item.icon,
                        backgroundColor = Color.White,
                        iconColor = Color(0xFF4A90E2),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { navController.navigate(item.route) }
                    )
                }
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun LargeStatCard(
    label: String,
    value: String,
    icon: Int,
    backgroundColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = Color(0xFF2D3748)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF718096)
                )
            }
        }
    }
}