package com.ys.callwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ClipData.Item
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ys.callwidget.ui.theme.CallwidgetTheme

class CallWidgetConfActivity : ComponentActivity() {

    private val viewModel: CallWidgetViewModel by viewModels() {
        CallWidgetViewModel.CallWidgetViewModelFactory(
            (application as CallWidgetApplication).database
                .userDao()
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("widgetCOnf", "conf")






        setContent {
            CallwidgetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LazyColumn {
                        val itemList: List<User>? = viewModel.listAll()
                        if (itemList != null) {
                            itemsIndexed(itemList) { index, item ->
                                Surface(onClick = {
                                    oncomplete(item)
                                }) {
                                    Text(text = "${item.name}")
                                }
                            }
                        }
                    }
                }
            }
        }


    }

    fun oncomplete(user: User) {
        var appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID


        val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(applicationContext)
        RemoteViews(applicationContext.packageName, R.layout.call_widget).also { views ->
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            putExtra("picture", user.picture)
            putExtra("phone", user.phone)
        }
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }
}
