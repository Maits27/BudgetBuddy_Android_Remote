package com.example.budgetbuddy.VM

import android.location.Location
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetbuddy.Data.Enumeration.AppLanguage
import com.example.budgetbuddy.Data.Room.Gasto
import com.example.budgetbuddy.Data.Room.GastoDia
import com.example.budgetbuddy.Data.Room.GastoTipo
import com.example.budgetbuddy.Data.Repositories.IGastoRepository
import com.example.budgetbuddy.Data.Enumeration.TipoGasto
import com.example.budgetbuddy.widgets.Widget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/********************************************************
 ****                 App View Model                 ****
 ********************************************************/
/**
 * View Model de Hilt para los datos del usuario
 * Encargado de las interacciones entre el frontend de la app y el repositorio [gastoRepository] que realiza los cambios en ROOM.
 *
 * @gastoRepository: implementación de [IGastoRepository] y repositorio a cargo de realizar los cambios en la BBDD.
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    private val gastoRepository: IGastoRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /*************************************************
     **                    Estados                  **
     *************************************************/
    var currentUser by mutableStateOf( "")

    // Flows a los que les llega constantemente las actualizaciones y datos de la BBDD.
    // De esta forma no es necesaria una actualización cada vez que se realice un cambio.

    private val _fecha = MutableStateFlow(LocalDate.now())

    val fecha: Flow<LocalDate> = _fecha

    val listadoGastos = gastoRepository.todosLosGastos(currentUser)

    val listadoGastosFecha: (LocalDate)-> Flow<List<Gasto>> = { gastoRepository.elementosFecha(it, currentUser) }

    val listadoGastosMes: (LocalDate)-> Flow<List<GastoDia>> = { sacarDatosMes(it, gastoRepository.todosLosGastos(currentUser)) }

    val listadoGastosTipo: (LocalDate)-> Flow<List<GastoTipo>> = { sacarDatosPorTipo(it, gastoRepository.todosLosGastos(currentUser)) }

    val totalGasto: (LocalDate)-> Flow<Double> = { gastoRepository.gastoTotalDia(it, currentUser) }

    var facturaActual: (LocalDate, AppLanguage)->  Flow<String> = { data, idioma->
        listadoGastosFecha(data).map { listaGastos ->
            listaGastos.fold("") { f, gasto -> f + "\t- " + gasto.toString(idioma) }
        }
    }
    private val _locationFlow = MutableStateFlow<Location?>(null)

    val locationFlow: Flow<Location?> = _locationFlow
    /*************************************************
     **          Inicialización de la BBDD          **
     *************************************************/

