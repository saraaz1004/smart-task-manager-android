package rs.ac.rma09

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import rs.ac.rma09.databinding.SingleContactBinding

class SingleContactItem(context: Context, contactModel: ContactModel) : ConstraintLayout(context) {

    private val binding = SingleContactBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.textViewName.text = contactModel.name
        binding.textViewEmail.text = contactModel.email
        binding.textViewPhone.text = contactModel.phone
    }
}
