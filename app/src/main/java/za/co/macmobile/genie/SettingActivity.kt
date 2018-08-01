package za.co.macmobile.genie

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.Window
import android.view.WindowManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_settings.*
import org.json.JSONObject
import za.co.macmobile.genie.adapters.ColourAdapter
import za.co.macmobile.genie.adapters.ItemAdapter
import za.co.macmobile.genie.models.Item
import za.co.macmobile.genie.util.SharedPrefs
import za.co.macmobile.genie.util.StatusBarUtil
import java.util.ArrayList

class SettingActivity : AppCompatActivity() {
    //region Declarations
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var items: MutableList<Item>
    private lateinit var gson: Gson
    private lateinit var context: Context
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var colourAdapter: ColourAdapter
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_settings)
        context = this
        StatusBarUtil.immersive(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        gson = Gson()
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        collapseBtn.setOnClickListener { bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
        avatarLayout.setOnClickListener { showAvatarSheet() }
        colorLayout.setOnClickListener { showSecColourSheet() }


    }

    override fun onPostResume() {
        super.onPostResume()
        waveHeader.startColor = Color.parseColor(SharedPrefs.retrieveSecColour(context))
        waveHeader.closeColor = Color.parseColor(SharedPrefs.retrieveSecColour(context))
    }

    //region Avatars
    private fun showAvatarSheet() {
        bottomSheetTitle.setText(resources.getString(R.string.avatar))
        populateAvatars()

        recyclerView.layoutManager = GridLayoutManager(context, 2, LinearLayoutManager.HORIZONTAL, false)
        itemAdapter = ItemAdapter(items, context)
        recyclerView.adapter = itemAdapter


        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

    }


    private fun populateAvatars() {
        items = ArrayList()
        val fileText = applicationContext.assets.open("avatars.json").bufferedReader().use { it.readText() }
        val json = JSONObject(fileText)
        val avatarsJson = json.getJSONArray("items")
        for (i in 0 until avatarsJson.length()) {
            val item: Item = gson.fromJson(avatarsJson.getJSONObject(i).toString(), Item::class.java)
            items.add(item)
        }

    }

    //endregion

    //region Secondary Colour
    private fun showSecColourSheet() {
        bottomSheetTitle.setText(resources.getString(R.string.color))
        populateSecColours()

        recyclerView.layoutManager = GridLayoutManager(context, 3)
        colourAdapter = ColourAdapter(items, context,waveHeader)
        recyclerView.adapter = colourAdapter


        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun populateSecColours() {
        items = ArrayList()
        val fileText = applicationContext.assets.open("secondaryColours.json").bufferedReader().use { it.readText() }
        val json = JSONObject(fileText)
        val avatarsJson = json.getJSONArray("items")
        for (i in 0 until avatarsJson.length()) {
            val item: Item = gson.fromJson(avatarsJson.getJSONObject(i).toString(), Item::class.java)
            items.add(item)
        }

    }

    //endregion
    override fun onCreateOptionsMenu(menu: Menu?): kotlin.Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        // super.onBackPressed()
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}
