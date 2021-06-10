package tecnm.mx.tepic.ladm_u3_practica2_firebase_pedidos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var baseRemota = FirebaseFirestore.getInstance()
    var dataLista = ArrayList<String>()
    var listaID = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        baseRemota.collection("pedidos")
            .addSnapshotListener { querySnapshot, error ->
                if (error !=null){
                    mensaje(error.message!!)
                    return@addSnapshotListener
                }
                dataLista.clear()
                for (document in querySnapshot!!){
                    var cadena = "${document.getString("nombre")}--entregado: ${document.get("entregado")}--$${document.get("total")}"
                    dataLista.add(cadena)
                    listaID.add(document.id.toString())
                }
                listapedido.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataLista)
                listapedido.setOnItemClickListener { adapterView, view, posicion, l ->
                    dialogoEliminaActualiza(posicion)
                }
            }
        button.setOnClickListener {
            insertarPedidos()
        }
    }

    private fun dialogoEliminaActualiza(posicion: Int) {
        var idElegido = listaID.get(posicion)
        AlertDialog.Builder(this).setTitle("ATENCION")
                .setMessage("QUE DESEAS HACER CON \n ${dataLista.get(posicion)}?")
                .setPositiveButton("ELIMINAR"){d, i->
                    eliminar(idElegido)
                }
                .setNeutralButton("ACTUALIZAR"){d, i->
                    var intent = Intent(this,MainActivity2::class.java)
                    intent.putExtra("idElegido",idElegido)
                    startActivity(intent)
                }
                .setNegativeButton("CANCELAR"){d, i->}
                .show()

    }
    private fun eliminar(idElegido: String) {
        baseRemota.collection("pedidos")
                .document(idElegido)
                .delete()
                .addOnSuccessListener {
                    alerta("SE ELIMINO CON EXITO")
                }
                .addOnFailureListener {
                    mensaje("ERROR:${it.message!!}")
                }
    }

    private fun insertarPedidos() {
        //var total = Total()
        var datosInsertar  = hashMapOf(
            "celular" to celular.text.toString(),
             "nombre" to nombre.text.toString(),
             "fecha" to fecha.text.toString(),
             "entregado" to entregado.text.toString(),
             "pedido" to hashMapOf(
                "cantidad" to cantidad.text.toString().toInt(),
                "descripcion" to descripcion.text.toString(),
                "precio" to precio.text.toString().toInt()
            ),
            "total" to total.text.toString().toInt()
        )
        baseRemota.collection("pedidos")
            .add(datosInsertar)
            .addOnSuccessListener {
                alerta("EXITO! SE INSERTO CORRECTAMENTE LA ORDEN")
                celular.setText("")
                nombre.setText("")
                fecha.setText("")
                entregado.setText("")
                cantidad.setText("")
                descripcion.setText("")
                precio.setText("")
                total.setText("")
            }
            .addOnFailureListener {
                mensaje("ERROR! NO SE PUDO INSERTAR LA ORDEN")
            }
    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this).setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK"){d,i->}
            .show()
        }
    private fun alerta(s: String) {
        Toast.makeText(this,s, Toast.LENGTH_LONG).show()
    }

}