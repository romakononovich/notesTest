package xyz.romakononovich.notes.activity

import android.appwidget.AppWidgetProvider
import android.widget.Toast
import android.widget.RemoteViews
import android.app.PendingIntent
import android.content.Intent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import xyz.romakononovich.notes.ADD_WIDGET
import xyz.romakononovich.notes.DELETE_WIDGET
import xyz.romakononovich.notes.R


/**
 * Created by romank on 28.01.18.
 */
class WidgetActivity : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach {
            updateAppWidget(context, appWidgetManager, it)
        }
    }


    override fun onEnabled(context: Context) {
        Toast.makeText(context, ADD_WIDGET, Toast.LENGTH_SHORT).apply {
            view.findViewById<TextView>(android.R.id.message).gravity = Gravity.CENTER
            show()
        }
    }

    override fun onDisabled(context: Context) {
        Toast.makeText(context, DELETE_WIDGET, Toast.LENGTH_SHORT).apply {
            view.findViewById<TextView>(android.R.id.message).gravity = Gravity.CENTER
            show()
        }
    }

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            val views = RemoteViews(context.packageName, R.layout.activity_widget)
            // Create an Intent to launch Activity
            val intent = Intent(context, LoginActivity::class.java).putExtra("isWidget", true)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            views.setOnClickPendingIntent(R.id.widget, pendingIntent)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}