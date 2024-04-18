package com.example.budgetbuddy.utils

import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.budgetbuddy.R
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.TimeZone


/**
 * Método para agregar el [Gasto] al calendario en caso de cumplir los requisitos
 * y de haber un calendario válido.
 */
/**             (Requisito opcional)           **/
fun agregarGastoAlCalendario(
    context: Context,
    user: String,
    titulo: String,
    descripcion: String,
    fechaL: Long
) {
    val contentResolver: ContentResolver = context.contentResolver
    val fecha = fechaL.toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    var calendarIds = obtenerIdsCalendarioXEmail(context, user) // Se busca la cuenta del usuario
    if (calendarIds.isEmpty()) calendarIds = obtenerIdsCalendarioLOCAL(context) // Si no tiene, los locales
    if (calendarIds.isEmpty()) calendarIds = obtenerIdsCalendario(context) // Si no hay (Pixel), busca todos los disponibles

    if(calendarIds.isEmpty()){
        // El evento no se pudo añadir por falta de calendarios de tipo LOCAL
        // Los calendarios de Google no son editables mediante el uso de ContentProviders.
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, context.getString(R.string.no_calendar), Toast.LENGTH_SHORT).show()
        }
    }else{
        if (fechaL > LocalDate.now().toLong()){
            for (calendarId in calendarIds){
                val timeZone = TimeZone.getDefault().id

                val values = ContentValues().apply {
                    put(CalendarContract.Events.DTSTART, fecha)
                    put(CalendarContract.Events.DTEND, fecha + (60 * 60 * 1000)) // Duración de 1 hora
                    put(CalendarContract.Events.ALL_DAY, 1) // Evento de todo el día
                    put(CalendarContract.Events.TITLE, titulo)
                    put(CalendarContract.Events.DESCRIPTION, descripcion)
                    put(CalendarContract.Events.CALENDAR_ID, calendarId)
                    put(CalendarContract.Events.EVENT_TIMEZONE, timeZone) // Agregar el campo eventTimezone
                }

                val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
                uri?.let {
                    // El evento se insertó correctamente
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, context.getString(R.string.evento_added), Toast.LENGTH_SHORT).show()
                    }
                } ?: run {
                    // Ocurrió un error al insertar el evento
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, context.getString(R.string.calendar_add_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }
}

/**
 * Obtiene los ID de todos los calencarios
 */
@SuppressLint("Range")
fun obtenerIdsCalendario(context: Context): List<Long> {

    val calendarIds = mutableListOf<Long>()
    val projection = arrayOf(CalendarContract.Calendars._ID)

    // Filtrar los calendarios por tipo de cuenta
//    val selection = "(${CalendarContract.Calendars.ACCOUNT_TYPE} IN (?) OR (${CalendarContract.Calendars.ACCOUNT_TYPE} NOT IN (?) AND ${CalendarContract.Calendars.ACCOUNT_NAME} = ?))"
//    val selectionArgs = arrayOf("LOCAL", "com.google", user)

    // Consultar los calendarios
    context.contentResolver.query(
        CalendarContract.Calendars.CONTENT_URI,
        projection,
        null,
        null,
        null
    )?.use { cursor ->
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID))
            Log.d("CALENDARIO", id.toString())
            calendarIds.add(id)
        }
    }
    Log.d("CALENDARIO", calendarIds.toString())
    return calendarIds
}

/**
 * Busca los ID de los calendarios de google que tengan como propietario el correo del [currentUser]
 */
@SuppressLint("Range")
fun obtenerIdsCalendarioXEmail(context: Context, user: String): List<Long> {

    val calendarIds = mutableListOf<Long>()
    val projection = arrayOf(CalendarContract.Calendars._ID)

    // Filtrar los calendarios por tipo de cuenta
    val selection = "${CalendarContract.Calendars.ACCOUNT_TYPE} IN (?) AND ${CalendarContract.Calendars.ACCOUNT_NAME} = ?"
    val selectionArgs = arrayOf("com.google", user)

    // Consultar los calendarios
    context.contentResolver.query(
        CalendarContract.Calendars.CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )?.use { cursor ->
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID))
            Log.d("CALENDARIO GOOGLE", id.toString())
            calendarIds.add(id)
        }
    }

    return calendarIds
}

/**
 * Busca los ID de los calendarios LOCALES
 */
@SuppressLint("Range")
fun obtenerIdsCalendarioLOCAL(context: Context): List<Long> {

    val calendarIds = mutableListOf<Long>()
    val projection = arrayOf(CalendarContract.Calendars._ID)

    // Filtrar los calendarios por tipo de cuenta
    val selection = "${CalendarContract.Calendars.ACCOUNT_TYPE} IN (?)"
    val selectionArgs = arrayOf("LOCAL")

    // Consultar los calendarios
    context.contentResolver.query(
        CalendarContract.Calendars.CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )?.use { cursor ->
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID))
            Log.d("CALENDARIO LOCAL", id.toString())
            calendarIds.add(id)
        }
    }

    return calendarIds
}



// Redundante con los AlarmManager

//fun agregarReminder(context: Context, eventoId: Long, minutos: Int) {
//    val contentResolver: ContentResolver = context.contentResolver
//
//    val reminderValues = ContentValues().apply {
//        put(CalendarContract.Reminders.EVENT_ID, eventoId)
//        put(CalendarContract.Reminders.MINUTES, minutos) // minutos antes del evento para mostrar el recordatorio
//        put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT) // Método de recordatorio
//    }
//
//    val uri: Uri? = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
//    uri?.let { insertedUri ->
//        val reminderId = ContentUris.parseId(insertedUri)
//        Log.d("Reminder", "Recordatorio insertado con ID: $reminderId")
//    }
//}