//    init {
//        viewModelScope.launch {
//            for (cantidad in 1 until 10){
//                Log.d("BD-Preloading", "Loading cantidad $cantidad")
//                añadirGasto( "Gasto Inicial 2$cantidad", 10.0*cantidad, LocalDate.of(2024,3, cantidad), TipoGasto.Comida)
//                añadirGasto( "Gasto Inicial 5$cantidad", 4.0*cantidad, LocalDate.of(2024,3, cantidad+20), TipoGasto.Ropa)
//                añadirGasto( "Gasto Inicial 4$cantidad", 5.0*cantidad, LocalDate.of(2024,3, cantidad+10), TipoGasto.Hogar)
//                añadirGasto( "Gasto Inicial 6$cantidad", 10.0*cantidad, LocalDate.of(2024,4, cantidad), TipoGasto.Transporte)
//                añadirGasto( "Gasto Inicial 7$cantidad", 4.0*cantidad, LocalDate.of(2024,4, cantidad+20), TipoGasto.Comida)
//                añadirGasto( "Gasto Inicial 8$cantidad", 5.0*cantidad, LocalDate.of(2024,4, cantidad+10), TipoGasto.Actividad)
//                añadirGasto( "Gasto Inicial 9$cantidad", 1.0*cantidad, LocalDate.now(), TipoGasto.Otros)
//            }
//        }
//    }

    /*************************************************
     **                    Eventos                  **
     *************************************************/


    ////////////////////// Añadir y eliminar elementos //////////////////////

    suspend fun añadirGasto(nombre: String, cantidad: Double, fecha: LocalDate, tipo: TipoGasto, latitud: Double, longitud: Double): Gasto {
        val gasto = Gasto(nombre, cantidad, fecha, tipo, latitud, longitud, currentUser)
        try {
            gastoRepository.insertGasto(gasto)
        }catch (e: Exception){
            Log.d("BASE DE DATOS!", e.toString())
        }
        return gasto
    }

    suspend fun borrarGasto(gasto: Gasto){
        gastoRepository.deleteGasto(gasto)
    }

    ////////////////////// Editar elementos //////////////////////


    fun cambiarFecha(nuevoValor: LocalDate) {
        _fecha.value = nuevoValor

    }
    fun cambiarLocalizacion(location: Location?) {
        _locationFlow.value = location

    }
    fun editarGasto(gasto_previo: Gasto, nombre:String, cantidad: Double, fecha:LocalDate, tipo: TipoGasto, latitud: Double, longitud: Double){
        gastoRepository.editarGasto(Gasto(nombre, cantidad, fecha, tipo, latitud, longitud, currentUser, gasto_previo.id))
    }

    ////////////////////// Pasar a formato String //////////////////////

    fun escribirFecha(fecha: LocalDate): String {
        return "${fecha.dayOfMonth}/${fecha.monthValue}/${fecha.year}"
    }

    fun escribirMesyAño(fecha: LocalDate): String {
        return "${fecha.monthValue}/${fecha.year}"
    }

    fun fecha_txt(fecha: LocalDate): String {
        return "${fecha.dayOfMonth}_${fecha.monthValue}_${fecha.year}"
    }

    ////////////////////// Recopilar datos gráficos //////////////////////

    fun sacarDatosMes(fecha: LocalDate, listadoGastos: Flow<List<Gasto>>): Flow<List<GastoDia>>{
        Log.d("CURRENT USER", currentUser)
        val gastosFechados = listadoGastos.map{
            it.filter { gasto ->
                Log.d("gasto inicial", "$gasto")
                gasto.fecha.year == fecha.year }
            .filter { gasto ->  gasto.fecha.monthValue == fecha.monthValue }
        }
        val gastosAgrupados = gastosFechados.map {
            it.groupBy { gasto -> gasto.fecha }.map { (fecha, gastos) ->
                GastoDia(
                    cantidad = gastos.sumByDouble { it.cantidad },
                    fecha = fecha
                )
            }
        }
        return gastosAgrupados
    }

    fun sacarDatosPorTipo(fecha: LocalDate, listadoGastos: Flow<List<Gasto>>): Flow<List<GastoTipo>>{
        val gastosFechados = listadoGastos.map{
            it.filter { gasto -> gasto.fecha.year == fecha.year }
            .filter { gasto ->  gasto.fecha.monthValue == fecha.monthValue }
            .filter { gasto ->  gasto.userId == currentUser }
        }
        val gastosAgrupados = gastosFechados.map {
            it.groupBy { gasto -> gasto.tipo }.map { (tipo, gastos) ->
                GastoTipo(
                    cantidad = gastos.sumByDouble { it.cantidad },
                    tipo = tipo
                )
            }
        }
        return gastosAgrupados
    }
    fun download_user_data(){
        viewModelScope.launch {
            gastoRepository.download_user_data(currentUser)
        }
    }
//    suspend fun erase_user_data(){
//        gastoRepository.deleteUserData(currentUser)
//    }
    private fun recogerTodosLosGastos(): Flow<List<Gasto>>{
        return gastoRepository.todosLosGastos(currentUser)
    }
    fun upload_user_data(currentUser:String, onConfirm: ()-> Unit){
        CoroutineScope(Dispatchers.IO).launch {
            try {
//                gastoRepository.deleteUserData(currentUser)
                val gastos = recogerTodosLosGastos().first()
                gastoRepository.uploadUserData(
                    currentUser,
                    gastos
                )
            } catch (_: Exception) {
                onConfirm()
            }
            onConfirm()
        }
    }
}




