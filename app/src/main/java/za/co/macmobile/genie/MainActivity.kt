package za.co.macmobile.genie

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.hardware.SensorManager
import android.widget.Toast
import android.R.attr.y
import android.R.attr.x
import android.util.Log
import android.R.attr.y
import android.R.attr.x
import android.app.Dialog
import android.content.DialogInterface
import android.opengl.Visibility
import android.os.Message
import android.util.FloatMath
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.result.Result
import com.sfyc.ctpv.CountTimeProgressView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_message.*
import java.lang.Boolean
import kotlin.math.sqrt


class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private val SHAKE_THRESHOLD_GRAVITY = 2.7f
    private  var count =0
   private var  isShaked = false;
    private  lateinit var message: String
    private lateinit var author:String
    private lateinit var dialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        dialog = Dialog(this@MainActivity,R.style.CustomDialogTheme)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x= event!!.values[0]
            val y = event.values[1]
            val z= event.values[2]

            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH
            val gForce  = sqrt(gX * gX + gY * gY + gZ * gZ)
            if (gForce > SHAKE_THRESHOLD_GRAVITY){
                val c = count++

if(isShaked==true){
    if(openButton.visibility==View.VISIBLE){
        normal.setText(resources.getString(R.string.answered))
    }else{
        normal.setText(resources.getString(R.string.collecting))
    }


   // Toast.makeText(this,"Already shaked "+count++,Toast.LENGTH_SHORT).show()
}else{
    isShaked=true
    countTimeProgressView.visibility =View.VISIBLE
    countTimeProgressView.startCountTimeAnimation()
    normal.setText(resources.getString(R.string.collecting))
    Fuel.get("https://talaikis.com/api/quotes/random/").responseJson() { request, response, result ->
        //do something with response
        when (result) {
            is Result.Failure -> {
                message = "Hello Word"
                author = "me"
            }
            is Result.Success -> {
                val data = result.get()
                message= data.getString("quote").trim()
                author = data.getString("author").trim()


            }
        }
    }
    countTimeProgressView.addOnEndListener(object: CountTimeProgressView.OnEndListener{

        override fun onAnimationEnd() {
        normal.setText(resources.getString(R.string.answered))
            countTimeProgressView.startAnimation(AnimationUtils.loadAnimation(this@MainActivity,R.anim.fade_out))
            countTimeProgressView.visibility = View.INVISIBLE
            openButton.startAnimation(AnimationUtils.loadAnimation(this@MainActivity,R.anim.zoom_in))
            openButton.visibility = View.VISIBLE
            openButton.setOnClickListener({
                Toast.makeText(this@MainActivity,"click",Toast.LENGTH_SHORT).show();

                dialog.setContentView(R.layout.dialog_message)
                dialog.show()
                dialog.closeButton.setOnClickListener { dialogBack() }
                dialog.message.setText("$message")
                dialog.authorText.setText("$author")
                dialog.setOnKeyListener(object :DialogInterface.OnKeyListener{
                    override fun onKey(p0: DialogInterface?, p1: Int, p2: KeyEvent?): kotlin.Boolean {
                        if (p1 == KeyEvent.KEYCODE_BACK){
                         dialogBack()
                        }
                        return true
                    }

                })

            })

        }

        override fun onClick(overageTime: Long) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    })



}
                //numberShake.setText("Number of shakes " +c)

            }

            //Toast.makeText(this,"movement",Toast.LENGTH_SHORT).show()
        }
    }
    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }
private fun dialogBack(){
    Toast.makeText(this@MainActivity,"back pressed",Toast.LENGTH_SHORT).show()
    openButton.visibility = View.INVISIBLE
    isShaked = false
    normal.setText(resources.getString(R.string.introText))
    dialog.dismiss()
}
}