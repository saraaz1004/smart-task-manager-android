package rs.ac.rma09

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rs.ac.rma09.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var db: DataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        db = DataBase(this)


        if (intent.getBooleanExtra("updateSuccess", false)) {
            Toast.makeText(this, "Profile successfully updated!", Toast.LENGTH_SHORT).show()
        }

        updateWelcomeText()

        binding.btnLogout.setOnClickListener {
            prefs.edit().clear().apply()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.btnViewTasks.setOnClickListener {
            val intent = Intent(this, TaskListActivity::class.java)
            startActivity(intent)
        }

        binding.btnAddTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnStats.setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }



    }

    override fun onResume() {
        super.onResume()
        updateWelcomeText()
    }

    private fun updateWelcomeText() {
        val email = prefs.getString("loggedInUserEmail", "Unknown")
        binding.txtWelcome.text = "Welcome, $email"
    }



}



