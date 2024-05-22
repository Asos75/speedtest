import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dao.http.HttpEvent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun ListEvents(){
    val events = HttpEvent(sessionManager).getAll()
    val scrollState = rememberLazyListState(0)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 12.dp, bottom = 12.dp),
        contentAlignment = Alignment.Center
    ){
        LazyColumn (
            state = scrollState
        ){
            items(events){event->
                EventRow(event)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState)
        )
    }
}
@Composable
fun EventRow(passedEvent: Event){
    var event by remember { (mutableStateOf(passedEvent)) }
    var status by remember { mutableStateOf(1) }
    Column(
        modifier = Modifier.border(1.dp, Color.Black).padding(4.dp).fillMaxWidth(0.7f)
    ) {
        if (status == 1) {
            ShowEvent(event)
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { HttpEvent(sessionManager).delete(event) },
                    colors = ButtonDefaults.buttonColors(Color.White)
                ) {
                    Text("\uD83D\uDDD1")
                }
                Button(
                    onClick = { status = 0},
                    colors = ButtonDefaults.buttonColors(Color.White)
                ) {
                    Text("\uD83D\uDD8A")
                }
            }
        } else {
            EditEvent(event) {
                status = 1
                event = HttpEvent(sessionManager).getById(event.id)!!
            }
        }
    }
}
@Composable
fun ShowEvent(event: Event){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Id: ${event.id}"
        )
        Text(
            text = "Name: ${event.name}"
        )
        Text(
            text = "Type: ${event.type}"
        )
        Text(
            text = "Time: ${event.time}"
        )
        Text(
            text = "Online: ${event.online}"
        )
        if(!event.online){
            Text(
                text = "Location: ${event.location!!.coordinates[1]} ${event.location!!.coordinates[0]}"
            )
        }

    }
}
@Composable
fun EditEvent(
    event: Event,
    cancelEdit: () -> Unit
){
    var status by remember { mutableStateOf("") }
    var name by remember{ mutableStateOf(event.name) }
    var type by remember { mutableStateOf(event.type) }

    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

    var dateInput by remember { mutableStateOf(event.time.format(dateFormatter)) }
    var timeInput by remember { mutableStateOf(event.time.format(timeFormatter)) }
    var dateError by remember { mutableStateOf(false) }
    var timeError by remember { mutableStateOf(false) }

    var online by remember{ mutableStateOf(event.online) }

    var lat by remember { mutableStateOf(if (!online) event.location!!.coordinates[1].toString() else "0.0") }
    var lon by remember { mutableStateOf(if (!online) event.location!!.coordinates[0].toString() else "0.0") }
    var latConverted by remember { mutableStateOf(if (!online) event.location!!.coordinates[1] else 0.0) }
    var lonConverted by remember { mutableStateOf(if (!online) event.location!!.coordinates[0] else 0.0) }

    var latError by remember { mutableStateOf(false) }
    var lonError by remember { mutableStateOf(false) }

    Column {
        Text(
            text = status,
            color = Color.Red
        )
        OutlinedTextFieldWithLabel(
            value = name,
            onValueChange = { name = it },
            label = "Email",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextFieldWithLabel(
            value = type,
            onValueChange = { type = it },
            label = "Email",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            OutlinedTextFieldWithLabel(
                value = dateInput,
                onValueChange = {
                    dateInput = it
                    dateError = try {
                        LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
                        status = ""
                        false
                    } catch (e: DateTimeParseException) {
                        status = "Invalid Date"
                        true
                    }
                },
                label = "Date (YYYY-MM-DD)",
                modifier = Modifier.fillMaxWidth(0.5f),
            )

            OutlinedTextFieldWithLabel(
                value = timeInput,
                onValueChange = {
                    timeInput = it
                    timeError = try {
                        LocalTime.parse(it, DateTimeFormatter.ISO_LOCAL_TIME)
                        status = ""
                        false
                    } catch (e: DateTimeParseException) {
                        status = "Invalid Time"
                        true
                    }
                },
                label = "Time (HH:MM:SS)",
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Online: "
            )
            Switch(
                checked = online,
                onCheckedChange = {
                    online = it
                }
            )


        }
        if(!online) {
            Row {
                OutlinedTextFieldWithLabel(
                    value = lat,
                    onValueChange = {
                        lat = it
                        latError = try {
                            latConverted = lat.toDouble()
                            status = ""
                            false
                        } catch (e: NumberFormatException){
                            status = "Invalid Lat"
                            true
                        }
                    },
                    label = "latitude",
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
                OutlinedTextFieldWithLabel(
                    value = lon,
                    onValueChange = {
                        lon = it
                        lonError = try {
                            lonConverted = lon.toDouble()
                            status = ""
                            false
                        } catch (e: NumberFormatException){
                            status = "Invalid Lon"
                            true
                        }
                    },
                    label = "lontitude",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Row{
            Button(
                onClick = {
                    if(
                        !latError &&
                        !lonError &&
                        !dateError &&
                        !timeError
                    ) {
                        val date = LocalDate.parse(dateInput, dateFormatter)
                        val time = LocalTime.parse(timeInput, timeFormatter)
                        val updatedEvent = Event(
                            name,
                            type,
                            LocalDateTime.of(date, time),
                            online,
                            if(!online) Location(coordinates = listOf(lonConverted, latConverted)) else null,
                            event.id
                        )
                        HttpEvent(sessionManager).update(updatedEvent)
                        cancelEdit()
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.White)
            ){
                Text("✓")
            }
            Button(
                onClick = cancelEdit,
                colors = ButtonDefaults.buttonColors(Color.White)
            ){
                Text("x")
            }
        }
    }
}