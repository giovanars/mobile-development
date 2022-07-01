package br.com.fiap.grupodois.mobiledevelopment.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.fiap.grupodois.mobiledevelopment.models.Product
import br.com.fiap.grupodois.mobiledevelopment.models.RequestState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application)
{
    //val products: LiveData<List<Product>>


    //private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    val mainState = MutableLiveData<RequestState<String>>()
    val productState = MutableLiveData<RequestState<List<Product>>>()

    init {
        getProductsInFireStore()
    }

    fun insert(product: Product) = viewModelScope.launch(Dispatchers.IO) {
        saveProductInFireStore(product)
    }

    fun deleteAll(){

    }

    fun delete(product: Product){

    }

    private fun saveProductInFireStore(product: Product){
        db.collection("products")
            .add(product)
            .addOnSuccessListener {
                mainState.value = RequestState.Success("Produto criado com sucesso!!")
            }
            .addOnFailureListener{ it ->
                mainState.value = RequestState.Error(Throwable(it.message))
            }
    }

    private fun getProductsInFireStore(){
        db.collection("products")
            .get()
            .addOnSuccessListener { documentReference ->
                val product = documentReference.toObjects<Product>()
                productState.value = RequestState.Success(product)
            }
            .addOnFailureListener{ it ->
                productState.value = RequestState.Error(Throwable(it.message))
            }
    }
}