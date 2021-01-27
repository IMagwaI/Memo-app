package com.example.myapplication

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.widget.RemoteViews
import androidx.room.RoomMasterTable
import com.example.myapplication.beans.Note
import com.example.myapplication.localdb.DbManager
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val intent = Intent(context.applicationContext, MemoQuotesService::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        context.startService(intent)

        // Maitenant je vais plus faire update manuellement ici , puisque j'ai crée un nouveau service , je lance l'update depuis ce dernier.
//        for (appWidgetId in appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId)
//        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    companion object {

        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val widgetdata = WidgetData(context)

            val widgetText = widgetdata.getMemoCount().toString()
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.new_app_widget)
            views.setTextViewText(R.id.appwidget_text, widgetText)

            views.setOnClickPendingIntent(
                R.id.addnote_button,
                GotoAddNote(context)
            )
            views.setOnClickPendingIntent(
                R.id.viewnote_btn,
                GotoView(context)
            )
            views.setTextViewText(R.id.memo_quote, getRandomQuote(context))

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun GotoView(context: Context): PendingIntent {

            val intent = Intent(context, MainActivity::class.java)
            return PendingIntent.getActivity(context, 0, intent, 0)
        }

        private fun GotoAddNote(context: Context): PendingIntent {
            val intent = Intent(context, AddActivity::class.java)
            return PendingIntent.getActivity(context, 0, intent, 0)
        }
        private fun getRandomQuote(context: Context): String {
            val quotes = context.resources.getStringArray(R.array.memo_texts)
            val rand = Math.random() * quotes.size
            return quotes[rand.toInt()].toString()
        }
    }
}