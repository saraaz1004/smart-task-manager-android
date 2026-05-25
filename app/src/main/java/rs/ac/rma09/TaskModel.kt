package rs.ac.rma09

    data class TaskModel(
        val id: Int,
        val userId: Int,
        val title: String,
        val description: String,
        val isDone: Boolean,
        val createdAt: String,
        val doneAt: String?,
        val priority: String
    )


