package com.bignerdranch.android.roompost

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.gson.Gson
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var upChan: Button
    private lateinit var downChan: Button
    private lateinit var upCond: Button
    private lateinit var downCond: Button
    private lateinit var light: Button

    var temp: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        upChan = findViewById(R.id.chanelUp)
        downChan = findViewById(R.id.chanelDown)
        upCond = findViewById(R.id.canditionUp)
        downCond = findViewById(R.id.canditionDown)
        light = findViewById(R.id.lightBtn)

        val exec: ExecutorService = Executors.newSingleThreadExecutor()

        upCond.setOnClickListener {
            var tempers : Double? = null
            temp = exec.submit(Callable {
                httpRequestGet("http://smartroom.ectsserver.edu/api/Room/Light/PowerOn/3")
                httpRequestGet("http://smartroom.ectsserver.edu/api/Room/Conditioner")
            }).get()
            if(temp != null){
                val inf: conditioner = Gson().fromJson(temp.toString(), conditioner::class.java)
                tempers = inf.temperature + 1

                if(inf.on == false){
                    exec.submit(Callable {
                        httpRequestGet("http://smartroom.ectsserver.edu/api/Room/Conditioner/Power/1")
                        httpRequestGet("http://smartroom.ectsserver.edu/api/Room/Conditioner/SetTemperature/$tempers")
                    }).get()
                }
                else {
                    exec.submit(Callable {
                        httpRequestGet("http://smartroom.ectsserver.edu/api/Room/Conditioner/SetTemperature/$tempers")
                    }).get()
                }
            }
        }
        downCond.setOnClickListener {
            var tempers : Double? = null
            temp = exec.submit(Callable {
                httpRequestGet("http://smartroom.ectsserver.edu/api/Room/Light/PowerOn/3")
                httpRequestGet("http://smartroom.ectsserver.edu/api/Room/Conditioner")
            }).get()
            if(temp != null){
                val inf: conditioner = Gson().fromJson(temp.toString(), conditioner::class.java)
                tempers = inf.temperature - 1

                if(inf.on == false){
                    exec.submit(Callable {
                        httpRequestGet("http://smartroom.ectsserver.edu/api/Room/Conditioner/Power/1")
                        httpRequestGet("http://smartroom.ectsserver.edu/api/Room/Conditioner/SetTemperature/$tempers")
                    }).get()
                }
                else {
                    exec.submit(Callable {
                        httpRequestGet("http://smartroom.ectsserver.edu/api/Room/Conditioner/SetTemperature/$tempers")
                    }).get()
                }
            }
        }


        upChan.setOnClickListener {
            var chanels : Int? = null
            temp = exec.submit(Callable {
                httpRequestGet("http://smartroom.ectsserver.edu/api/Room/Light/PowerOn/3")
                httpRequestGet("http://smartroom.ectsserver.edu/api/Room/Tv")
            }).get()
            if(temp != null){
                val inf: chanel = Gson().fromJson(temp.toString(), chanel::class.java)
                Log.d("Response", inf.channel.toString())
                chanels = inf.channel+1
                if(inf.on == false){
                    exec.execute{
                        httpRequestPost("http://smartroom.ectsserver.edu/api/Room/TV/On")
                        httpRequestPost("http://smartroom.ectsserver.edu/api/Room/TV/Channel/$chanels")
                    }
                }
                else {
                    exec.execute{
                        httpRequestPost("http://smartroom.ectsserver.edu/api/Room/TV/Channel/$chanels")
                    }
                }
            }
        }
        downChan.setOnClickListener {
            var chanels : Int? = null
            temp = exec.submit(Callable {
                httpRequestGet("http://smartroom.ectsserver.edu/api/Room/Light/PowerOn/3")
                httpRequestGet("http://smartroom.ectsserver.edu/api/Room/Tv")
            }).get()
            if(temp != null){
                val inf: chanel = Gson().fromJson(temp.toString(), chanel::class.java)
                Log.d("Response", inf.channel.toString())
                chanels = inf.channel-1
                if(inf.on == false){
                    exec.execute{
                        httpRequestPost("http://smartroom.ectsserver.edu/api/Room/TV/On")
                        httpRequestPost("http://smartroom.ectsserver.edu/api/Room/TV/Channel/$chanels")
                    }
                }
                else {
                    exec.execute{
                        httpRequestPost("http://smartroom.ectsserver.edu/api/Room/TV/Channel/$chanels")
                    }
                }
            }
        }
        light.setOnClickListener {
            temp = exec.submit(Callable {
                httpRequestGet("http://smartroom.ectsserver.edu/api/Room/Light")
            }).get()
            if(temp != null){
                val inf: lights = Gson().fromJson(temp.toString(), lights::class.java)
                if(inf.status == 0){
                    exec.submit(Callable {
                        httpRequestGet("http://smartroom.ectsserver.edu/api/Room/Light/PowerOn/3")
                    }).get()
                }
                else {
                    exec.submit(Callable {
                        httpRequestGet("http://smartroom.ectsserver.edu/api/Room/Light/PowerOn/0")
                    }).get()
                }
            }
        }
    }

    @Throws(IOException::class)
    fun httpRequestGet(urlString: String):String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        var data: Int = connection.inputStream.read()
        var str = ""
        while (data != -1){
            str += data.toChar()
            data = connection.inputStream.read()
        }
        Log.d("Response", str)
        return str
    }
    fun httpRequestPost(urlString: String) {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        Log.d("Response", connection.responseCode.toString())
        /*
        var data: Int = connection.inputStream.read()
        var str = ""
        while (data != -1){
            str += data.toChar()
            data = connection.inputStream.read()
        }
        Log.d("Response", str)

         */
    }
}