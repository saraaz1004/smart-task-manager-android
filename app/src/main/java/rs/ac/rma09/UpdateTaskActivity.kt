package rs.ac.rma09

import android.app.Activity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class UpdateTaskActivity : AppCompatActivity() {

    private lateinit var db: DataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_task)

        db = DataBase(this)

        val taskId = intent.getIntExtra("taskId", -1)
        val oldTitle = intent.getStringExtra("title") ?: ""
        val oldDesc = intent.getStringExtra("description") ?: ""
        val isDone = intent.getBooleanExtra("isDone", false)
        val oldPriority = intent.getStringExtra("priority") ?: "Medium"  // default fallback

        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val cbIsDone = findViewById<CheckBox>(R.id.cbIsDone)
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)
        val spinner = findViewById<Spinner>(R.id.spinnerPriority)

        val priorities = listOf("High", "Medium", "Low")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


        etTitle.setText(oldTitle)
        etDescription.setText(oldDesc)
        cbIsDone.isChecked = isDone


        val index = priorities.indexOf(oldPriority)
        if (index >= 0) spinner.setSelection(index)

        btnUpdate.setOnClickListener {
            val newTitle = etTitle.text.toString().trim()
            val newDesc = etDescription.text.toString().trim()
            val newStatus = cbIsDone.isChecked
            val newPriority = spinner.selectedItem.toString()

            if (taskId != -1) {
                val success1 = db.updateTask(taskId, newTitle, newDesc, newPriority)
                val success2 = db.updateTaskStatus(taskId, newStatus)

                if (success1 || success2) {
                    Toast.makeText(this, "Task updated!", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this, "Update failed.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Invalid task ID", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


