package com.example.budgetbuddy.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

@SuppressLint("Range")
fun obtenerIdsCalendario(context: Context): List<Long> {
    val calendarIds = mutableListOf<Long>()
    val projection = arrayOf(CalendarContract.Calendars._ID)

    // Filtrar los calendarios por tipo de cuenta
    val selection = "${CalendarContract.Calendars.ACCOUNT_TYPE} NOT IN (?)"
    val selectionArgs = arrayOf("com.google")

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
            calendarIds.add(id)
        }
    }

    return calendarIds
}

fun agregarGastoAlCalendario(
    context: Context,
    titulo: String,
    descripcion: String,
    fechaL: Long
) {
    val contentResolver: ContentResolver = context.contentResolver

    val fecha = fechaL.toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

    val calendarIds = obtenerIdsCalendario(context)

    if (fechaL > LocalDate.now().toLong()){
        for (calendarId in calendarIds) {
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
                val eventoId = ContentUris.parseId(it)
                // El evento se insertó correctamente
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Evento agregado al calendario", Toast.LENGTH_SHORT).show()
                    agregarReminder(context, eventoId, 480)
                }
            } ?: run {
                // Ocurrió un error al insertar el evento
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Error al agregar evento al calendario", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

fun agregarReminder(context: Context, eventoId: Long, minutos: Int) {
    val contentResolver: ContentResolver = context.contentResolver

    val reminderValues = ContentValues().apply {
        put(CalendarContract.Reminders.EVENT_ID, eventoId)
        put(CalendarContract.Reminders.MINUTES, minutos) // minutos antes del evento para mostrar el recordatorio
        put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT) // Método de recordatorio
    }

    val uri: Uri? = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
    uri?.let { insertedUri ->
        val reminderId = ContentUris.parseId(insertedUri)
        Log.d("Reminder", "Recordatorio insertado con ID: $reminderId")
    }
}



//// Configuración de la autenticación
//val credential = GoogleAccountCredential.usingOAuth2(context, listOf(CalendarScopes.CALENDAR))
//credential.selectedAccount = Account("tu_correo@gmail.com", "com.google")
//
//// Inicialización del cliente de la API de Google Calendar
//val service = Calendar.Builder(
//    AndroidHttp.newCompatibleTransport(),
//    JacksonFactory.getDefaultInstance(),
//    credential
//)
//    .setApplicationName("TuAppName")
//    .build()
//
//// Crear evento
//val event = Event()
//event.summary = "Título del evento"
//event.description = "Descripción del evento"
//// Establecer la fecha y hora del evento
//val startDateTime = DateTime("2024-04-06T10:00:00-07:00")
//val start = EventDateTime().setDateTime(startDateTime)
//event.start = start
//val endDateTime = DateTime("2024-04-06T10:30:00-07:00")
//val end = EventDateTime().setDateTime(endDateTime)
//event.end = end
//
//// Insertar el evento en el calendario
//val insertedEvent = service.events().insert("primary", event).execute()
//val eventId = insertedEvent.id
