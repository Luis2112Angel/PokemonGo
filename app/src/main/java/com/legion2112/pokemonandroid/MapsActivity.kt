package com.legion2112.pokemonandroid

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.os.AsyncTask
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()
        LoadPockemon()
        GetSunset()
    }

    var ACCESSLOCATION = 123
    fun checkPermission(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.
                    checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), ACCESSLOCATION)
                return
            }
        }

        GetUserLocation()
    }

    @SuppressLint("MissingPermission")
    fun GetUserLocation(){
        Toast.makeText(this,"User location access on",Toast.LENGTH_LONG).show()
        //TODO: Will implement later

        var myLocation= MylocationListener()

        var locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,myLocation)

        var mythread=myThread()
        mythread.start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){

            ACCESSLOCATION->{

                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    GetUserLocation()
                } else {
                    Toast.makeText(this,"We cannot access to your location",Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }

    var location:Location?=null

    //Get user location

    inner class MylocationListener:LocationListener{


        constructor(){
            location= Location("Start")
            location!!.longitude=0.0
            location!!.longitude=0.0
        }
        override fun onLocationChanged(p0: Location?) {
            location=p0
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(p0: String?) {
            // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(p0: String?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    var oldLocation:Location?=null

    inner class myThread:Thread{

        constructor():super(){
            oldLocation= Location("Start")
            oldLocation!!.longitude=0.0
            oldLocation!!.longitude=0.0
        }

        override fun run(){

            while (true){

                try {

                    if(oldLocation!!.distanceTo(location)==0f){
                        continue
                    }

                    oldLocation=location


                    runOnUiThread {


                        mMap!!.clear()

                        // show me
                        val usbi = LatLng(18.915193, -97.028844)
                        mMap!!.addMarker(MarkerOptions()
                            .position(usbi)
                            .title("Yo")
                            .snippet("Aqui esta mi localizacion")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario)))
                        //mMap.addMarker(MarkerOptions().position(usbi).title("Marker in USBI"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(usbi, 18f))

                        // show pockemons

                        for(i in 0..listPockemons.size-1){

                            var newPockemon=listPockemons[i]

                            if(newPockemon.IsCatch==false){

                                val pockemonLoc = LatLng(newPockemon.location!!.latitude, newPockemon.location!!.longitude)
                                mMap!!.addMarker(MarkerOptions()
                                    .position(pockemonLoc)
                                    .title(newPockemon.name!!)
                                    .snippet(newPockemon.des!! +", power:"+ newPockemon!!.power)
                                    .icon(BitmapDescriptorFactory.fromResource(newPockemon.image!!)))


                                if (location!!.distanceTo(newPockemon.location)<2){
                                    newPockemon.IsCatch=true
                                    listPockemons[i]=newPockemon
                                    playerPower+=newPockemon.power!!
                                    Toast.makeText(applicationContext,
                                        "You catch new pockemon your new power is " + playerPower,
                                        Toast.LENGTH_LONG).show()

                                }

                            }
                        }

                    }

                    Thread.sleep(1000)

                }catch (ex:Exception){}


            }

        }

    }


    var playerPower=0.0
    var listPockemons=ArrayList<Pockemon>()

    fun  LoadPockemon(){

        listPockemons.add(Pockemon(R.drawable.charmander,
            "Charmander", "Charmander living in usbi", 55.0, 18.915838, -97.029514))
        listPockemons.add(Pockemon(R.drawable.bulbasaur,
            "Bulbasaur", "Bulbasaur living in sumidero", 90.5, 18.914849,  -97.026193))
        listPockemons.add(Pockemon(R.drawable.squirtle,
            "Squirtle", "Squirtle living in cafeteria el chino", 33.5, 18.916214, -97.030286))

    }

    protected fun GetSunset(){
        var city = "Orizaba,Ver"
        //val url="https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22"+ city +"%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys"
        val url = "https://upheld-castle-251021.appspot.com/entidades"
        MyAsyncTask().execute(url)

    }

    inner class MyAsyncTask: AsyncTask<String, String, String>() {

        var inString="";

        override fun onPreExecute() {
            //Before task started
        }
        override fun doInBackground(vararg p0: String?): String {

            try {

                val url=URL(p0[0])

                val urlConnect=url.openConnection() as HttpURLConnection
                urlConnect.connectTimeout=7000

                inString= ConvertStreamToString(urlConnect.inputStream)
                //Cannot access to ui
                publishProgress(inString)
            }catch (ex:Exception){}


            return " "

        }

        override fun onProgressUpdate(vararg values: String?) {
            try{
                //var json=JSONObject(values[0])
                //val query=json.getJSONObject("query")
                //val results=query.getJSONObject("results")
                //val channel=results.getJSONObject("channel")
                //val astronomy=channel.getJSONObject("astronomy")
                //var sunrise=astronomy.getString("sunrise")
                //tvSunSetTime.text = " Sunrise time is "+ sunrise


            }catch (ex:Exception){}
        }

        override fun onPostExecute(result: String?) {
            Toast.makeText(applicationContext, "result" + inString, Toast.LENGTH_LONG).show()
            //after task done
        }
    }


    fun ConvertStreamToString(inputStream:InputStream):String{

        val bufferReader=BufferedReader(InputStreamReader(inputStream))
        var line:String
        var AllString:String=""

        try {
            do{
                line=bufferReader.readLine()
                if(line!=null){
                    AllString+=line
                }
            }while (line!=null)
            inputStream.close()
        }catch (ex:Exception){}

        return AllString
    }

}
