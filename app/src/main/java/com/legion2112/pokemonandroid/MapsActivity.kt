package com.legion2112.pokemonandroid

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
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var ACCESSLOCATION = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        print("-------------------------------1------------------------------------------------")
        checkPermission()
        loadPokemon()
        getSunset()
    }


    fun getSunset(){
        print("--------------------------------2--------------------------------------------")
        var city= "orizaba"
        val url="http://total-name-256003.appspot.com/pokemons"
        //val url ="link de la api"
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
                /*var json=JSONObject(values[0])
                val query=json.getJSONObject("query")
                val results=query.getJSONObject("results")
                val channel=results.getJSONObject("channel")
                val astronomy=channel.getJSONObject("astronomy")
                var sunrise=astronomy.getString("sunrise")
                Toast.makeText(applicationContext," Sunrise time is "+ sunrise,  Toast.LENGTH_LONG).show()*/



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

    private fun checkPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if(ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), ACCESSLOCATION)
                return
            }
        }

        getUserLocation()
    }

    private fun getUserLocation(){
        Toast.makeText(this,"User location access on",Toast.LENGTH_LONG).show()

        val listener = PokeListener()

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show()
            return
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,listener)

        val thread = PokeThread()
        thread.start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            ACCESSLOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getUserLocation()
                }
                else{
                    Toast.makeText(this,"We cannot access to your location",Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // Poke Settings

    var playerPower = 0.0
    var pokemons = ArrayList<Pokemon>()

    private fun loadPokemon(){

        pokemons.add(
            Pokemon(
                "Charmander", "Charmander living in japan", 55.0,
                17.0,  -96.0, R.drawable.charmander
            )
        )
        pokemons.add(
            Pokemon(
                "Bulbasaur", "Bulbasaur living in usa", 90.5,
                18.0,  -97.0, R.drawable.bulbasaur
            )
        )
        pokemons.add(
            Pokemon(
                "Squirtle", "Squirtle living in iraq", 33.5,
                19.0,  -98.0, R.drawable.squirtle
            )
        )
    }

    // Inner classes

    private var location: Location? = null
    var oldLocation: Location? = null

    inner class PokeListener: LocationListener{

        constructor(){
            location = Location("Start")
            location!!.longitude = 0.0
            location!!.longitude = 0.0
        }
        override fun onLocationChanged(p0: Location?) {
            location = p0
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            //TODO("not implemented")
        }

        override fun onProviderEnabled(p0: String?) {
            // TODO("not implemented")
        }

        override fun onProviderDisabled(p0: String?) {
            //TODO("not implemented")
        }
    }

    inner class PokeThread: Thread{

        constructor():super(){
            oldLocation = Location("Start")
            oldLocation!!.longitude = 0.0
            oldLocation!!.longitude = 0.0
        }

        override fun run(){
            while (true){
                try {
                    if(oldLocation!!.distanceTo(location) == 0f){
                        continue
                    }

                    oldLocation = location

                    runOnUiThread {
                        mMap!!.clear()

                        // Load user location
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap!!.addMarker(MarkerOptions()
                            .position(sydney)
                            .title("Me")
                            .snippet("here is my location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario)))
                        //mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))

                        // Load pokemon location
                        for(i in 0 until pokemons.size){

                            val currentPokemon = pokemons[i]

                            if(currentPokemon.isCatch == false){

                                val pokemonLocation = LatLng(currentPokemon.location!!.latitude, currentPokemon.location!!.longitude)

                                mMap!!.addMarker(MarkerOptions()
                                    .position(pokemonLocation)
                                    .title(currentPokemon.name!!)
                                    .snippet(currentPokemon.description!! +", power:"+ currentPokemon!!.power)
                                    .icon(BitmapDescriptorFactory.fromResource(currentPokemon.image!!)))

                                if (location!!.distanceTo(currentPokemon.location) < 10) {
                                    currentPokemon.isCatch = true
                                    pokemons[i] = currentPokemon
                                    playerPower += currentPokemon.power!!
                                    Toast.makeText(
                                        applicationContext,
                                        "You catched a new pokemon your new power is $playerPower",
                                        Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    sleep(1000)
                }
                catch (ex:Exception){}
            }
        }
    }
}