package rs.ac.rma09

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var registerBtn: Button
    private lateinit var goToLoginBtn: Button

    private val db: DataBase by lazy { DataBase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        emailInput = findViewById(R.id.editUsername)
        passwordInput = findViewById(R.id.editPassword)
        registerBtn = findViewById(R.id.btnRegisterConfirm)
        goToLoginBtn = findViewById(R.id.btnGoToLogin)

        registerBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email must be in format name@example.com", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 4 || password.length > 10) {
                Toast.makeText(this, "Password must be between 4 and 10 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (db.checkIfUserExists(email)) {
                Toast.makeText(this, "User already exists! Please log in.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            db.addUser(email, password)
            Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }

        goToLoginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}


