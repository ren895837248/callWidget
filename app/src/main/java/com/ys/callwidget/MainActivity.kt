package com.ys.callwidget

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.FileUtils
import android.os.PersistableBundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import android.provider.MediaStore
import android.util.Log
import android.util.TimeUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.ys.callwidget.ui.theme.CallwidgetTheme
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream


class MainActivity : ComponentActivity() {

//    private val viewModel: CallWidgetViewModel by activityViewModels {
//        InventoryViewModelFactory(
//            (activity?.application as InventoryApplication).database
//                .itemDao()
//        )
//    }

    private val viewModel: CallWidgetViewModel by viewModels(){
        CallWidgetViewModel.CallWidgetViewModelFactory(
            (application as CallWidgetApplication).database
                .userDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    fun checkPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_CONTACTS
            ) == PackageManager.PERMISSION_DENIED
        ) {
            var requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
                    println(granted)
                }
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.READ_CONTACTS
                )
            )
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            var requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
                    println(granted)
                }
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                )
            )
        }


    }

    var contactsLaunch: ActivityResultLauncher<Intent>? = null
    var pickMedia :ActivityResultLauncher<PickVisualMediaRequest>?= null
    fun createContactsLaunch() {
        contactsLaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (result != null) {
                        val intent = result.data
                        if (intent == null) {
                            return@registerForActivityResult
                        }
                        val contactUri: Uri = intent.data!!
                        val projection: Array<String> =
                            arrayOf(
                                ContactsContract.CommonDataKinds.Phone.NUMBER,
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                            )
                        contentResolver.query(contactUri, projection, null, null, null)
                            .use { cursor ->
                                // If the cursor returned is valid, get the phone number
                                if (cursor != null && cursor.moveToFirst()) {
                                    var numberIndex =
                                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    val number = cursor.getString(numberIndex)
                                    numberIndex =
                                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                                    val name = cursor.getString(numberIndex)
                                    Log.d("phonec", number)
                                    Log.d("name", name)
                                    viewModel.phone.setValue(number);
                                    viewModel.name.setValue(name);
                                }
                            }
                    }

                }


            }
    }


    fun pickMediaLaunch() {
        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    val dirRoot = applicationContext.filesDir.absoluteFile
                    val pciDir ="$dirRoot/callWidgetPic"
                    if(!File(pciDir).exists()){
                        val success = File(pciDir).mkdirs()
                        println(success)
                    }


                    val fileDescirptor = contentResolver.openFileDescriptor(uri,"r")
                    if (fileDescirptor != null) {
                        val inputStrem =  contentResolver.openInputStream(uri)
                        if(inputStrem!=null){
                            val fileName = System.currentTimeMillis()
                            val outFile = File("$pciDir/$fileName")
                            outFile.createNewFile()
                            IOUtils.copy(inputStrem,FileOutputStream(outFile))
                            viewModel.personPicPath.setValue(outFile.absolutePath)
                        }

                    }

                    viewModel.personPicUri.setValue(uri)
                    Log.d("PhotoPicker", "Selected URI: $uri")
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        viewModel.listAll()
        checkPermission()

        createContactsLaunch()

        pickMediaLaunch()
        setContent {

            var name by remember { (mutableStateOf("")) }
            var phone by remember { (mutableStateOf("")) }
            var uri  by remember { (mutableStateOf(Uri.EMPTY)) }

            val nameOb = Observer<String> { newName ->
                Log.d("nameob", newName)
                name = newName

            }
            viewModel.name.observe(this, nameOb)

            val phoneOb = Observer<String> { newPhone ->
                Log.d("newPhone", newPhone)
                phone = newPhone
            }
            viewModel.phone.observe(this, phoneOb)

            val uriOb = Observer<Uri> { newUri ->
                //Log.d("newPhone", newUri)
                uri = newUri
            }
            viewModel.personPicUri.observe(this, uriOb)


            CallwidgetTheme {


                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Ttt( name, phone,uri)
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Ttt(
        name: String, phone: String,uri:Uri,
        modifier: Modifier = Modifier.fillMaxSize(),
    ) {

        Column(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Button(
                onClick = {
                    Log.d("xunze", "xun")
                    var intent = Intent(Intent.ACTION_PICK).apply {
                        type = CommonDataKinds.Phone.CONTENT_TYPE
                    }
                    contactsLaunch?.launch(intent)
                },
            ) {
                Text(text = "选择联系人")
            }
            Row {


                Text(text = name, fontSize = 20.sp)
                Text(text = phone, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier)
            var phoneNumber by remember { (mutableStateOf("")) }

            Surface(onClick = {
                //val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
                // Include only one of the following calls to launch(), depending on the types
                // of media that you want to let the user choose from.

                // Launch the photo picker and let the user choose images and videos.
                pickMedia?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))

            }) {
                val dirRoot = Environment.getExternalStorageDirectory().absoluteFile
                val pciDir ="$dirRoot/callWidgetPic"
                if(!File(pciDir).exists()){
                    File(pciDir).mkdirs()
                }
                if (uri !=null && uri.path!=null && uri.path!!.length >0){
                    val fileDescirptor = contentResolver.openFileDescriptor(uri,"r")
                    if (fileDescirptor != null) {
                        val bitmap = BitmapFactory.decodeFileDescriptor(fileDescirptor.fileDescriptor)
                        Image(painter = BitmapPainter(image = bitmap.asImageBitmap()),contentDescription = "",
                            modifier = Modifier.height(
                                400.dp
                            ),
                            contentScale = ContentScale.Fit,
                            )
                    };

                }else{
                    Image(
                        painter = ColorPainter(Color(0xFF0000FF)),
                        contentDescription = "",
                        modifier = Modifier.height(
                            400.dp
                        ),

                        )
                }



            }


            Spacer(modifier = Modifier)

            Column(
                verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                Button(onClick = {
                    viewModel.saveUser()
                    Toast.makeText(applicationContext,"保存成功",Toast.LENGTH_LONG)
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




