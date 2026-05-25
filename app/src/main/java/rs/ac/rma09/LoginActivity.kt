package rs.ac.rma09

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private lateinit var prefs: SharedPreferences
    private val db: DataBase by lazy { DataBase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.editUsername)
        passwordInput = findViewById(R.id.editPassword)
        loginButton = findViewById(R.id.btnLogin)
        registerButton = findViewById(R.id.btnRegister)

        prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Wrong email format!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 4 || password.length > 10) {
                Toast.makeText(this, "Password must be between 4 and 10 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ovde se email koristi kao username jer baza koristi kolonu "username"
            if (db.checkUserCredentials(email, password)) {
                val userId = db.getUserId(email)

                if (userId != null) {
                    prefs.edit()
                        .putInt("loggedInUserId", userId)
                        .putString("loggedInUserEmail", email)
                        .apply()

                    Toast.makeText(this, "Welcome, $email!", Toast.LENGTH_LONG).show()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("username", email)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Login error!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Wrong email or password!", Toast.LENGTH_SHORT).show()
            }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}