package rs.ac.rma09

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var db: DataBase
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        db = DataBase(this)
        prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        val userId = prefs.getInt("loggedInUserId", -1)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnBack = findViewById<Button>(R.id.btnBack)

        if (userId != -1) {
            val (username, password) = db.getUserById(userId) ?: Pair("", "")
            etUsername.setText(username)
            etPassword.setText(password)
        }

        btnSave.setOnClickListener {
            val newUsername = etUsername.text.toString().trim()
            val newPassword = etPassword.text.toString().trim()

            if (newUsername.length < 4) {
                Toast.makeText(this, "Username must be at least 4 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword.length < 4 || newPassword.length > 10) {
                Toast.makeText(this, "Password must be 4 to 10 characters long", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId != -1) {
                val success = db.updateUser(userId, newUsername, newPassword)
                if (success) {

                    prefs.edit()
                        .putString("loggedInUserEmail", newUsername)
                        .putInt("loggedInUserId", userId)  // Ako si dodala logiku da se ID menja, osveži ga ovde
                        .apply()

                    Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()


                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("updateSuccess", true)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                } else {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}

