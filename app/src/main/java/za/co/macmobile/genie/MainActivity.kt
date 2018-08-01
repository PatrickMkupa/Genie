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
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.os.Message
import android.util.FloatMath
import android.view.*
import android.view.animation.AnimationUtils
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.result.Result
import com.sfyc.ctpv.CountTimeProgressView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_message.*
import org.json.JSONArray
import org.json.JSONObject
import za.co.macmobile.genie.util.SharedPrefs
import za.co.macmobile.genie.util.StatusBarUtil
import za.co.macmobile.genie.util.StatusBarUtil.mixtureColor

import java.lang.Boolean
import java.util.*
import kotlin.math.sqrt


class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private val SHAKE_THRESHOLD_GRAVITY = 2.7f
    private var count = 0
    private var isShaked = false;
    private lateinit var message: String
    private lateinit var author: String
    private lateinit var dialog: Dialog
    private lateinit var context:Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this


        dialog = Dialog(this@MainActivity, R.style.CustomDialogTheme)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val id = context.resources.getIdentifier(SharedPrefs.retrieveAvatar(context), "drawable", context.packageName)
        val drawable = context.getResources().getDrawable(id)
        val secondaryColor = Color.parseColor(SharedPrefs.retrieveSecColour(context))
        genieIcon.setImageDrawable(drawable)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(secondaryColor))
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = mixtureColor(secondaryColor, 0.9f)
        dialog.window.statusBarColor = mixtureColor(secondaryColor, 0.9f)

        openButton.setBackgroundColor(secondaryColor)
        countTimeProgressView.borderDrawColor = secondaryColor
        countTimeProgressView.markBallColor = secondaryColor

    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event!!.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH
            val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)
            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                val c = count++

                if (isShaked == true) {
                    if (openButton.visibility == View.VISIBLE) {
                        normal.setText(resources.getString(R.string.answered))
                    } else {
                        normal.setText(resources.getString(R.string.collecting))
                    }

                } else {
                    isShaked = true
                    countTimeProgressView.visibility = View.VISIBLE
                    countTimeProgressView.startCountTimeAnimation()
                    normal.setText(resources.getString(R.string.collecting))

                    Fuel.get("https://talaikis.com/api/quotes/random/").responseJson() { request, response, result ->
                        //do something with response
                        when (result) {
                            is Result.Failure -> {
                                val fileText = applicationContext.assets.open("quotes.json").bufferedReader().use { it.readText() }
                                val json = JSONObject(fileText)
                                val qoutes = json.getJSONArray("quotes")
                                val r = Random()
                                val id1 = r.nextInt(qoutes.length())
                                message = qoutes.getJSONObject(id1).getString("quote");
                                author = qoutes.getJSONObject(id1).getString("author");
                            }
                            is Result.Success -> {
                                val data = result.get()
                                message = data.getString("quote").trim()
                                author = data.getString("author").trim()


                            }
                        }
                    }

                    countTimeProgressView.addOnEndListener(object : CountTimeProgressView.OnEndListener {

                        override fun onAnimationEnd() {
                            normal.setText(resources.getString(R.string.answered))
                            countTimeProgressView.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.fade_out))
                            countTimeProgressView.visibility = View.INVISIBLE
                            openButton.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.zoom_in))
                            openButton.visibility = View.VISIBLE
                            openButton.setOnClickListener({
                             openDialog()

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

    private fun dialogBack() {
        // Toast.makeText(this@MainActivity,"back pressed",Toast.LENGTH_SHORT).show()
        openButton.visibility = View.INVISIBLE
        isShaked = false
        normal.setText(resources.getString(R.string.introText))
        dialog.dismiss()
    }

    private fun openDialog() {
        dialog.setContentView(R.layout.dialog_message)
        dialog.show()
        dialog.closeButton.setOnClickListener { dialogBack() }
        dialog.shareButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, "$message  \n Written by $author")
            intent.type = "text/plain"

            startActivity(Intent.createChooser(intent, "Please select app: "))
        }
        dialog.message.setText("$message")
        dialog.authorText.setText("$author")


        //dialog.favouriteButton.playAnimation()
        dialog.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(p0: DialogInterface?, p1: Int, p2: KeyEvent?): kotlin.Boolean {
                if (p1 == KeyEvent.KEYCODE_BACK) {
                    dialogBack()
                }
                return true
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): kotlin.Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): kotlin.Boolean {

        when (item!!.itemId) {
            R.id.action_settings->{
               val intent = Intent(this,SettingActivity::class.java)
                startActivity(intent)
                finish()
            }


        }
        return super.onOptionsItemSelected(item)
    }
}
