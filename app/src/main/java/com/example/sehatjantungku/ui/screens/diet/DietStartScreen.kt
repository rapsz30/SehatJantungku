package com.example.sehatjantungku.ui.screens.diet

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietStartScreen(
    navController: NavController,
    dietId: String,
    viewModel: DietProgramViewModel
) {
    val context = LocalContext.current

    val dietProgress by viewModel.dietProgress.collectAsState()
    val isLoadingProgress by viewModel.isLoadingProgress.collectAsState()

    val dietPlan by viewModel.fetchedDietPlan.collectAsState()
    val isLoadingPlan by viewModel.isLoadingPlan.collectAsState()

    var taskStatus by remember { mutableStateOf(mutableMapOf<String, Boolean>()) }
    var showVictoryDialog by remember { mutableStateOf(false) }

    // State Menu & Stop Dialog
    var showMenu by remember { mutableStateOf(false) }
    var showStopDialog by remember { mutableStateOf(false) }

    // [PERBAIKAN] Selalu load data saat layar dibuka untuk memastikan data fresh (misal setelah reset)
    LaunchedEffect(Unit) {
        viewModel.loadUserDietProgress()
    }

    LaunchedEffect(dietProgress, dietId) {
        val idToFetch = dietProgress?.dietId?.takeIf { it.isNotEmpty() } ?: dietId
        if (idToFetch.isNotEmpty()) {
            viewModel.fetchDietPlanFromFirebase(idToFetch)
        }
    }

    LaunchedEffect(dietProgress) {
        dietProgress?.let {
            taskStatus = it.tasks.toMutableMap()
        }
    }

    val pinkMain = Color(0xFFFF6FB1)
    val bgGray = Color(0xFFF9FAFB)
    val isLoadingTotal = isLoadingProgress || isLoadingPlan

    fun toggleTask(key: String) {
        val current = taskStatus[key] ?: false
        taskStatus = taskStatus.toMutableMap().apply { put(key, !current) }
    }

    // --- DIALOG STOP DIET ---
    if (showStopDialog) {
        AlertDialog(
            onDismissRequest = { showStopDialog = false },
            icon = { Icon(Icons.Default.DeleteForever, null, tint = Color.Red) },
            title = { Text("Batalkan Program Diet?") },
            text = { Text("Seluruh progres akan dihapus. Anda harus mengisi ulang kuesioner jika ingin memulai lagi.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.stopCurrentDiet {
                            showStopDialog = false
                            Toast.makeText(context, "Program diet dibatalkan", Toast.LENGTH_SHORT).show()
                            navController.navigate("diet_program") {
                                popUpTo("home") { inclusive = false }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Ya, Hentikan") }
            },
            dismissButton = {
                TextButton(onClick = { showStopDialog = false }) { Text("Batal") }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Diet Tracker", fontWeight = FontWeight.Bold)
                        if (dietPlan != null) Text(dietPlan!!.dietName, fontSize = 12.sp, fontWeight = FontWeight.Normal)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "Opsi")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Batalkan Program Diet", color = Color.Red) },
                            onClick = {
                                showMenu = false
                                showStopDialog = true
                            },
                            leadingIcon = { Icon(Icons.Default.Cancel, null, tint = Color.Red) }
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (isLoadingTotal || dietPlan == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = pinkMain)
            }
        } else {
            val plan = dietPlan!!
            val currentDay = dietProgress?.currentDay ?: 1
            val totalDays = plan.waktuDiet
            val progress = currentDay.toFloat() / totalDays.toFloat()
            val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

            val rotationIndex = (currentDay - 1) % 3
            val menuCode = when(rotationIndex) { 0 -> "A"; 1 -> "B"; else -> "C" }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(bgGray)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Progress
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFFFD700))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hari $currentDay / $totalDays", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.weight(1f))
                            Text("${(animatedProgress * 100).toInt()}%", color = pinkMain, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = pinkMain,
                            trackColor = Color(0xFFF3E8FF),
                        )
                    }
                }

                // Aturan (Full Text)
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4F4)), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, pinkMain.copy(alpha = 0.2f))) {
                    Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Warning, null, tint = Color(0xFFBE123C), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Aturan Penting", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF881337))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(plan.aturanDiet, fontSize = 13.sp, color = Color(0xFF4B5563), lineHeight = 18.sp)
                        }
                    }
                }

                Text("Tugas Hari Ini (Menu $menuCode)", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                // Task List
                val sarapanMenu = when(menuCode) { "A" -> plan.sarapanA; "B" -> plan.sarapanB; else -> plan.sarapanC }
                DetailedTaskCard("Sarapan", plan.deskripsiSarapan, plan.waktuSarapan, sarapanMenu, plan.tipsSarapan, taskStatus["sarapan"] == true, { toggleTask("sarapan") }, Icons.Default.Restaurant, pinkMain)

                val siangMenu = when(menuCode) { "A" -> plan.makansiangA; "B" -> plan.makansiangB; else -> plan.makansiangC }
                DetailedTaskCard("Makan Siang", plan.deskripsiMakanSiang, plan.waktuMakanSiang, siangMenu, plan.tipsMakanSiang, taskStatus["siang"] == true, { toggleTask("siang") }, Icons.Default.Restaurant, pinkMain)

                val malamMenu = when(menuCode) { "A" -> plan.makanmalamA.ifEmpty { "Protein + Sayur" }; "B" -> plan.makanmalamB.ifEmpty { "Protein + Sayur" }; else -> plan.makanmalamC.ifEmpty { "Protein + Sayur" } }
                DetailedTaskCard("Makan Malam", plan.deskripsiMakanMalam, plan.waktuMakanMalam, malamMenu, plan.tipsMakanMalam, taskStatus["malam"] == true, { toggleTask("malam") }, Icons.Default.Restaurant, pinkMain)

                val camilanMenu = when(menuCode) { "A" -> plan.camilanA; "B" -> plan.camilanB; else -> plan.camilanC }
                DetailedTaskCard("Camilan Sehat", plan.deskripsiCamilan, plan.waktuCamilan, camilanMenu, plan.tipsCamilan, taskStatus["camilan"] == true, { toggleTask("camilan") }, Icons.Default.Restaurant, pinkMain)

                DetailedTaskCard("Hidrasi Tubuh", "Jaga tubuh tetap terhidrasi", "Sepanjang hari", "8 Gelas Air Putih (2 Liter)", "Minum 1 gelas sebelum makan.", taskStatus["air"] == true, { toggleTask("air") }, Icons.Default.WaterDrop, Color(0xFF3B82F6))

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.saveChecklistOnly(taskStatus) { Toast.makeText(context, "Checklist tersimpan!", Toast.LENGTH_SHORT).show() } },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, pinkMain),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = pinkMain)
                    ) { Text("Simpan Saja") }

                    Button(
                        onClick = {
                            if (taskStatus.values.count { it } < 3) {
                                Toast.makeText(context, "Selesaikan minimal 3 tugas!", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.completeDay(totalDays, onSuccess = { showVictoryDialog = true; taskStatus = mutableMapOf() }, onFinished = { navController.navigate("diet_completion") })
                            }
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = pinkMain)
                    ) { Text("Selesaikan Hari") }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showVictoryDialog) {
        val currentDay = dietProgress?.currentDay ?: 1
        AlertDialog(
            onDismissRequest = { showVictoryDialog = false },
            icon = { Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFFFD700), modifier = Modifier.size(48.dp)) },
            title = { Text("Hari ke-${currentDay-1} Selesai!") },
            text = { Text("Progres disimpan. Lanjut ke Hari ${currentDay}?") },
            confirmButton = { Button(onClick = { showVictoryDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = pinkMain)) { Text("Lanjut!") } },
            containerColor = Color.White
        )
    }
}

