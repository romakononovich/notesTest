package xyz.romakononovich.notes

import android.appwidget.AppWidgetProvider
import android.widget.Toast
import android.widget.RemoteViews
import android.app.PendingIntent
import android.content.Intent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import xyz.romakononovich.notes.Constants.ADD_WIDGET
import xyz.romakononovich.notes.Constants.DELETE_WIDGET
import xyz.romakononovich.notes.activity.AddNoteActivity
import xyz.romakononovich.notes.activity.LoginActivity


/**
 * Created by romank on 28.01.18.
 */
class WidgetActivity : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (i in 0 until appWidgetIds.size) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i])
        }
    }


    override fun onEnabled(context: Context) {
        val toast = Toast.makeText(context,ADD_WIDGET,Toast.LENGTH_SHORT)
        val v = toast.view.findViewById<TextView>(android.R.id.message)
        v.gravity = Gravity.CENTER
        toast.show()

    }

    override fun onDisabled(context: Context) {
        val toast = Toast.makeText(context, DELETE_WIDGET,Toast.LENGTH_SHORT)
        val v = toast.view.findViewById<TextView>(android.R.id.message)
        v.gravity = Gravity.CENTER
        toast.show()
    }

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            val views = RemoteViews(context.packageName, R.layout.activity_widget)
            // Create an Intent to launch Activity
            val intent = Intent(context, LoginActivity::class.java).putExtra("isWidget",true)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            views.setOnClickPendingIntent(R.id.widget, pendingIntent)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}