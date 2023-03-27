package net.map.elixirmap.act

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import net.map.elixirmap.R

class StartAct : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_start)
        permissions
    }

    private val permissions: Unit
        private get() {
            val strings: MutableList<String> = ArrayList()
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                strings.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                strings.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            if (strings.isNotEmpty()) {
                val permissions = strings.toTypedArray()
                ActivityCompat.requestPermissions(this, permissions, 1002)
            } else {
                val intent = Intent()
                intent.setClass(this@StartAct, MapAct::class.java)
                startActivity(intent)
                finish()
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1002 -> if (grantResults.isNotEmpty()) {
                val list: MutableList<String> = ArrayList()
                var i = 0
                while (i < grantResults.size) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        val permission = permissions[i]
                        list.add(permission)
                    }
                    i++
                }
                if (list.isEmpty()) {
                    val intent = Intent()
                    intent.setClass(this@StartAct, MapAct::class.java)
                    startActivity(intent)
                    finish()
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    Toast.makeText(
                        this@StartAct,
                        "Be sure to grant location permissions, otherwise it won't work!",
                        Toast.LENGTH_LONG
                    ).show()
                    getAppDetailSettingIntent(this@StartAct)
                } else {
                    val alertDialog = AlertDialog.Builder(this)
                        .setTitle("Permission settings")
                        .setMessage("Be sure to grant location permissions, otherwise it won't work!")
                        .setPositiveButton("ok"
                        ) { _, _ -> permissions }.create()
                    alertDialog.show()
                }
            }
        }
    }

    /**
     * 跳转到权限设置界面
     */
    private fun getAppDetailSettingIntent(context: Context) {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
        intent.data = Uri.fromParts("package", packageName, null)
        startActivity(intent)
    }
}