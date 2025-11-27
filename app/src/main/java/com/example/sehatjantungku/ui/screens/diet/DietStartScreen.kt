package com.example.sehatjantungku.ui.screens.diet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class DailyTask(
    val id: Int,
    val title: String,
    val time: String,
    val points: Int,
    val detail: String,
    val examples: List<String>,
    val tips: List<String>,
    var isCompleted: Boolean = false,
    var isExpanded: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietStartScreen(
    navController: NavController,
    dietType: String
) {
    val pinkMain = Color(0xFFFF6FB1)
    val pinkLight = Color(0xFFFF8CCF)
    val purpleLight = Color(0xFFCC7CF0)

    var streak by remember { mutableStateOf(7) }
    var totalPoints by remember { mutableStateOf(450) }
    var dailyProgress by remember { mutableStateOf(65) }

    val tasks = remember {
        mutableStateListOf(
            DailyTask(
                1, "Sarapan Sehat", "07:00 - 09:00", 50,
                "Konsumsi sarapan bergizi dengan karbohidrat kompleks, protein, dan serat untuk energi optimal di pagi hari.",
                listOf("Oatmeal dengan buah segar dan kacang", "Roti gandum dengan telur rebus dan alpukat", "Smoothie bowl dengan chia seeds"),
                listOf("Hindari gula tambahan", "Minum air putih setelah bangun", "Sarapan maksimal 2 jam setelah bangun")
            ),
            DailyTask(
                2, "Makan Siang Seimbang", "12:00 - 13:30", 50,
                "Makan siang dengan porsi seimbang: 50% sayur, 25% protein tanpa lemak, 25% karbohidrat kompleks.",
                listOf("Nasi merah + ikan kukus + tumis sayur", "Salad quinoa dengan grilled chicken", "Sup sayur dengan tahu/tempe"),
                listOf("Kunyah makanan perlahan", "Hindari gorengan", "Gunakan piring kecil untuk kontrol porsi")
            ),
            DailyTask(
                3, "Makan Malam Ringan", "18:00 - 19:30", 40,
                "Makan malam lebih ringan dan hindari makanan berat minimal 3 jam sebelum tidur.",
                listOf("Sup sayuran dengan protein nabati", "Salad dengan dressing ringan", "Pepes ikan dengan lalapan"),
                listOf("Hindari karbohidrat berlebih", "Jangan makan sambil menonton TV", "Makan 3 jam sebelum tidur")
            ),
            DailyTask(
                4, "Minum Air 2L", "Sepanjang Hari", 30,
                "Konsumsi minimal 8 gelas air putih (2 liter) untuk hidrasi optimal dan metabolisme yang baik.",
                listOf("2 gelas saat bangun", "1 gelas sebelum setiap makan", "1 gelas setelah olahraga", "Air infus lemon untuk variasi"),
                listOf("Bawa botol minum kemana-mana", "Set reminder di HP", "Hindari minuman manis")
            )
        )
    }

    val completedTasks = tasks.count { it.isCompleted }
    val totalTasks = tasks.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Program Diet $dietType", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(pinkLight, purpleLight)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔥", fontSize = 32.sp)
                            Text("$streak Hari", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Streak", color = Color.White.copy(0.9f), fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⭐", fontSize = 32.sp)
                            Text("$totalPoints Poin", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Total", color = Color.White.copy(0.9f), fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("✅", fontSize = 32.sp)
                            Text("$completedTasks/$totalTasks", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Hari Ini", color = Color.White.copy(0.9f), fontSize = 12.sp)
                        }
                    }
                }
            }

            // Daily Progress
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Progress Harian", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("$dailyProgress%", color = pinkMain, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = dailyProgress / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = pinkMain,
                        trackColor = Color(0xFFEEEEEE)
                    )
                }
            }

            // Daily Tasks
            Text(
                "Tugas Harian",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            tasks.forEach { task ->
                TaskCard(
                    task = task,
                    onCheckChanged = { isChecked ->
                        task.isCompleted = isChecked
                        if (isChecked) {
                            totalPoints += task.points
                            dailyProgress = ((completedTasks + 1) * 100) / totalTasks
                        } else {
                            totalPoints -= task.points
                            dailyProgress = ((completedTasks - 1) * 100) / totalTasks
                        }
                    },
                    onExpandToggle = {
                        task.isExpanded = !task.isExpanded
                    }
                )
            }

            // Achievements
            Text(
                "Pencapaian",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AchievementBadge("🥇", "7 Hari", true)
                AchievementBadge("🏆", "30 Hari", false)
                AchievementBadge("💎", "100 Hari", false)
                AchievementBadge("👑", "365 Hari", false)
            }
        }
    }
}

@Composable
fun TaskCard(
    task: DailyTask,
    onCheckChanged: (Boolean) -> Unit,
    onExpandToggle: () -> Unit
) {
    val pinkMain = Color(0xFFFF6FB1)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onCheckChanged,
                    colors = CheckboxDefaults.colors(checkedColor = pinkMain)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(task.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Text(task.time, fontSize = 12.sp, color = Color.Gray)
                }
                Text(
                    "+${task.points}",
                    color = pinkMain,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                IconButton(onClick = onExpandToggle) {
                    Icon(
                        if (task.isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand"
                    )
                }
            }

            if (task.isExpanded) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text(task.detail, fontSize = 14.sp, color = Color(0xFF666666), lineHeight = 20.sp)

                Spacer(modifier = Modifier.height(12.dp))
                Text("Contoh Menu:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                task.examples.forEach { example ->
                    Text("• $example", fontSize = 13.sp, color = Color(0xFF666666), modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Tips:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                task.tips.forEach { tip ->
                    Text("✓ $tip", fontSize = 13.sp, color = Color(0xFF666666), modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                }
            }
        }
    }
}

@Composable
fun RowScope.AchievementBadge(emoji: String, label: String, unlocked: Boolean) {
    Card(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked) Color(0xFFFFF3E0) else Color(0xFFEEEEEE)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 32.sp, color = if (unlocked) Color.Unspecified else Color.Gray)
            Text(label, fontSize = 10.sp, color = if (unlocked) Color(0xFF333333) else Color.Gray)
        }
    }
}
