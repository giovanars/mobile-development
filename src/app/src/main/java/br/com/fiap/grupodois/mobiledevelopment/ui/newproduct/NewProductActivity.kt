package br.com.fiap.grupodois.mobiledevelopment.ui.newproduct

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import br.com.fiap.grupodois.mobiledevelopment.databinding.ActivityNewProductBinding

class NewProductActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNewProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
    }

    private fun initListeners () {
        binding.btSave.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(binding.etProduct.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val product = binding.etProduct.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, product)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY = "PRODUCT"
    }
}