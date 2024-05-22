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
import dao.http.HttpMeasurement
import dao.http.HttpUser
import speedTest.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun ListMeasurements(){
    val measurments = HttpMeasurement(sessionManager).getAll()
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
            items(measurments){measurement->
                MeasurementRow(measurement)
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
fun MeasurementRow(passedMeasurment: Measurment){

    var measurement by remember { (mutableStateOf(passedMeasurment)) }
    var status by remember { mutableStateOf(1) }
    Column(
        modifier = Modifier.border(1.dp, Color.Black).padding(4.dp).fillMaxWidth(0.7f)
    ) {
        if (status == 1) {
            ShowMeasurement(measurement)
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { HttpMeasurement(sessionManager).delete(measurement) },
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
            EditMeasurement(measurement) {
                status = 1
                measurement = HttpMeasurement(sessionManager).getById(measurement.id)!!
            }
        }
    }
}

@Composable
fun ShowMeasurement(measurment: Measurment){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Id: ${measurment.id}"
        )
        Text(
            text = "Speed: ${measurment.speed}"
        )
        Text(
            text = "Type: ${measurment.type}"
        )
        Text(
            text = "Provider: ${measurment.provider}"
        )
        Text(
            text = "Location: ${measurment.location.coordinates[1]} ${measurment.location.coordinates[0]}"
        )
        Text(
            text = "Time: ${measurment.time}"
        )
        if(measurment.user != null) {
            Text(
                text = "User: ${measurment.user!!.username}"
            )
        }
    }
}

@Composable
fun EditMeasurement(
    measurment: Measurment,
    cancelEdit: () -> Unit
){

    var status by remember { mutableStateOf("") }
    var speedError by remember { mutableStateOf(false) }
    var speed by remember { mutableStateOf(measurment.speed.toString()) }
    var speedConverted by remember { mutableStateOf(measurment.speed) }

    val options = listOf("data", "wifi")
    var selectedOption by remember { mutableStateOf(options.first()) }
    var isSelectorOpen by remember { mutableStateOf(false) }

    var provider by remember { mutableStateOf(measurment.provider) }

    var lat by remember { mutableStateOf(measurment.location.coordinates[1].toString()) }
    var lon by remember { mutableStateOf(measurment.location.coordinates[0].toString()) }
    var latConverted by remember { mutableStateOf(measurment.location.coordinates[1]) }
    var lonConverted by remember { mutableStateOf(measurment.location.coordinates[0]) }
    var latError by remember { mutableStateOf(false) }
    var lonError by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

    var dateInput by remember { mutableStateOf(measurment.time.format(dateFormatter)) }
    var timeInput by remember { mutableStateOf(measurment.time.format(timeFormatter)) }
    var dateError by remember { mutableStateOf(false) }
    var timeError by remember { mutableStateOf(false) }

    var user by remember { mutableStateOf(measurment.user != null) }
    val userOptions = HttpUser(sessionManager).getAll().map {
            user: User ->
        Pair<String, User> (user.username, user)
    }

    var defaultUserOption = userOptions.first()
    if(measurment.user != null) {
        var selectedUser = measurment.user

        defaultUserOption = userOptions.find { it.second.username == selectedUser?.username } ?: userOptions.first()
    }
    var userSelectedOption by remember { mutableStateOf(defaultUserOption) }
    var isUserSelectorOpen by remember { mutableStateOf(false) }

    Column {
        Text(
            text = status,
            color = Color.Red
        )
        OutlinedTextFieldWithLabel(
            value = speed,
            onValueChange = {
                speed = it
                speedError = try {
                    speedConverted = speed.toLong()
                    status = ""
                    false
                } catch (e: NumberFormatException){
                    status = "Invalid speed"
                    true
                }
                            },
            label = "Email",
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { isSelectorOpen = true }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
            }
            Text(text = selectedOption)
            DropdownMenu(
                expanded = isSelectorOpen,
                onDismissRequest = { isSelectorOpen = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(onClick = {
                        selectedOption = option
                        isSelectorOpen = false
                    }) {
                        Text(option)
                    }
                }
            }
        }
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
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextFieldWithLabel(
            value = provider,
            onValueChange = { provider = it },
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
                text = "User: "
            )
            Switch(
                checked = user,
                onCheckedChange = {
                    user = it
                }
            )
            if(user) {
                IconButton(onClick = { isUserSelectorOpen = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                }
                Text(userSelectedOption.first)
                DropdownMenu(
                    expanded = isUserSelectorOpen,
                    onDismissRequest = { isUserSelectorOpen = false },
                ) {
                    userOptions.forEach { option ->
                        DropdownMenuItem(onClick = {
                            userSelectedOption = option
                            isUserSelectorOpen = false
                        }) {
                            Text(option.first)
                        }
                    }
                }
            }

        }
        Row{
            Button(
                onClick = {
                    if(
                        !latError &&
                        !lonError &&
                        !speedError &&
                        !dateError &&
                        !timeError
                    ) {
                        val date = LocalDate.parse(dateInput, dateFormatter)
                        val time = LocalTime.parse(timeInput, timeFormatter)
                        val updatedMeasurement = Measurment(
                            speedConverted,
                            Type.valueOf(selectedOption),
                            provider,
                            Location(coordinates = listOf(lonConverted, latConverted)),
                            LocalDateTime.of(date, time),
                            if(!user) null else userSelectedOption.second,
                            measurment.id
                        )
                        HttpMeasurement(sessionManager).update(updatedMeasurement)
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