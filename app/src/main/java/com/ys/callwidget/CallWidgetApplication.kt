package com.ys.callwidget

import android.app.Application

class CallWidgetApplication : Application(){
    val database: CallWidgetDatabase by lazy { CallWidgetDatabase.getDatabase(this) }

}