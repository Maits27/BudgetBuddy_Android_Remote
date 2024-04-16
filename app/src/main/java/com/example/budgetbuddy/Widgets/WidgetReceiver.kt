package com.example.budgetbuddy.Widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.budgetbuddy.Repositories.GastoRepository
import com.example.budgetbuddy.Repositories.ILoginSettings
import com.example.budgetbuddy.Local.Data.CompactGasto
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class WidgetReceiver : GlanceAppWidgetReceiver() {
    /*************************************************
     **                  Attributes                 **
     *************************************************/

    //-----------------   Widget   -----------------//
    override val glanceAppWidget: GlanceAppWidget = Widget()

    private val coroutineScope = MainScope()

    //-------------   Repositorios   --------------//
    @Inject
    lateinit var gastoRepository: GastoRepository
    @Inject
    lateinit var userRepository: ILoginSettings
    /*************************************************
     **                    Events                   **
     *************************************************/

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        Log.d("Widget", "onUpdate Called")
        observeData(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Log.d("Widget", "onReceive Called; Action: ${intent.action}")

        // We filter actions in order to prevent updating twice in onUpdate events.
        if (intent.action == UPDATE_ACTION || intent.action.equals("ACTION_TRIGGER_LAMBDA")) {
            observeData(context)
        }
    }


    /*************************************************
     **                Datos y Update               **
     *************************************************/

    private fun observeData(context: Context) {
        coroutineScope.launch {
            Log.d("Widget", "Coroutine Called")

            // Get last logged user or null
            val currentUsername = userRepository.getLastLoggedUser()?:""

            // If there's  a user get it's visits
            val gastos = if (currentUsername != "") {
                gastoRepository.elementosFecha(LocalDate.now(), currentUsername).first().map(::CompactGasto)
            } else emptyList()


            Log.d("Widget", "Coroutine - Data-Length: ${gastos.size}")

            // Get all the widget IDs and update them
            GlanceAppWidgetManager(context).getGlanceIds(Widget::class.java).forEach { glanceId ->
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { widgetDataStore ->
                    widgetDataStore.toMutablePreferences().apply {

                        // If there's a user update data.
                        if (currentUsername != "") {
                            this[currentUserKey] = currentUsername
                            this[todayGastoDataKey] = Json.encodeToString(gastos)
                        }

                        // If there's no user clear all data
                        else this.clear()

                    }
                }
            }

            // Force widget update
            glanceAppWidget.updateAll(context)
        }
    }


    /************************************************
     ****              Constantes                ****
     ************************************************/

    companion object {

        const val UPDATE_ACTION = "updateAction"

        val currentUserKey = stringPreferencesKey("currentUser")
        val todayGastoDataKey = stringPreferencesKey("data")
    }
}