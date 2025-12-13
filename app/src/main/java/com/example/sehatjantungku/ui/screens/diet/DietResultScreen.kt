package com.example.sehatjantungku.ui.screens.diet

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietResultScreen(
    navController: NavController,
    bestDiet: String,
    scores: List<Int>
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("SehatJantungku", Context.MODE_PRIVATE)

    val cvdIntegrated = sharedPreferences.getBoolean("useCVDDataForDiet", false)

    val pinkMain = Color(0xFFFF6FB1)
    val pinkLight = Color(0xFFFF8CCF)
    val purpleLight = Color(0xFFCC7CF0)

    val dietDescriptions = mapOf(
        "Plant-Based" to "Diet berbasis tumbuhan dengan fokus pada sayuran, buah, kacang-kacangan, dan biji-bijian.",
        "DASH" to "Dietary Approaches to Stop Hypertension - diet rendah garam untuk kesehatan jantung.",
        "Mediterranean" to "Diet Mediterania dengan fokus pada minyak zaitun, ikan, dan makanan segar.",
        "Low-Sodium" to "Diet rendah natrium untuk mengontrol tekanan darah.",
        "Low-Fat" to "Diet rendah lemak untuk menurunkan berat badan dan kesehatan jantung."
    )

    val reasons = mapOf(
        "Plant-Based" to if (cvdIntegrated)
            "Berdasarkan preferensi Anda dan hasil CVD Risk, diet berbasis tumbuhan sangat cocok untuk menurunkan risiko kardiovaskular sambil memenuhi kebutuhan nutrisi Anda."
        else
            "Anda memiliki preferensi tinggi terhadap sayur dan buah, serta ingin membatasi konsumsi daging. Diet berbasis tumbuhan akan sangat cocok untuk gaya hidup Anda.",
        "DASH" to if (cvdIntegrated)
            "Hasil CVD Risk Predictor Anda menunjukkan pentingnya menurunkan tekanan darah. Diet DASH dirancang khusus untuk kesehatan jantung dan sangat sesuai dengan kondisi Anda."
        else
            "Anda ingin mengurangi asupan garam dengan serius. Diet DASH dirancang khusus untuk menurunkan tekanan darah dan menjaga kesehatan jantung Anda.",
        "Mediterranean" to if (cvdIntegrated)
            "Dengan mempertimbangkan hasil CVD Risk Anda, diet Mediterania menawarkan keseimbangan nutrisi yang terbukti baik untuk kesehatan jantung."
        else
            "Anda memiliki preferensi seimbang antara sayur, buah, dan protein. Diet Mediterania menawarkan fleksibilitas dengan tetap menjaga kesehatan jantung.",
        "Low-Sodium" to if (cvdIntegrated)
            "Hasil CVD Risk Anda menekankan pentingnya mengurangi natrium. Diet rendah natrium akan membantu mengontrol tekanan darah dan risiko kardiovaskular."
        else
            "Fokus Anda pada pengurangan garam menunjukkan bahwa diet rendah natrium adalah pilihan terbaik untuk kesehatan kardiovaskular Anda.",
        "Low-Fat" to if (cvdIntegrated)
            "Kombinasi preferensi Anda dan hasil CVD Risk menunjukkan bahwa mengurangi lemak adalah langkah penting untuk kesehatan jantung Anda."
        else
            "Anda ingin mengurangi lemak dan berminyak. Diet rendah lemak akan membantu mencapai tujuan berat badan dan kesehatan jantung Anda."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rekomendasi Diet Anda", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = pinkMain,
                    titleContentColor = Color.White
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(pinkLight, purpleLight)
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                "Diet Terbaik untuk Anda",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            bestDiet,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            dietDescriptions[bestDiet] ?: "",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            if (cvdIntegrated) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFF1E40AF),
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                "Rekomendasi Berdasarkan CVD Risk",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E3A8A)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Diet ini dipilih dengan mempertimbangkan hasil prediksi CVD Risk Anda untuk memberikan manfaat optimal bagi kesehatan jantung Anda.",
                                fontSize = 12.sp,
                                color = Color(0xFF1E40AF),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Mengapa diet ini?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        reasons[bestDiet] ?: "Diet ini paling sesuai dengan preferensi dan tujuan kesehatan Anda.",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        lineHeight = 22.sp
                    )
                }
            }

            // Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        // Save active program
                        sharedPreferences.edit().apply {
                            putString("activeDietProgram", bestDiet)
                            putInt("dietCurrentDay", 1)
                            putInt("dietTotalDays", 21)
                            apply()
                        }
                        navController.navigate("diet_start/$bestDiet")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = pinkMain
                    )
                ) {
                    Text("Mulai Program Diet", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = pinkMain
                    )
                ) {
                    Text("Isi Ulang Form", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
