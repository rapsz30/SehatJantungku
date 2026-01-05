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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietStartScreen(
    navController: NavController,
    dietId: String,
    viewModel: DietProgramViewModel = viewModel()
) {
    val context = LocalContext.current

    val PrimaryBlue = Color(0xFF1E88E5)
    val PinkMain = Color(0xFFFF6FB1)
    val BgGray = Color(0xFFF9FAFB)
    val StreakOrange = Color(0xFFFF6F00)
    val StreakBg = Color(0xFFFFF3E0)

    val dietProgress by viewModel.dietProgress.collectAsState()
    val isLoadingProgress by viewModel.isLoadingProgress.collectAsState()
    val dietPlan by viewModel.fetchedDietPlan.collectAsState()
    val isLoadingPlan by viewModel.isLoadingPlan.collectAsState()

    var taskStatus by remember { mutableStateOf(mutableMapOf<String, Boolean>()) }
    var showVictoryDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showStopDialog by remember { mutableStateOf(false) }

    val isSubmittedToday = remember(dietProgress) {
        val lastDate = dietProgress?.lastLogDate ?: ""
        if (lastDate.isNotEmpty()) {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            lastDate == today
        } else {
            false
        }
    }

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

    fun toggleTask(key: String) {
        if (isSubmittedToday) {
            Toast.makeText(context, "Target hari ini sudah selesai! Kembali lagi besok.", Toast.LENGTH_SHORT).show()
            return
        }
        val current = taskStatus[key] ?: false
        taskStatus = taskStatus.toMutableMap().apply { put(key, !current) }
    }

    val isLoadingTotal = isLoadingProgress || isLoadingPlan

    if (showStopDialog) {
        AlertDialog(
            onDismissRequest = { showStopDialog = false },
            icon = { Icon(Icons.Default.DeleteForever, null, tint = Color.Red) },
            title = { Text("Hapus Progres Diet?") },
            text = { Text("Seluruh catatan hari dan streak akan dihapus permanen. Anda harus mulai dari awal.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.stopCurrentDiet(context) {
                            showStopDialog = false
                            Toast.makeText(context, "Program diet dihapus", Toast.LENGTH_SHORT).show()
                            navController.navigate("diet_program") {
                                popUpTo("home") { inclusive = false }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Hapus") }
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
                            text = { Text("Hapus Progress Diet", color = Color.Red) },
                            onClick = {
                                showMenu = false
                                showStopDialog = true
                            },
                            leadingIcon = {
                                Icon(Icons.Default.DeleteForever, null, tint = Color.Red)
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (isLoadingTotal || dietPlan == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PinkMain)
            }
        } else {
            val plan = dietPlan!!
            val currentDay = dietProgress?.currentDay ?: 1
            val currentStreak = dietProgress?.currentStreak ?: 0
            val totalDays = plan.waktuDiet
            val progress = currentDay.toFloat() / totalDays.toFloat()
            val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

            val rotationIndex = (currentDay - 1) % 3
            val menuCode = when(rotationIndex) { 0 -> "A"; 1 -> "B"; else -> "C" }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(BgGray)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = StreakBg,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Current Streak",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Text(
                                text = "$currentStreak Hari",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = StreakOrange
                            )
                            Text(
                                text = if (currentStreak > 0) "Pertahankan semangatmu!" else "Ayo mulai hari ini!",
                                fontSize = 12.sp,
                                color = Color(0xFFEF6C00)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak Fire",
                                tint = StreakOrange,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, null, tint = Color(0xFFFFD700))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Progress Harian", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "Hari $currentDay / $totalDays",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = PinkMain,
                            trackColor = Color(0xFFF3E8FF),
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4F4)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PinkMain.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Warning, null, tint = Color(0xFFBE123C), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Aturan Diet", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF881337))
                            Text(plan.aturanDiet, fontSize = 12.sp, color = Color(0xFF4B5563), lineHeight = 16.sp)
                        }
                    }
                }

                Text("Tugas Hari Ini", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                val sarapanMenu = when(menuCode) { "A" -> plan.sarapanA; "B" -> plan.sarapanB; else -> plan.sarapanC }
                DetailedTaskCard("Sarapan", plan.deskripsiSarapan, plan.waktuSarapan, sarapanMenu, plan.tipsSarapan, taskStatus["sarapan"] == true, { toggleTask("sarapan") }, Icons.Default.Restaurant, PinkMain, !isSubmittedToday)

                val siangMenu = when(menuCode) { "A" -> plan.makansiangA; "B" -> plan.makansiangB; else -> plan.makansiangC }
                DetailedTaskCard("Makan Siang", plan.deskripsiMakanSiang, plan.waktuMakanSiang, siangMenu, plan.tipsMakanSiang, taskStatus["siang"] == true, { toggleTask("siang") }, Icons.Default.Restaurant, PinkMain, !isSubmittedToday)

                val malamMenu = when(menuCode) { "A" -> plan.makanmalamA.ifEmpty { "Protein + Sayur" }; "B" -> plan.makanmalamB.ifEmpty { "Protein + Sayur" }; else -> plan.makanmalamC.ifEmpty { "Protein + Sayur" } }
                DetailedTaskCard("Makan Malam", plan.deskripsiMakanMalam, plan.waktuMakanMalam, malamMenu, plan.tipsMakanMalam, taskStatus["malam"] == true, { toggleTask("malam") }, Icons.Default.Restaurant, PinkMain, !isSubmittedToday)

                val camilanMenu = when(menuCode) { "A" -> plan.camilanA; "B" -> plan.camilanB; else -> plan.camilanC }
                DetailedTaskCard("Camilan Sehat", plan.deskripsiCamilan, plan.waktuCamilan, camilanMenu, plan.tipsCamilan, taskStatus["camilan"] == true, { toggleTask("camilan") }, Icons.Default.Restaurant, PinkMain, !isSubmittedToday)

                DetailedTaskCard("Hidrasi Tubuh", "Jaga tubuh tetap terhidrasi", "Sepanjang hari", "8 Gelas Air Putih (2 Liter)", "Minum secara berkala", taskStatus["air"] == true, { toggleTask("air") }, Icons.Default.WaterDrop, Color(0xFF3B82F6), !isSubmittedToday)

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.saveChecklistOnly(taskStatus) { Toast.makeText(context, "Checklist tersimpan!", Toast.LENGTH_SHORT).show() } },
                        enabled = !isSubmittedToday,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (!isSubmittedToday) PinkMain else Color.Gray),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (!isSubmittedToday) PinkMain else Color.Gray
                        )
                    ) { Text("Simpan Saja") }

                    Button(
                        onClick = {
                            if (taskStatus.values.count { it } < 3) {
                                Toast.makeText(context, "Selesaikan minimal 3 tugas!", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.completeDay(
                                    maxDays = totalDays,
                                    onSuccess = {
                                        showVictoryDialog = true
                                        taskStatus = mutableMapOf()
                                    },
                                    onFinished = { navController.navigate("diet_completion") },
                                    onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show() }
                                )
                            }
                        },
                        enabled = !isSubmittedToday,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSubmittedToday) Color.Gray else PinkMain
                        )
                    ) {
                        Text(if (isSubmittedToday) "Kembali Besok" else "Selesaikan Hari")
                    }
                }

                if(isSubmittedToday) {
                    Text(
                        text = "Anda telah menyelesaikan laporan hari ini. Silakan kembali besok untuk melanjutkan streak!",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showVictoryDialog) {
        val earnedStreak = dietProgress?.currentStreak ?: 0

        AlertDialog(
            onDismissRequest = { showVictoryDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Target Harian Tercapai! \uD83C\uDF89",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Luar biasa! Kamu berhasil menyelesaikan semua jadwal diet hari ini.",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = StreakBg,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "\uD83D\uDD25 $earnedStreak Hari Beruntun! \uD83D\uDD25",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = StreakOrange,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Pertahankan konsistensimu demi jantung yang lebih sehat. Sampai jumpa besok!",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showVictoryDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkMain),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Siap, Lanjut Besok!", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun DetailedTaskCard(
    title: String,
    description: String,
    time: String,
    menu: String,
    tips: String,
    isCompleted: Boolean,
    onCheckChange: () -> Unit,
    icon: ImageVector,
    color: Color,
    isEnabled: Boolean = true
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEnabled) { onCheckChange() }
            .background(if(isCompleted) color.copy(alpha = 0.05f) else Color.Transparent, RoundedCornerShape(16.dp))
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
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onCheckChange() },
                    enabled = isEnabled,
                    colors = CheckboxDefaults.colors(
                        checkedColor = color,
                        uncheckedColor = if(isEnabled) Color.Gray else Color.LightGray
                    )
                )
            }
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF3F4F6))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, null, tint = if(isEnabled) color else Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(time.ifEmpty { "Sesuai Jadwal" }, fontSize = 12.sp, color = if(isEnabled) color else Color.Gray, fontWeight = FontWeight.Bold)
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