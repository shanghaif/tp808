package cn.com.erayton.jt_t808

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import cn.com.erayton.jt_t808.manager.ClientManager
import cn.com.erayton.jt_t808.manager.SenderManager

class MainActivity : AppCompatActivity() {

    val clientManager = ClientManager.getInstance() ;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clientManager.ServerLogin()
    }

    fun buttonClick(v:View){
        when(v.id){
            R.id.loginButton ->{
                SenderManager.SendLogin() ;
                Toast.makeText(this, "123459", Toast.LENGTH_SHORT).show()
            }
            R.id.authButton ->
                Toast.makeText(this, "123456789", Toast.LENGTH_SHORT).show()

        }
    }



}
