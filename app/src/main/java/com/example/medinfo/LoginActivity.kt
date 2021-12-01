package com.example.medinfo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.medinfo.api.ServiceBuilder
import com.example.medinfo.databinding.ActivityLoginBinding
import com.example.medinfo.notification.NotificationChannel
import com.example.medinfo.repository.UserRepository
import com.google.android.gms.maps.model.Dash
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var view: ActivityLoginBinding

    private lateinit var sensorManager: SensorManager;
    private var sensor: Sensor? = null;

    companion object {
        var EMAIL: String? = null
        var PHONE: String? = null
        var FULLNAME: String? = null
        var USER_ID: String? = null

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        view = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(view.root)

        view.registerInfo.setOnClickListener {
            goToRegister()
        }

        view.btnLogin.setOnClickListener {
            login()
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager;
        if (!checkSensor()) {
            return
        } else {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sensorManager.registerListener(
                this@LoginActivity,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            );

        }
    }

    private fun checkSensor(): Boolean {
        var flag = true;
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) == null) {
            flag = false
        }
        return flag;
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val values = event!!.values[0];
        if (values < 1) {
            val builder = AlertDialog.Builder(this);
            builder.setTitle("Prevent Misoperation Mode Trigerred")
            builder.setMessage("Please do not block the top of the screen");
            builder.setIcon(android.R.drawable.ic_dialog_alert);

            var alert = builder.create();
            alert.setCancelable(true);
            alert.show();
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }


    @SuppressLint("ServiceCast")
    private fun vibratePhone() {

        val vibrator = this?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(2000)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun login() {

        CoroutineScope(Dispatchers.IO).launch {


            val userRepo = UserRepository();
            val response = userRepo.login(
                email = view.etEmail.text.toString().trim(),
                password = view.etPassword.text.toString().trim()
            );
            if (response.success == true) {

                EMAIL = response.data?.email.toString();
                PHONE = response.data?.phone.toString();
                FULLNAME = response.data?.fullName.toString();
                USER_ID = response.data?._id.toString();

                ServiceBuilder.token = "Bearer " + response.token;

                withContext(Main) {
                    startActivity(Intent(this@LoginActivity, DashbardActivity::class.java))
                    Notification(response.data?.fullName.toString());
                }
            } else {
                withContext(Main) {
                    vibratePhone();
                    makeText(this@LoginActivity, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun goToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun Notification(user: String) {

        val manager = NotificationManagerCompat.from(this);
        val channels = NotificationChannel(this);
        channels.createNotificationChannel();
        val notification = NotificationCompat.Builder(this, channels.CHANNEL_1)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle("Hi There !")
            .setContentText("Hey ${user},  Welcome to MedicInfo !")
            .setColor(Color.GREEN)
            .build();
        manager.notify(1, notification);

    }
}