// Komponen Helper DetailedTaskCard tetap sama, tidak perlu diubah.
// (Copy dari file sebelumnya jika belum ada di sini)
@Composable
fun DetailedTaskCard(title: String, description: String, time: String, menu: String, tips: String, isCompleted: Boolean, onCheckChange: () -> Unit, icon: ImageVector, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().clickable { onCheckChange() }.background(if(isCompleted) color.copy(alpha = 0.05f) else Color.Transparent, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(36.dp).background(if(isCompleted) color else Color(0xFFF3F4F6), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = if(isCompleted) Color.White else Color.Gray, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1F2937))
                    if (description.isNotEmpty()) Text(description, fontStyle = FontStyle.Italic, fontSize = 12.sp, color = Color.Gray)
                }
                Checkbox(checked = isCompleted, onCheckedChange = { onCheckChange() }, colors = CheckboxDefaults.colors(checkedColor = color))
            }
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF3F4F6))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, null, tint = color, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(time.ifEmpty { "Sesuai Jadwal" }, fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(menu, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF374151), lineHeight = 20.sp)
            if (tips.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFFFFBEB), RoundedCornerShape(8.dp)).padding(10.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Lightbulb, null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(tips, fontSize = 12.sp, color = Color(0xFF92400E), lineHeight = 16.sp)
                }
            }
        }
    }
}