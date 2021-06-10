package tecnm.mx.tepic.ladm_u3_practica2_firebase_pedidos

import android.hardware.camera2.TotalCaptureResult
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2:AppCompatActivity() {
    var baseRemota = FirebaseFirestore.getInstance()
    var id = "";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        var extra = intent.extras
        id = extra!!.getString("idElegido")!!
        baseRemota.collection("pedidos")
                .document(id)
                .get()
                .addOnSuccessListener {
                    actnombre.setText(it.getString("nombre"))
                    actentregado.setText(it.getString("entregado"))
                }
                .addOnFailureListener {
                    alerta("ERROR! NO EXISTE ID ${id}")
                }
        button2.setOnClickListener {
            actualizar()
        }
        button3.setOnClickListener {
            finish()
        }

    }
    private fun actualizar() {
        baseRemota.collection("pedidos")
                .document(id)
                .update("entregado",actentregado.text.toString())
                .addOnSuccessListener {
                    alerta("EXITO! SE ACTUALIZO EL ESTATUS DE ENTREGA")
                }
                .addOnFailureListener {
                    mensaje("ERROR NO SE PUDO ACTUALIZAR")
                }
    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this).setTitle("ATENCION")
                .setMessage(s)
                .setPositiveButton("OK"){d,i->}
                .show()
    }

    private fun alerta(s: String) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show()
    }

}
