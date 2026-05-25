package rs.ac.rma09

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class TaskListActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var db: DataBase
    private lateinit var tasksContainer: LinearLayout
    private lateinit var userEmailText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        val backBtn = findViewById<Button>(R.id.btnBackToMain)
        backBtn.setOnClickListener { finish() }

        prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        db = DataBase(this)

        val userId = prefs.getInt("loggedInUserId", -1)
        val userEmail = prefs.getString("loggedInUserEmail", null)

        tasksContainer = findViewById(R.id.tasksContainer)
        userEmailText = findViewById(R.id.userEmailText)

        if (userId == -1 || userEmail == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        userEmailText.text = "Logged user: $userEmail"
        showUserTasks(userId)
    }

    private fun showUserTasks(userId: Int) {
        val tasks = db.getTasksForUser(userId)
        tasksContainer.removeAllViews()

        for (task in tasks) {
            val cardView = CardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, 24) }
                radius = 20f
                setCardBackgroundColor(Color.parseColor("#FFF0F5"))
                cardElevation = 8f
                setContentPadding(24, 24, 24, 24)
            }

            val innerLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
            }


            val createdAtFormatted = task.createdAt.replace("T", " ").take(16)
            val doneAtFormatted = task.doneAt?.replace("T", " ")?.take(16)

            val titleView = TextView(this).apply {
                text = "🔹 ${task.title.uppercase()}"
                textSize = 18f
                setTextColor(Color.BLACK)
            }

            val descView = TextView(this).apply {
                text = "📄 ${task.description}"
                textSize = 16f
                setTextColor(Color.DKGRAY)
            }

            val createdView = TextView(this).apply {
                text = "🕓 Created: $createdAtFormatted"
                textSize = 14f
                setTextColor(Color.GRAY)
            }

            val priorityView = TextView(this).apply {
                text = "⭐ Priority: ${task.priority}"
                textSize = 14f
                setTextColor(Color.GRAY)
            }

            val statusView = TextView(this).apply {
                text = if (task.isDone) {
                    "✅ Done\n🕒 Completed: ${doneAtFormatted ?: "Unknown"}"
                } else {
                    "⏳ In progress"
                }
                textSize = 14f
                setTextColor(Color.DKGRAY)
            }

            val buttonLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 16, 0, 0)
            }

            val btnEdit = Button(this).apply {
                text = "Edit"
                textSize = 14f
                setBackgroundColor(Color.parseColor("#FF4081"))
                setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    setMargins(0, 0, 8, 0)
                }
                setOnClickListener {
                    val intent = Intent(this@TaskListActivity, UpdateTaskActivity::class.java).apply {
                        putExtra("taskId", task.id)
                        putExtra("title", task.title)
                        putExtra("description", task.description)
                        putExtra("isDone", task.isDone)
                        putExtra("priority", task.priority)
                    }
                    startActivityForResult(intent, 1001)
                }
            }

            val btnDelete = Button(this).apply {
                text = "Delete"
                textSize = 14f
                setBackgroundColor(Color.parseColor("#B00020"))
                setTextColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setOnClickListener {
                    android.app.AlertDialog.Builder(this@TaskListActivity)
                        .setTitle("Delete Task")
                        .setMessage("Are you sure you want to delete this task?")
                        .setPositiveButton("Yes") { _, _ ->
                            if (db.deleteTask(task.id)) {
                                Toast.makeText(this@TaskListActivity, "Task deleted", Toast.LENGTH_SHORT).show()
                                showUserTasks(userId)
                            } else {
                                Toast.makeText(this@TaskListActivity, "Error deleting task", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
            }

            buttonLayout.addView(btnEdit)
            buttonLayout.addView(btnDelete)


            innerLayout.addView(titleView)
            innerLayout.addView(descView)
            innerLayout.addView(createdView)
            innerLayout.addView(priorityView)
            innerLayout.addView(statusView)
            innerLayout.addView(buttonLayout)

            cardView.addView(innerLayout)
            tasksContainer.addView(cardView)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val userId = prefs.getInt("loggedInUserId", -1)
            showUserTasks(userId)
        }
    }
}



