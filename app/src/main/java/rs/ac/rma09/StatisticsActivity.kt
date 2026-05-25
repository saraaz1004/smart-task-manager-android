package rs.ac.rma09

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class StatisticsActivity : AppCompatActivity() {

    private lateinit var db: DataBase
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        db = DataBase(this)
        prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        val txtStats = findViewById<TextView>(R.id.txtStats)
        val barChart = findViewById<BarChart>(R.id.barChart)
        val btnBack = findViewById<Button>(R.id.btnBackToMain)

        val userEmail = prefs.getString("loggedInUserEmail", null)
        val userId = userEmail?.let { db.getUserId(it) }

        if (userId != null) {
            val tasks = db.getTasksForUserInLast7Days(userId)
            val doneTasks = tasks.count { it.isDone }
            val total = tasks.size
            val inProgress = total - doneTasks


            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            format.timeZone = TimeZone.getDefault()


            val doneTaskTimes = tasks.filter { it.isDone && it.doneAt != null }.mapNotNull {
                try {
                    val created = format.parse(it.createdAt)
                    val done = format.parse(it.doneAt)
                    if (created != null && done != null) {
                        val diffMillis = done.time - created.time
                        diffMillis / 60000.0 // minuta
                    } else null
                } catch (e: Exception) {
                    null
                }
            }

            val avgMinutes = if (doneTaskTimes.isNotEmpty()) {
                doneTaskTimes.average()
            } else null

            val avgText = if (avgMinutes != null) {
                val hoursPart = avgMinutes.toInt() / 60
                val minutesPart = avgMinutes.toInt() % 60
                "Avg time to complete task: ${hoursPart}h ${minutesPart}m"
            } else {
                "No completed task time data."
            }

            txtStats.text = """
                In the past 7 days, you created $total tasks.
                Completed: $doneTasks
                In progress: $inProgress
                $avgText
            """.trimIndent()

            // Dijagram
            val entries = listOf(
                BarEntry(0f, doneTasks.toFloat()),
                BarEntry(1f, inProgress.toFloat())
            )

            val dataSet = BarDataSet(entries, "Task Statistics")
            dataSet.setColors(
                Color.parseColor("#4CAF50"), // done
                Color.parseColor("#FF4081")  // in progress
            )

            val data = BarData(dataSet)
            data.barWidth = 0.5f
            barChart.data = data

            val xAxis = barChart.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Done", "In progress"))
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(true)
            xAxis.labelCount = 2
            xAxis.granularity = 1f

            barChart.axisLeft.axisMinimum = 0f
            barChart.axisRight.isEnabled = false
            barChart.description.isEnabled = false
            barChart.legend.isEnabled = false
            barChart.invalidate()
        } else {
            txtStats.text = "User not found."
        }

        btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}






