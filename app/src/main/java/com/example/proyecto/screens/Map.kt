package com.example.proyecto.screens

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto.Components.CustomBottomBar
import com.example.proyecto.Components.StarRewards
import com.example.proyecto.Components.addStarsToUser
import com.example.proyecto.R
import com.example.proyecto.auth
import com.example.proyecto.database
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class RocketRadar(val id: Int, val center: LatLng, val radius: Double = 40.0)
data class PokemonEncounter(val id: Int, val name: String, val position: LatLng, val resId: Int)

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
    val eggHatched: Boolean = false,
    val isGameActive: Boolean = false,
    val radars: List<RocketRadar> = emptyList(),
    val isInsideRadar: Boolean = false,
    val stealthHealth: Float = 1.0f,
    val lastStepCount: Float = -1f,
    val gameFinished: Boolean = false,

    val showStarReward: Boolean = false,
    val starsEarned: Int = 0,
    val leveledUp: Boolean = false,
    val newLevel: Int = 1
)

class MapViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MapState())
    val uiState: StateFlow<MapState> = _uiState.asStateFlow()

    private val pokemonList = listOf(
        "Pichu" to R.drawable.pichu, "Squirtle" to R.drawable.squirtle,
        "Psyduck" to R.drawable.psyduck, "Rattata" to R.drawable.rattata,
        "Pidgey" to R.drawable.pidgey, "Nidoran" to R.drawable.nidoran,
        "Igglybuff" to R.drawable.igglybuff
    )

    fun onLuminosityChanged(value: Float) = _uiState.update { it.copy(luminosity = value) }

    fun processNewLocation(context: Context, location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        _uiState.update { it.copy(location = latLng) }
        if (_uiState.value.activePokemon == null && _uiState.value.canSpawn && !_uiState.value.isGameActive)
            spawnPokemonNear(latLng)
        if (_uiState.value.isGameActive) {
            checkRadarProximity(context, latLng)
            checkVictory(latLng)
        }
    }

    private fun spawnPokemonNear(userLoc: LatLng) {
        val rnd = pokemonList.random()
        _uiState.update {
            it.copy(
                activePokemon = PokemonEncounter(
                    id = (0..1000).random(), name = rnd.first,
                    position = LatLng(userLoc.latitude + (Math.random() - 0.5) / 2000,
                        userLoc.longitude + (Math.random() - 0.5) / 2000),
                    resId = rnd.second
                ), canSpawn = false
            )
        }
    }

    fun startCapture() = _uiState.update { it.copy(isCapturing = true, captureMessage = "¡AGITA EL CELULAR!") }


    fun checkShake(x: Float, y: Float, z: Float, uid: String?) {
        val acc = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        if (_uiState.value.isCapturing && acc > 45f) {
            val pokemon = _uiState.value.activePokemon
            _uiState.update { it.copy(captureMessage = "¡CAPTURADO!", isCapturing = false) }

            if (pokemon != null && uid != null) {
                database.getReference("users/$uid/pokemons")
                    .push().setValue(mapOf("nombre" to pokemon.name, "id" to pokemon.id))


                addStarsToUser(uid, StarRewards.POKEMON_CAPTURADO) { _, newLevel, leveledUp ->
                    _uiState.update {
                        it.copy(
                            showStarReward = true,
                            starsEarned = StarRewards.POKEMON_CAPTURADO,
                            leveledUp = leveledUp,
                            newLevel = newLevel
                        )
                    }
                }
            }
        }
    }

    fun onStepsChanged(totalSteps: Float) {
        _uiState.update { state ->
            val first = if (state.startSteps == -1f) totalSteps else state.startSteps
            val current = totalSteps - first
            val hatched = current >= state.stepsToHatch && !state.eggHatched
            var stealth = state.stealthHealth
            var finished = state.gameFinished
            if (state.isGameActive && state.isInsideRadar && state.lastStepCount != -1f && totalSteps > state.lastStepCount) {
                stealth -= 0.15f
                if (stealth <= 0f) finished = true
            }
            state.copy(startSteps = first, currentSteps = current,
                eggHatched = state.eggHatched || hatched,
                stealthHealth = stealth.coerceAtLeast(0f),
                gameFinished = finished, lastStepCount = totalSteps)
        }
    }

    fun onMapLongClick(context: Context, point: LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            val geo = Geocoder(context, Locale.getDefault())
            val address = try { geo.getFromLocation(point.latitude, point.longitude, 1)?.get(0)?.getAddressLine(0) }
            catch (e: Exception) { null } ?: "Destino"
            val userLoc = _uiState.value.location
            val radars = mutableListOf<RocketRadar>()
            if (userLoc != null) for (i in 1..3) {
                radars.add(RocketRadar(i, LatLng(
                    userLoc.latitude + (point.latitude - userLoc.latitude) * (i * 0.25),
                    userLoc.longitude + (point.longitude - userLoc.longitude) * (i * 0.25)
                )))
            }
            _uiState.update {
                it.copy(longClickLocation = point, longClickAddress = address,
                    radars = radars, isGameActive = true,
                    stealthHealth = 1.0f, gameFinished = false)
            }
        }
    }

    private fun checkRadarProximity(context: Context, userLoc: LatLng) {
        var detected = false
        _uiState.value.radars.forEach { radar ->
            val res = FloatArray(1)
            Location.distanceBetween(userLoc.latitude, userLoc.longitude, radar.center.latitude, radar.center.longitude, res)
            if (res[0] < radar.radius) detected = true
        }
        if (detected && !_uiState.value.isInsideRadar) triggerVibration(context)
        _uiState.update { it.copy(isInsideRadar = detected) }
    }


    private fun checkVictory(userLoc: LatLng) {
        val target = _uiState.value.longClickLocation ?: return
        val res = FloatArray(1)
        Location.distanceBetween(userLoc.latitude, userLoc.longitude, target.latitude, target.longitude, res)
        if (res[0] < 15f && _uiState.value.isGameActive) {
            val uid = auth.currentUser?.uid
            if (uid != null && _uiState.value.stealthHealth > 0f) {
                addStarsToUser(uid, StarRewards.INFILTRACION_EXITOSA) { _, newLevel, leveledUp ->
                    _uiState.update {
                        it.copy(isGameActive = false, gameFinished = true,
                            showStarReward = true, starsEarned = StarRewards.INFILTRACION_EXITOSA,
                            leveledUp = leveledUp, newLevel = newLevel)
                    }
                }
            } else {
                _uiState.update { it.copy(isGameActive = false, gameFinished = true) }
            }
        }
    }

    private fun triggerVibration(context: Context) {
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) v.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
        else v.vibrate(400)
    }

    fun dismissStarReward() = _uiState.update { it.copy(showStarReward = false) }
    fun resetEncounter() {
        _uiState.update { it.copy(activePokemon = null, captureMessage = "", showStarReward = false) }
        viewModelScope.launch { delay(8000); _uiState.update { it.copy(canSpawn = true) } }
    }
    fun resetEgg() = _uiState.update { it.copy(eggHatched = false, startSteps = -1f, currentSteps = 0f) }
    fun resetGame() = _uiState.update { it.copy(isGameActive = false, gameFinished = false, radars = emptyList(), showStarReward = false) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, viewModel: MapViewModel = viewModel()) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val cameraPositionState = rememberCameraPositionState()

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
    LaunchedEffect(Unit) {
        permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.VIBRATE))
    }

    DisposableEffect(Unit) {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val listener = object : SensorEventListener {
            override fun onSensorChanged(e: SensorEvent?) {
                e?.let {
                    when (it.sensor.type) {
                        Sensor.TYPE_LIGHT -> viewModel.onLuminosityChanged(it.values[0])
                        Sensor.TYPE_ACCELEROMETER -> viewModel.checkShake(it.values[0], it.values[1], it.values[2], auth.currentUser?.uid)
                        Sensor.TYPE_STEP_COUNTER -> viewModel.onStepsChanged(it.values[0])
                    }
                }
            }
            override fun onAccuracyChanged(s: Sensor?, a: Int) {}
        }
        sm.registerListener(listener, sm.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_UI)
        sm.registerListener(listener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME)
        sm.registerListener(listener, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), SensorManager.SENSOR_DELAY_UI)
        onDispose { sm.unregisterListener(listener) }
    }

    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    DisposableEffect(Unit) {
        val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 4000).build()
        val cb = object : LocationCallback() {
            override fun onLocationResult(r: LocationResult) { r.lastLocation?.let { viewModel.processNewLocation(context, it) } }
        }
        try { fusedClient.requestLocationUpdates(req, cb, context.mainLooper) } catch (e: SecurityException) {}
        onDispose { fusedClient.removeLocationUpdates(cb) }
    }

    LaunchedEffect(state.location) { state.location?.let { cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 17f)) } }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pokémon Map", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (state.isInsideRadar) Color.Red else Color(0xFF5db3f5),
                    titleContentColor = Color.White)
            )
        },
        bottomBar = { CustomBottomBar(navController) }
    ) { pv ->
        Box(modifier = Modifier.fillMaxSize().padding(pv)) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapLongClick = { viewModel.onMapLongClick(context, it) },
                properties = MapProperties(isMyLocationEnabled = true,
                    mapStyleOptions = if (state.luminosity < 50) MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark) else null),
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            ) {
                state.activePokemon?.let { poke ->
                    Marker(state = rememberMarkerState(position = poke.position),
                        title = "¡Un ${poke.name} salvaje!",
                        icon = BitmapDescriptorFactory.fromResource(poke.resId),
                        onClick = { viewModel.startCapture(); true })
                }
                state.longClickLocation?.let {
                    Marker(state = rememberMarkerState(position = it), title = "Destino",
                        snippet = state.longClickAddress,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    state.location?.let { loc ->
                        Polyline(points = listOf(loc, it),
                            color = if (state.isInsideRadar) Color.Red else Color.Cyan, width = 10f)
                    }
                }
                state.radars.forEach { radar ->
                    Circle(center = radar.center, radius = radar.radius,
                        fillColor = if (state.isInsideRadar) Color.Red.copy(0.4f) else Color.Red.copy(0.1f),
                        strokeColor = Color.Red, strokeWidth = 5f)
                }
            }


            Column(modifier = Modifier.padding(16.dp)) {
                Text("Luz: ${state.luminosity.toInt()} lx",
                    modifier = Modifier.background(Color.White.copy(0.6f)).padding(4.dp), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.background(Color.White.copy(0.8f)).padding(8.dp)) {
                    Text("Huevo 🥚", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    LinearProgressIndicator(progress = { (state.currentSteps / state.stepsToHatch).coerceIn(0f, 1f) },
                        modifier = Modifier.width(100.dp), color = Color(0xFF4CAF50))
                    Text("${state.currentSteps.toInt()} / ${state.stepsToHatch.toInt()} pas", fontSize = 10.sp)
                }
                if (state.isGameActive) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(modifier = Modifier.background(Color.Black.copy(0.7f)).padding(8.dp)) {
                        Text("Sigilo 👤", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        LinearProgressIndicator(progress = { state.stealthHealth },
                            modifier = Modifier.width(100.dp), color = Color.Red)
                    }
                }
            }


            if (state.isCapturing || state.captureMessage == "¡CAPTURADO!") {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.85f)),
                    contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        state.activePokemon?.let {
                            Image(painter = painterResource(it.resId), contentDescription = null, modifier = Modifier.size(220.dp))
                        }
                        Text(state.captureMessage, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)


                        if (state.captureMessage == "¡CAPTURADO!" && state.showStarReward) {
                            Spacer(modifier = Modifier.height(12.dp))
                            StarRewardBanner(state.starsEarned, state.leveledUp, state.newLevel)
                        }

                        if (state.captureMessage == "¡CAPTURADO!") {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.resetEncounter() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))) {
                                Text("Continuar")
                            }
                        }
                    }
                }
            }


            if (state.eggHatched) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Cyan.copy(0.9f)), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("¡ECLOSIONÓ!", fontSize = 32.sp, fontWeight = FontWeight.Black)
                        Image(painter = painterResource(R.drawable.pichu), contentDescription = null, modifier = Modifier.size(200.dp))
                        Button(onClick = { viewModel.resetEgg() }) { Text("¡Genial!") }
                    }
                }
            }


            if (state.gameFinished) {
                val victoria = state.stealthHealth > 0f
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.9f)), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Text(
                            if (victoria) "¡INFILTRACIÓN EXITOSA!" else "¡TE CAPTURARON!",
                            color = if (victoria) Color(0xFF4CAF50) else Color.Red,
                            fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
                        )
                        if (victoria && state.showStarReward) {
                            Spacer(modifier = Modifier.height(12.dp))
                            StarRewardBanner(state.starsEarned, state.leveledUp, state.newLevel)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(onClick = { viewModel.resetGame() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))) {
                            Text("Cerrar")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun StarRewardBanner(stars: Int, leveledUp: Boolean, newLevel: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFFFFF8E1), shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 28.dp, vertical = 14.dp)
    ) {
        Text(
            text = "+ $stars ⭐",
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFFFA000)
        )
        if (leveledUp) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "🎉 ¡SUBISTE AL NIVEL $newLevel!",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0),
                textAlign = TextAlign.Center
            )
        }
    }
}