package rs.ac.rma09

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DataBase(context: Context) : SQLiteOpenHelper(context, DATABASE_FILE_NAME, null, 5) {

    companion object {
        const val DATABASE_FILE_NAME = "contact_database"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val contactTable = """CREATE TABLE IF NOT EXISTS ${ContactModel.TABLE_NAME} (
            ${ContactModel.COLUMN_CONTACT_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${ContactModel.COLUMN_CONTACT_NAME} TEXT,
            ${ContactModel.COLUMN_CONTACT_EMAIL} TEXT,
            ${ContactModel.COLUMN_CONTACT_PHONE} TEXT
        )"""
        db.execSQL(contactTable)

        val userTable = """CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT UNIQUE,
            password TEXT
        )"""
        db.execSQL(userTable)

        val taskTable = """CREATE TABLE IF NOT EXISTS tasks (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            title TEXT NOT NULL,
            description TEXT,
            is_done INTEGER DEFAULT 0,
            created_at TEXT DEFAULT CURRENT_TIMESTAMP,
            done_at TEXT,
            priority TEXT DEFAULT 'Medium'
        )"""
        db.execSQL(taskTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${ContactModel.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS tasks")
        onCreate(db)
    }

    // ----------------------- CONTACT METHODS -----------------------

    fun addContact(name: String, email: String, phone: String) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(ContactModel.COLUMN_CONTACT_NAME, name)
        cv.put(ContactModel.COLUMN_CONTACT_EMAIL, email)
        cv.put(ContactModel.COLUMN_CONTACT_PHONE, phone)
        db.insert(ContactModel.TABLE_NAME, null, cv)
    }

    fun editContact(contactId: Int, name: String, email: String, phone: String) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(ContactModel.COLUMN_CONTACT_NAME, name)
        cv.put(ContactModel.COLUMN_CONTACT_EMAIL, email)
        cv.put(ContactModel.COLUMN_CONTACT_PHONE, phone)
        db.update(ContactModel.TABLE_NAME, cv, "${ContactModel.COLUMN_CONTACT_ID}=?", arrayOf(contactId.toString()))
    }

    fun deleteContact(contactId: Int): Int {
        val db = writableDatabase
        return db.delete(ContactModel.TABLE_NAME, "${ContactModel.COLUMN_CONTACT_ID}=?", arrayOf(contactId.toString()))
    }

    fun getContactById(contactId: Int): ContactModel? {
        val db = readableDatabase
        val query = """SELECT * FROM ${ContactModel.TABLE_NAME} WHERE ${ContactModel.COLUMN_CONTACT_ID} = ?"""
        val cursor: Cursor = db.rawQuery(query, arrayOf(contactId.toString()))
        return if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactModel.COLUMN_CONTACT_NAME))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow(ContactModel.COLUMN_CONTACT_PHONE))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(ContactModel.COLUMN_CONTACT_EMAIL))
            cursor.close()
            ContactModel(contactId, name, email, phone)
        } else {
            cursor.close()
            null
        }
    }

    fun getAllContacts(): List<ContactModel> {
        val db = readableDatabase
        val query = "SELECT * FROM ${ContactModel.TABLE_NAME}"
        val cursor: Cursor = db.rawQuery(query, null)
        val contactList = mutableListOf<ContactModel>()
        while (cursor.moveToNext()) {
            val contactId = cursor.getInt(cursor.getColumnIndexOrThrow(ContactModel.COLUMN_CONTACT_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactModel.COLUMN_CONTACT_NAME))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow(ContactModel.COLUMN_CONTACT_PHONE))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(ContactModel.COLUMN_CONTACT_EMAIL))
            contactList.add(ContactModel(contactId, name, email, phone))
        }
        cursor.close()
        return contactList
    }

    // ----------------------- USER METHODS -----------------------

    fun checkIfUserExists(username: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", arrayOf(username))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun addUser(username: String, password: String): Boolean {
        if (checkIfUserExists(username)) return false
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("username", username)
        cv.put("password", password)
        db.insert("users", null, cv)
        return true
    }

    fun checkUserCredentials(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE username = ? AND password = ?",
            arrayOf(username, password)
        )
        val isValid = cursor.count > 0
        cursor.close()
        return isValid
    }

    fun getUserId(username: String): Int? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id FROM users WHERE username = ?", arrayOf(username))
        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            cursor.close()
            id
        } else {
            cursor.close()
            null
        }
    }

    fun updateUser(userId: Int, newUsername: String, newPassword: String): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("username", newUsername)
        cv.put("password", newPassword)
        return db.update("users", cv, "id=?", arrayOf(userId.toString())) > 0
    }

    fun getUserById(userId: Int): Pair<String, String>? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT username, password FROM users WHERE id = ?", arrayOf(userId.toString()))
        return if (cursor.moveToFirst()) {
            val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
            val password = cursor.getString(cursor.getColumnIndexOrThrow("password"))
            cursor.close()
            Pair(username, password)
        } else {
            cursor.close()
            null
        }
    }

    // ----------------------- TASK METHODS -----------------------

    fun addTask(userId: Int, title: String, description: String, priority: String = "Medium"): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val now = formatter.format(Calendar.getInstance().time)
        cv.put("user_id", userId)
        cv.put("title", title)
        cv.put("description", description)
        cv.put("created_at", now)
        cv.put("priority", priority)
        return db.insert("tasks", null, cv) != -1L
    }

    fun getTasksForUser(userId: Int): List<TaskModel> {
        val db = readableDatabase
        val query = """
        SELECT * FROM tasks WHERE user_id = ? ORDER BY
        CASE priority
            WHEN 'High' THEN 1
            WHEN 'Medium' THEN 2
            WHEN 'Low' THEN 3
            ELSE 4
        END
    """
        val cursor = db.rawQuery(query.trimIndent(), arrayOf(userId.toString()))
        val tasks = mutableListOf<TaskModel>()

        while (cursor.moveToNext()) {
            tasks.add(
                TaskModel(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    isDone = cursor.getInt(cursor.getColumnIndexOrThrow("is_done")) == 1,
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at")),
                    doneAt = cursor.getString(cursor.getColumnIndexOrThrow("done_at")),
                    priority = cursor.getString(cursor.getColumnIndexOrThrow("priority"))
                )
            )
        }

        cursor.close()
        return tasks
    }


    fun updateTaskStatus(taskId: Int, isDone: Boolean): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("is_done", if (isDone) 1 else 0)
        if (isDone) {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().time)
            cv.put("done_at", timestamp)
        } else {
            cv.putNull("done_at")
        }
        return db.update("tasks", cv, "id=?", arrayOf(taskId.toString())) > 0
    }

    fun updateTask(taskId: Int, newTitle: String, newDesc: String, newPriority: String): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put("title", newTitle)
        cv.put("description", newDesc)
        cv.put("priority", newPriority)
        return db.update("tasks", cv, "id=?", arrayOf(taskId.toString())) > 0
    }

    fun deleteTask(taskId: Int): Boolean {
        val db = writableDatabase
        return db.delete("tasks", "id=?", arrayOf(taskId.toString())) > 0
    }

    fun getTasksForUserInLast7Days(userId: Int): List<TaskModel> {
        val db = readableDatabase
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        val dateLimit = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        val cursor = db.rawQuery(
            "SELECT * FROM tasks WHERE user_id = ? AND date(created_at) >= date(?)",
            arrayOf(userId.toString(), dateLimit)
        )

        val tasks = mutableListOf<TaskModel>()
        while (cursor.moveToNext()) {
            tasks.add(
                TaskModel(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    isDone = cursor.getInt(cursor.getColumnIndexOrThrow("is_done")) == 1,
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at")),
                    doneAt = cursor.getString(cursor.getColumnIndexOrThrow("done_at")),
                    priority = cursor.getString(cursor.getColumnIndexOrThrow("priority"))
                )
            )
        }
        cursor.close()
        return tasks
    }
}
