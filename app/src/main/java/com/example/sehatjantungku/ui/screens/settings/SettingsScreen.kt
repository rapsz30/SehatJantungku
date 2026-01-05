package com.example.sehatjantungku.ui.screens.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sehatjantungku.ui.components.BottomNavBar
import com.example.sehatjantungku.ui.screens.diet.DietProgramViewModel
import com.example.sehatjantungku.ui.viewmodel.AuthViewModel
import com.example.sehatjantungku.utils.DietNotificationHelper
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    dietViewModel: DietProgramViewModel = viewModel() // Inject ViewModel Diet
) {
    val context = LocalContext.current

    // --- STATE DATA DIET ---
    // Kita perlu memantau apakah user punya diet aktif & data plannya sudah ada
    val dietProgress by dietViewModel.dietProgress.collectAsState()
    val fetchedPlan by dietViewModel.fetchedDietPlan.collectAsState()

    // Load data diet saat layar dibuka agar tahu status diet user
    LaunchedEffect(Unit) {
        dietViewModel.loadUserDietProgress()
    }

    // --- STATE UNTUK SWITCH NOTIFIKASI ---
    var isNotificationEnabled by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else {
                true // Di bawah Android 13 dianggap aktif default (kecuali dimatikan manual di settings HP)
            }
        )
    }

    // Fungsi Pembantu: Jadwalkan Alarm jika data plan tersedia
    fun scheduleIfPossible() {
        val activeDietId = dietProgress?.dietId
        if (activeDietId != null) {
            // Jika plan belum ada di memori, fetch dulu dari Firebase
            if (fetchedPlan == null || fetchedPlan?.id.toString() != activeDietId) {
                dietViewModel.fetchDietPlanFromFirebase(activeDietId)
                Toast.makeText(context, "Memuat jadwal diet...", Toast.LENGTH_SHORT).show()
            } else {
                // Jika plan sudah ada, langsung jadwalkan
                val helper = DietNotificationHelper(context)
                helper.scheduleDietPlan(fetchedPlan!!)
                Toast.makeText(context, "Pengingat makan diaktifkan!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Anda belum memulai program diet.", Toast.LENGTH_SHORT).show()
        }
    }

    // Observer: Jika fetchedPlan baru saja berhasil diload dan notifikasi ON, jadwalkan alarm
    LaunchedEffect(fetchedPlan) {
        if (fetchedPlan != null && isNotificationEnabled) {
            val helper = DietNotificationHelper(context)
            helper.scheduleDietPlan(fetchedPlan!!)
        }
    }

    // Launcher untuk Request Permission
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            isNotificationEnabled = isGranted
            if (isGranted) {
                scheduleIfPossible()
            } else {
                Toast.makeText(context, "Izin notifikasi diperlukan untuk pengingat.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    fun toggleNotification(isChecked: Boolean) {
        val helper = DietNotificationHelper(context)

        if (isChecked) {
            // User ingin MENYALAKAN
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Cek izin dulu untuk Android 13+
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                isNotificationEnabled = true
                scheduleIfPossible()
            }
        } else {
            // User ingin MEMATIKAN
            isNotificationEnabled = false
            helper.cancelAllAlarms() // Batalkan semua alarm
            Toast.makeText(context, "Pengingat dimatikan.", Toast.LENGTH_SHORT).show()

            // Opsional: Arahkan ke settings HP jika ingin memblokir total
            // val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            //     data = Uri.fromParts("package", context.packageName, null)
            // }
            // context.startActivity(intent)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController, currentRoute = "settings")
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Umum",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item { SettingsItem(Icons.Default.Person, "Akun") { navController.navigate("settings/account") } }
            item { SettingsItem(Icons.Default.Language, "Bahasa") { navController.navigate("settings/language") } }

            // --- MENU NOTIFIKASI DENGAN SWITCH ---
            item {
                SettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = "Pengingat Makan",
                    checked = isNotificationEnabled,
                    onCheckedChange = { toggleNotification(it) }
                )
            }

            item { SettingsItem(Icons.Default.Help, "Pusat Bantuan") { navController.navigate("settings/help") } }
            item { Spacer(modifier = Modifier.height(32.dp)) }

            // --- TOMBOL LOGOUT ---
            item {
                Button(
                    onClick = {
                        authViewModel.clearUserData()
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFE5E5),
                        contentColor = Color.Red
                    ),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Keluar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

// Komponen Item Biasa (Panah Kanan)
@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFFFF6FB1), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

// Komponen Item Switch (On/Off)
@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFFFF6FB1), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFFFF6FB1)
                )
            )
        }
    }
}