package com.example.sehatjantungku.ui.screens.diet

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController

data class DailyTask(
    val id: Int,
    val emoji: String,
    val title: String,
    val description: String,
    val time: String,
    val points: Int,
    val shortTip: String,
    val examples: List<String>,
    var isCompleted: Boolean = false
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

    var currentDay by remember { mutableStateOf(20) } // Set to day 20 for testing
    val totalDays = 21
    var currentStreak by remember { mutableStateOf(19) }
    var totalPoints by remember { mutableStateOf(950) }

    var isRulesOpen by remember { mutableStateOf(false) }
    var showAchievementsModal by remember { mutableStateOf(false) }

    val tasks = remember {
        mutableStateListOf(
            DailyTask(
                0, "☕", "Sarapan Sehat Pagi",
                "Mulai hari dengan nutrisi lengkap",
                "06:00 - 09:00",
                20,
                "Sertakan protein, sayur, dan buah",
                listOf("Oatmeal + pisang + telur", "Roti gandum + alpukat + tomat", "Smoothie buah + yogurt")
            ),
            DailyTask(
                1, "🍽️", "Makan Siang Bergizi",
                "Porsi seimbang dengan sayuran",
                "12:00 - 14:00",
                25,
                "50% sayur, 25% protein, 25% karbo",
                listOf("Nasi merah + ikan + sayur bening", "Salad ayam dengan quinoa", "Sup sayuran + tempe")
            ),
            DailyTask(
                2, "💧", "Minum 8 Gelas Air",
                "Jaga hidrasi sepanjang hari",
                "Sepanjang hari",
                15,
                "2L air = 8 gelas @ 250ml",
                listOf("Pagi: 2 gelas", "Siang: 3 gelas", "Sore: 2 gelas", "Malam: 1 gelas")
            ),
            DailyTask(
                3, "🍎", "Camilan Sehat",
                "Buah atau kacang-kacangan",
                "10:00 atau 16:00",
                15,
                "Max 150 kalori per snack",
                listOf("Segenggam kacang almond", "1 apel atau pir", "Yogurt plain + madu")
            ),
            DailyTask(
                4, "🌙", "Makan Malam Ringan",
                "Porsi kecil, banyak sayur",
                "18:00 - 20:00",
                25,
                "Makan 3 jam sebelum tidur",
                listOf("Pepes ikan + tumis sayur", "Sup ayam + sayuran", "Tempe kukus + capcay")
            )
        )
    }

    val completedCount = tasks.count { it.isCompleted }
    val progress = (completedCount.toFloat() / tasks.size * 100).toInt()

    val achievements = listOf(
        Achievement("Minggu Pertama", "⭐", true, Color(0xFFFFC107)),
        Achievement("Minggu Kedua", "💎", true, Color(0xFF00BCD4)),
        Achievement("Minggu Ketiga", "🏆", false, Color(0xFF9C27B0)),
        Achievement("Program Selesai", "👑", false, Color(0xFFFF6FB1))
    )

    if (showAchievementsModal) {
        Dialog(onDismissRequest = { showAchievementsModal = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(pinkLight, purpleLight)
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Text(
                                    "Pencapaian Anda",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            IconButton(onClick = { showAchievementsModal = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        achievements.forEach { achievement ->
                            AchievementCard(achievement)
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Program Diet $dietType",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            "Hari $currentDay dari $totalDays",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showAchievementsModal = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF3E0))
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Achievements",
                            tint = Color(0xFFFF9800)
                        )
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
            // Stats Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard("🔥", currentStreak.toString(), "Hari Streak", Modifier.weight(1f))
                StatCard("🏆", totalPoints.toString(), "Total Poin", Modifier.weight(1f))
                StatCard("🎯", "$completedCount/${tasks.size}", "Tugas Selesai", Modifier.weight(1f))
            }

            // Daily Progress Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = null,
                                tint = pinkMain,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Progress Hari $currentDay",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            "$progress%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = pinkMain
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = progress / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = pinkMain,
                        trackColor = Color(0xFFEEEEEE)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (completedCount == tasks.size)
                            "🎉 Sempurna! Semua tugas hari ini selesai!"
                        else
                            "Masih ${tasks.size - completedCount} tugas lagi untuk hari ini",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isRulesOpen = !isRulesOpen }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📋", fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Aturan Dasar Diet $dietType",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46)
                            )
                        }
                        Icon(
                            if (isRulesOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color(0xFF10B981)
                        )
                    }

                    AnimatedVisibility(visible = isRulesOpen) {
                        Column(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RuleItem("1", "Konsistensi: Jalankan program minimal 21 hari untuk hasil optimal")
                            RuleItem("2", "Porsi: Makan 3x sehari dengan porsi sedang, hindari berlebihan")
                            RuleItem("3", "Hidrasi: Minum air putih 8 gelas (2L) setiap hari")
                            RuleItem("4", "Olahraga: Kombinasikan dengan aktivitas fisik minimal 30 menit/hari")
                            RuleItem("5", "Tidur: Istirahat cukup 7-8 jam setiap malam")
                        }
                    }
                }
            }

            // Daily Tasks Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = pinkMain,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    "Tugas Harian Anda",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            tasks.forEach { task ->
                ImprovedTaskCard(
                    task = task,
                    onToggle = {
                        task.isCompleted = !task.isCompleted
                        if (task.isCompleted) {
                            totalPoints += task.points
                        } else {
                            totalPoints -= task.points
                        }
                    }
                )
            }

            Button(
                onClick = {
                    if (completedCount == tasks.size) {
                        if (currentDay == totalDays) {
                            // Navigate to completion page
                            navController.navigate("diet_completion")
                        } else {
                            // Move to next day
                            currentDay += 1
                            currentStreak += 1
                            tasks.forEach { it.isCompleted = false }
                        }
                    } else {
                        // Show alert
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = pinkMain)
            ) {
                Text(
                    "Selesaikan Tugas Hari Ini",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StatCard(emoji: String, value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Text(
                label,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun RuleItem(number: String, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                number,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Text(
            text,
            fontSize = 14.sp,
            color = Color(0xFF065F46),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ImprovedTaskCard(task: DailyTask, onToggle: () -> Unit) {
    val pinkMain = Color(0xFFFF6FB1)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted)
                Color(0xFFFCE7F3)
            else
                Color.White
        ),
        border = if (task.isCompleted)
            androidx.compose.foundation.BorderStroke(2.dp, pinkMain)
        else
            null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .then(
                            if (task.isCompleted)
                                Modifier.background(
                                    Brush.horizontalGradient(listOf(pinkMain, Color(0xFFCC7CF0)))
                                )
                            else
                                Modifier.background(Color(0xFFF3F4F6))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(task.emoji, fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            task.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (task.isCompleted) pinkMain else Color(0xFF1F2937)
                        )
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { onToggle() },
                            colors = CheckboxDefaults.colors(checkedColor = pinkMain)
                        )
                    }

                    Text(
                        task.description,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "⏰ ${task.time}",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier
                                .background(Color(0xFFF3F4F6), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                        Text(
                            "⭐ +${task.points} poin",
                            fontSize = 11.sp,
                            color = if (task.isCompleted) Color.White else Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .background(
                                    if (task.isCompleted) pinkMain else Color(0xFFF3F4F6),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            "💡 Tips: ${task.shortTip}",
                            fontSize = 12.sp,
                            color = Color(0xFF1E40AF)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                "Contoh:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF374151)
                            )
                            task.examples.take(2).forEach { example ->
                                Text(
                                    "• $example",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked)
                achievement.color.copy(alpha = 0.1f)
            else
                Color(0xFFF3F4F6)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (achievement.isUnlocked)
                            Color.White
                        else
                            Color(0xFFE5E7EB)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    achievement.emoji,
                    fontSize = 32.sp,
                    color = if (achievement.isUnlocked)
                        Color.Unspecified
                    else
                        Color.Gray
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    achievement.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.isUnlocked)
                        Color(0xFF1F2937)
                    else
                        Color.Gray
                )
                Text(
                    if (achievement.isUnlocked) "✓ Terbuka" else "🔒 Terkunci",
                    fontSize = 12.sp,
                    color = if (achievement.isUnlocked)
                        Color(0xFF10B981)
                    else
                        Color.Gray
                )
            }
        }
    }
}

data class Achievement(
    val title: String,
    val emoji: String,
    val isUnlocked: Boolean,
    val color: Color
)
