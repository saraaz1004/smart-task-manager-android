package rs.ac.rma09

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AddTaskActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descInput: EditText
    private lateinit var saveButton: Button
    private lateinit var prioritySpinner: Spinner

    private lateinit var prefs: SharedPreferences
    private val db: DataBase by lazy { DataBase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        titleInput = findViewById(R.id.editTaskTitle)
        descInput = findViewById(R.id.editTaskDesc)
        saveButton = findViewById(R.id.btnSaveTask)
        prioritySpinner = findViewById(R.id.spinnerPriority)

        val priorities = arrayOf("High", "Medium", "Low")
        prioritySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, priorities)

        prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = prefs.getInt("loggedInUserId", -1)

        saveButton.setOnClickListener {
            val title = titleInput.text.toString().trim()
            val desc = descInput.text.toString().trim()
            val selectedPriority = prioritySpinner.selectedItem.toString()

            if (title.isBlank() || desc.isBlank()) {
                Toast.makeText(this, "Fill in all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId == -1) {
                Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.addTask(userId, title, desc, selectedPriority)
            Toast.makeText(this, "Task added!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
