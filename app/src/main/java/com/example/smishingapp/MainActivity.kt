package com.example.smishingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smishingapp.ui.MessageViewModel
import com.example.smishingapp.ui.SinglePermission
import com.example.smishingapp.ui.theme.SmishingAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmishingAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val messageViewModel:MessageViewModel = viewModel()
//                    messageViewModel.classifier = Classifier(this,"word_dict.json",100)
//                    messageViewModel.classifier.processVocab()
//                    messageViewModel.model = getFileFromAsset(this,"Modelfile.tflite")
//                    messageViewModel.interpreter = Interpreter(messageViewModel.model)
                    SinglePermission(messageViewModel)
                }
            }
        }
    }
}

//fun getFileFromAsset(context: Context, filename: String): File {
//    val file = File(context.cacheDir, filename)
//    context.assets.open(filename).use { inputStream ->
//        file.outputStream().use { outputStream ->
//            inputStream.copyTo(outputStream)
//        }
//    }
//    return file
//}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SmishingAppTheme {
        Greeting("Android")
    }
}