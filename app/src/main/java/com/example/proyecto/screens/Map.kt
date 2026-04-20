package com.example.proyecto.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto.Components.CustomBottomBar
import com.example.proyecto.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale



data class PokemonEncounter(
    val id: Int,
    val name: String,
    val position: LatLng,
    val resId: Int
)

data class MapState(
    val location: LatLng? = null,
    val luminosity: Float = 0f,
    val longClickLocation: LatLng? = null,
    val longClickAddress: String = "",
    val activePokemon: PokemonEncounter? = null,
    val isCapturing: Boolean = false,
    val captureMessage: String = "",
    val canSpawn: Boolean = true,

    val startSteps: Float = -1f,
    val currentSteps: Float = 0f,
    val stepsToHatch: Float = 2500f,
    val eggHatched: Boolean = false
)



class MapViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MapState())
    val uiState: StateFlow<MapState> = _uiState.asStateFlow()

    private val pokemonList = listOf(
        "Pichu" to R.drawable.pichu,
        "Squirtle" to R.drawable.squirtle,
        "Psyduck" to R.drawable.psyduck,
        "Rattata" to R.drawable.rattata,
        "Pidgey" to R.drawable.pidgey,
        "Nidoran" to R.drawable.nidoran,
        "Igglybuff" to R.drawable.igglybuff
    )

    fun onLuminosityChanged(value: Float) {
        _uiState.update { it.copy(luminosity = value) }
    }

    fun processNewLocation(context: Context, location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        _uiState.update { it.copy(location = latLng) }


        if (_uiState.value.activePokemon == null && _uiState.value.canSpawn) {
            spawnPokemonNear(latLng)
        }
    }

    private fun spawnPokemonNear(userLoc: LatLng) {
        val randomPoke = pokemonList.random()
        val latOffset = (Math.random() - 0.5) / 2000
        val lngOffset = (Math.random() - 0.5) / 2000

        val encounter = PokemonEncounter(
            id = (0..1000).random(),
            name = randomPoke.first,
            position = LatLng(userLoc.latitude + latOffset, userLoc.longitude + lngOffset),
            resId = randomPoke.second
        )
        _uiState.update { it.copy(activePokemon = encounter, canSpawn = false) }
    }

    fun startCapture() {
        _uiState.update { it.copy(isCapturing = true, captureMessage = "¡AGITA EL CELULAR!") }
    }

    fun checkShake(x: Float, y: Float, z: Float) {
        val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()


        if (_uiState.value.isCapturing && acceleration > 45f) {
            _uiState.update { it.copy(captureMessage = "¡CAPTURADO!", isCapturing = false) }
        }
    }

    fun onStepsChanged(totalSteps: Float) {
        if (_uiState.value.startSteps == -1f) {
            _uiState.update { it.copy(startSteps = totalSteps) }
        }
        val stepsSinceStart = totalSteps - _uiState.value.startSteps
        _uiState.update { it.copy(currentSteps = stepsSinceStart) }

        if (stepsSinceStart >= _uiState.value.stepsToHatch && !_uiState.value.eggHatched) {
            _uiState.update { it.copy(eggHatched = true) }
        }
    }

    fun resetEncounter() {
        _uiState.update { it.copy(activePokemon = null, captureMessage = "") }
        viewModelScope.launch {
            delay(10000)
            _uiState.update { it.copy(canSpawn = true) }
        }
    }

    fun resetEgg() {
        _uiState.update { it.copy(eggHatched = false, startSteps = -1f, currentSteps = 0f) }
    }

    fun onMapLongClick(context: Context, point: LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val address = try {
                geocoder.getFromLocation(point.latitude, point.longitude, 1)?.get(0)?.getAddressLine(0)
            } catch (e: Exception) { null } ?: "Punto marcado"

            _uiState.update { it.copy(longClickLocation = point, longClickAddress = address) }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, viewModel: MapViewModel = viewModel()) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val cameraPositionState = rememberCameraPositionState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION
        ))
    }


    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    when (it.sensor.type) {
                        Sensor.TYPE_LIGHT -> viewModel.onLuminosityChanged(it.values[0])
                        Sensor.TYPE_ACCELEROMETER -> viewModel.checkShake(it.values[0], it.values[1], it.values[2])
                        Sensor.TYPE_STEP_COUNTER -> viewModel.onStepsChanged(it.values[0])
                    }
                }
            }
            override fun onAccuracyChanged(s: Sensor?, a: Int) {}
        }
        sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(listener, accelSensor, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(listener, stepSensor, SensorManager.SENSOR_DELAY_UI)
        onDispose { sensorManager.unregisterListener(listener) }
    }


    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    DisposableEffect(Unit) {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { viewModel.processNewLocation(context, it) }
            }
        }
        try {
            fusedLocationClient.requestLocationUpdates(request, callback, context.mainLooper)
        } catch (e: SecurityException) {}
        onDispose { fusedLocationClient.removeLocationUpdates(callback) }
    }

    LaunchedEffect(state.location) {
        state.location?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 17f))
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pokémon Map", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF5db3f5),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = { CustomBottomBar(navController) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapLongClick = { viewModel.onMapLongClick(context, it) },
                properties = MapProperties(
                    isMyLocationEnabled = true,
                    mapStyleOptions = if (state.luminosity < 50)
                        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
                    else null
                ),
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            ) {

                state.activePokemon?.let { poke ->
                    Marker(
                        state = rememberMarkerState(position = poke.position),
                        title = "¡Un ${poke.name} salvaje!",
                        icon = BitmapDescriptorFactory.fromResource(poke.resId),
                        onClick = {
                            viewModel.startCapture()
                            true
                        }
                    )
                }


                state.longClickLocation?.let { point ->
                    Marker(
                        state = rememberMarkerState(position = point),
                        title = "Destino",
                        snippet = state.longClickAddress,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                }

                if (state.location != null && state.longClickLocation != null) {
                    Polyline(
                        points = listOf(state.location!!, state.longClickLocation!!),
                        color = Color.Red,
                        width = 10f
                    )
                }
            }


            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Luz: ${state.luminosity.toInt()} lx",
                    modifier = Modifier.background(Color.White.copy(0.6f)).padding(4.dp),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .background(Color.White.copy(0.8f))
                        .padding(8.dp)
                ) {
                    Text("Huevo 🥚", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    LinearProgressIndicator(
                        progress = { (state.currentSteps / state.stepsToHatch).coerceIn(0f, 1f) },
                        modifier = Modifier.width(100.dp),
                        color = Color(0xFF4CAF50)
                    )
                    Text("${state.currentSteps.toInt()} / ${state.stepsToHatch.toInt()} pas", fontSize = 10.sp)
                }
            }


            if (state.isCapturing || state.captureMessage == "¡CAPTURADO!") {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        state.activePokemon?.let {
                            Image(
                                painter = painterResource(id = it.resId),
                                contentDescription = null,
                                modifier = Modifier.size(250.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = state.captureMessage,
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        if (state.captureMessage == "¡CAPTURADO!") {
                            Spacer(modifier = Modifier.height(30.dp))
                            Button(
                                onClick = { viewModel.resetEncounter() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5db3f5))
                            ) {
                                Text("Continuar viaje")
                            }
                        }
                    }
                }
            }


            if (state.eggHatched) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Cyan.copy(0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("¡ECLOSIONÓ!", fontSize = 32.sp, fontWeight = FontWeight.Black)
                        Image(
                            painter = painterResource(R.drawable.pichu),
                            contentDescription = null,
                            modifier = Modifier.size(200.dp)
                        )
                        Button(onClick = { viewModel.resetEgg() }) {
                            Text("¡Genial!")
                        }
                    }
                }
            }
        }
    }
}