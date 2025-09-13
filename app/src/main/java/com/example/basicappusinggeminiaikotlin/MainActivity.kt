package com.example.basicappusinggeminiaikotlin

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box // Added
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.basicappusinggeminiaikotlin.ui.theme.BasicAppUsingGeminiAIKotlinTheme
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BasicAppUsingGeminiAIKotlinTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ModelCall(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ModelCall(modifier: Modifier = Modifier) {
    var prompt by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val apiKey = BuildConfig.GEMINI_API_KEY
    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey
        )
    }

    // Scroll screen
    val responseScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Image(
            painter = painterResource(id = R.drawable.zeus),
            contentDescription = "Zeus ai",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .border(2.dp, Color.LightGray, CircleShape)
                .padding(4.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    spotColor = Color.Black
                )
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp)) // Added space after image

        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Ask me ..") },
            modifier = Modifier.fillMaxWidth(),

        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (prompt.isNotBlank()) {
                    isLoading = true
                    responseText = "" // Clear previous response
                    coroutineScope.launch {
                        try {
                            val response = generativeModel.generateContent(prompt)
                            responseText = response.text ?: "No text in response"
                        } catch (e: Exception) {
                            Log.e("GeminiAI", "Error generating content", e)
                            responseText = "Error: ${e.localizedMessage}"
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            enabled = !isLoading && prompt.isNotBlank()
        ) {
            Text("Start ⬆\uFE0F",
                modifier = Modifier.padding(start = 8.dp),
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Area for displaying response or loading indicator
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = responseText,
                    modifier = Modifier
                        .fillMaxSize() // Fill the Box
                        .verticalScroll(responseScrollState) // Make only the text scrollable
                        .padding(top = 8.dp) // Add some padding above the response text
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ModelCallPreview() {
    BasicAppUsingGeminiAIKotlinTheme {
        ModelCall()
    }
}
