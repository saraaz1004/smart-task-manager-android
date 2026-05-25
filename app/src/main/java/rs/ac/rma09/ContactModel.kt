package rs.ac.rma09

data class ContactModel(
    val contactId: Int,
    val name: String,
    val email: String,
    val phone: String
) {
    companion object {
        const val TABLE_NAME = "contact"
        const val COLUMN_CONTACT_ID = "contact_id"
        const val COLUMN_CONTACT_NAME = "name"
        const val COLUMN_CONTACT_EMAIL = "email"
        const val COLUMN_CONTACT_PHONE = "phone"
    }

    override fun toString(): String {
        return "ContactModel(contactId=$contactId, name='$name', email='$email', phone='$phone')"
    }
}
