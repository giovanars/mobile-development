package br.com.fiap.grupodois.mobiledevelopment.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.fiap.grupodois.mobiledevelopment.R
import br.com.fiap.grupodois.mobiledevelopment.databinding.ActivityMainBinding
import br.com.fiap.grupodois.mobiledevelopment.models.Product
import br.com.fiap.grupodois.mobiledevelopment.models.RequestState
import br.com.fiap.grupodois.mobiledevelopment.ui.newproduct.NewProductActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: MainListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRecyclerView()
        initViewModel()
        initObservers()
        initListeners()
    }

    private fun initListeners(){
        binding.fabNewProduct.setOnClickListener{
            val nextScreen = Intent(this, NewProductActivity::class.java)
            newProductRequest.launch(nextScreen)
        }
    }

    private fun setUpRecyclerView () {
        adapter = MainListAdapter { mainViewModel.delete(it) }

        binding.rvProducts.adapter = adapter
        binding.rvProducts.layoutManager = LinearLayoutManager(this)
    }

    private val newProductRequest = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == RESULT_OK) {
                it.data?.getStringExtra(NewProductActivity.EXTRA_REPLY)?.let {
                    val product = Product(it)
                    mainViewModel.insert(product)
                }
            }
        }

    private fun initViewModel(){
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private fun initObservers() {
       /* mainViewModel.products.observe(this ){
            it?.let {
                adapter.setProducts(it)
            }
        }*/

        mainViewModel.productState.observe(this, Observer{
            when(it){
                is RequestState.Success -> {
                    it?.let {
                        adapter.setProducts(it.data)
                    }
                }
                is RequestState.Error -> {
                    Toast.makeText(this, it.throwable.message, Toast.LENGTH_SHORT).show()
                }
                is RequestState.Loading -> {

                }
            }
        })


        mainViewModel.mainState.observe(this, Observer {
            when (it) {
                is RequestState.Success -> {
                    Toast.makeText(this, it.data, Toast.LENGTH_SHORT).show()
                }
                is RequestState.Error -> {
                    Toast.makeText(this, it.throwable.message, Toast.LENGTH_SHORT).show()
                }
                is RequestState.Loading -> {
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btDelete -> {
                dialogDelete().show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun dialogDelete(): AlertDialog {
        return AlertDialog.Builder(this)
            .setTitle("Produtos")
            .setMessage("Deseja apagar sua lista?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Apagar") { dialog, _ ->
                mainViewModel.deleteAll()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") {
                    dialog, _ -> dialog.dismiss()
            }
            .create()
    }
}