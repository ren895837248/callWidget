package com.ys.callwidget

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ys.callwidget.ui.theme.CallwidgetTheme


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        //pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageAndVideo))
        super.onCreate(savedInstanceState)

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        setContent {
            CallwidgetTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Ttt(pickMedia)
                }
            }
        }
    }



    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Ttt(pickMedia:ActivityResultLauncher<PickVisualMediaRequest>, modifier: Modifier = Modifier.fillMaxSize()) {

        Column(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Button(
                onClick = {

                },
            ) {
                Text(text = "选择联系人")
            }

            Spacer(modifier = Modifier)
            var phoneNumber by remember { (mutableStateOf("")) }

            Surface(onClick = {
                //val intent = Intent(MediaStore.ACTION_PICK_IMAGES)



// Include only one of the following calls to launch(), depending on the types
// of media that you want to let the user choose from.

// Launch the photo picker and let the user choose images and videos.
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))

            }) {
                Image(
                    painter = ColorPainter(Color(0xFF0000FF)),
                    contentDescription = "",
                    modifier = Modifier.height(
                        400.dp
                    ),

                    )
            }


            Spacer(modifier = Modifier)

            Column(
                verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                Button(onClick = {

                }) {
                    Text(text = "保存")
                }
            }


        }


    }


    //@Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        //ActivityResultContracts.StartActivityForResult(intent, PHOTO_PICKER_REQUEST_CODE)

        CallwidgetTheme {

            //Ttt()
        }
    }






}




