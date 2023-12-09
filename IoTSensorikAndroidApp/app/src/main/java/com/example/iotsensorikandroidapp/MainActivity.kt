package com.example.iotsensorikandroidapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iotsensorikandroidapp.ui.theme.IoTSensorikAndroidAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {

    private val channelID = "2374438"
    private val readAPIKey = "DG8UJP4LTUFD1GM6"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val temperatureState = remember { mutableStateOf("Loading...") }

            // Holen der Daten im Hintergrund und Aktualisieren des Zustands
            LaunchedEffect(true) {
                while (true) {
                    val data = fetchData()
                    displayData(data, temperatureState)
                    delay(3000) // 3000 Millisekunden = 3 Sekunden
                }
            }

            IoTSensorikAndroidAppTheme {
                // Zentrieren Sie die Temperaturanzeige und machen Sie sie größer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TemperatureShow(
                        temperature = temperatureState.value,
                        modifier = Modifier
                            .width(200.dp) // Ändere die Breite nach Bedarf
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(16.dp)
                    )
                }
            }
        }
    }

    private suspend fun fetchData(): String? {
        return try {
            withContext(Dispatchers.IO) {
                val url = URL("https://api.thingspeak.com/channels/$channelID/feeds.json?api_key=$readAPIKey")
                val connection = url.openConnection() as HttpURLConnection

                try {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    response.toString()
                } finally {
                    connection.disconnect()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun displayData(data: String?, temperatureState: MutableState<String>) {
        try {
            val jsonObject = JSONObject(data)
            val channelObject = jsonObject.getJSONObject("channel")
            val feedsArray = jsonObject.getJSONArray("feeds")

            if (feedsArray.length() > 0) {
                val lastEntry = feedsArray.getJSONObject(feedsArray.length() - 1)
                val temperature = lastEntry.getString("field1")

                // Aktualisieren des Zustands mit der neuen Temperatur
                temperatureState.value = "Temperature: $temperature"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

@Composable
fun TemperatureShow(temperature: String, modifier: Modifier = Modifier) {
    Text(
        text = temperature,
        modifier = modifier,
        fontSize = 24.sp,
        color = Color.White,
        textAlign = TextAlign.Center
    )
}
