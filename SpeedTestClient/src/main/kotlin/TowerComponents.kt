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
import dao.http.HttpMobileTower
import dao.http.HttpUser

@Composable
fun ListTowers(){
    val towers = HttpMobileTower(sessionManager).getAll()
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
            items(towers){tower->
                TowersRow(tower)
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
fun TowersRow(passedTower: MobileTower){
    var tower by remember { (mutableStateOf(passedTower)) }
    var status by remember { mutableStateOf(1) }
    Column(
        modifier = Modifier.border(1.dp, Color.Black).padding(4.dp).fillMaxWidth(0.7f)
    ) {
        if (status == 1) {
            ShowTower(tower)
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { HttpMobileTower(sessionManager).delete(tower) },
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
            EditTower(tower) {
                status = 1
                tower = HttpMobileTower(sessionManager).getById(tower.id)!!
            }
        }
    }
}
@Composable
fun ShowTower(tower: MobileTower){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Id: ${tower.id}"
        )
        Text(
            text = "Location: ${tower.location.coordinates[1]} ${tower.location.coordinates[0]}"
        )
        Text(
            text = "Provider: ${tower.provider}"
        )
        Text(
            text = "Type: ${tower.type}"
        )
        Text(
            text = "Confirmed: ${tower.confirmed}"
        )
        if(tower.locator != null) {
            Text(
                text = "User: ${tower.locator!!.username}"
            )
        }

    }
}
@Composable
fun EditTower(
    tower: MobileTower,
    cancelEdit: () -> Unit
){
    var flagged by remember { mutableStateOf(false) }
    var statusLat by remember { mutableStateOf("") }
    var statusLon by remember { mutableStateOf("") }

    var lat by remember { mutableStateOf(tower.location.coordinates[1].toString()) }
    var lon by remember { mutableStateOf(tower.location.coordinates[0].toString()) }
    var latConverted by remember { mutableStateOf(tower.location.coordinates[1]) }
    var lonConverted by remember { mutableStateOf(tower.location.coordinates[0]) }

    var provider by remember { mutableStateOf(tower.provider) }
    var type by remember { mutableStateOf(tower.type) }
    var confirmed by remember { mutableStateOf(tower.confirmed) }

    val options = HttpUser(sessionManager).getAll().map {
        user: User ->
         Pair<String, User> (user.username, user)
    }
    var selectedOption by remember { mutableStateOf(options.first()) }
    var isSelectorOpen by remember { mutableStateOf(false) }

    Column {
        Row {
            Text(
                text = statusLat,
                modifier = Modifier.fillMaxWidth(0.5f),
                color = Color.Red
            )
            Text(
                text = statusLon,
                modifier = Modifier.fillMaxWidth(0.5f),
                color = Color.Red
            )
        }

        Row {
            OutlinedTextFieldWithLabel(
                value = lat,
                onValueChange = {
                    lat = it
                    try {
                        latConverted = lat.toDouble()
                        statusLat = ""
                        flagged = false
                    } catch (e: NumberFormatException){
                        statusLat = "Invalid Lat"
                        flagged = true
                    }
                                },
                label = "latitude",
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            OutlinedTextFieldWithLabel(
                value = lon,
                onValueChange = {
                    lon = it
                    try {
                        lonConverted = lon.toDouble()
                        statusLon = ""
                        flagged = false
                    } catch (e: NumberFormatException){
                        statusLon = "Invalid Lon"
                        flagged = true
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
            label = "provider",
            modifier = Modifier.fillMaxWidth(0.5f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextFieldWithLabel(
            value = type,
            onValueChange = { type = it },
            label = "type",
            modifier = Modifier.fillMaxWidth(0.5f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Conifrmed: "
            )
            Switch(
                checked = confirmed,
                onCheckedChange = {
                    confirmed = it
                    println(confirmed)
                }
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { isSelectorOpen = true }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
            }
            Text(selectedOption.first)
            DropdownMenu(
                expanded = isSelectorOpen,
                onDismissRequest = { isSelectorOpen = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(onClick = {
                        selectedOption = option
                        isSelectorOpen = false
                    }) {
                        Text(option.first)
                    }
                }
            }
        }
        Row {
            Button(
                onClick = {
                    if(!flagged) {
                        val updatedTower = MobileTower(
                            Location(coordinates = listOf(lonConverted, latConverted)),
                            provider,
                            type,
                            confirmed,
                            selectedOption.second,
                            tower.id
                        )
                        HttpMobileTower(sessionManager).update(updatedTower)
                        cancelEdit()
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.White)
            ) {
                Text("✓")
            }
            Button(
                onClick = cancelEdit,
                colors = ButtonDefaults.buttonColors(Color.White)
            ) {
                Text("x")
            }
        }
    }
}

@Composable
fun CreateTower(){
    var status by remember {  mutableStateOf("") }
    var flagged by remember { mutableStateOf(false) }
    var statusLat by remember { mutableStateOf("") }
    var statusLon by remember { mutableStateOf("") }

    var lat by remember { mutableStateOf("0.0") }
    var lon by remember { mutableStateOf("0.0") }
    var latConverted by remember { mutableStateOf(0.0) }
    var lonConverted by remember { mutableStateOf(0.0) }

    var provider by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var confirmed by remember { mutableStateOf(false) }

    val options = HttpUser(sessionManager).getAll().map {
            user: User ->
        Pair<String, User> (user.username, user)
    }
    var selectedOption by remember { mutableStateOf(options.first()) }
    var isSelectorOpen by remember { mutableStateOf(false) }

    Column {
        Row {
            Text(
                text = statusLat,
                modifier = Modifier.fillMaxWidth(0.5f),
                color = Color.Red
            )
            Text(
                text = statusLon,
                modifier = Modifier.fillMaxWidth(0.5f),
                color = Color.Red
            )
        }

        Row {
            OutlinedTextFieldWithLabel(
                value = lat,
                onValueChange = {
                    lat = it
                    try {
                        latConverted = lat.toDouble()
                        statusLat = ""
                        flagged = false
                    } catch (e: NumberFormatException){
                        statusLat = "Invalid Lat"
                        flagged = true
                    }
                    status = ""
                },
                label = "latitude",
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            OutlinedTextFieldWithLabel(
                value = lon,
                onValueChange = {
                    lon = it
                    try {
                        lonConverted = lon.toDouble()
                        statusLon = ""
                        flagged = false
                    } catch (e: NumberFormatException){
                        statusLon = "Invalid Lon"
                        flagged = true
                    }
                },
                label = "lontitude",
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextFieldWithLabel(
            value = provider,
            onValueChange = { provider = it; status = "" },
            label = "provider",
            modifier = Modifier.fillMaxWidth(0.5f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextFieldWithLabel(
            value = type,
            onValueChange = { type = it; status = "" },
            label = "type",
            modifier = Modifier.fillMaxWidth(0.5f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Conifrmed: "
            )
            Switch(
                checked = confirmed,
                onCheckedChange = {
                    confirmed = it
                    println(confirmed)
                }
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { isSelectorOpen = true }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
            }
            Text(selectedOption.first)
            DropdownMenu(
                expanded = isSelectorOpen,
                onDismissRequest = { isSelectorOpen = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(onClick = {
                        selectedOption = option
                        isSelectorOpen = false
                    }) {
                        Text(option.first)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = status
        )
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if(!flagged) {
                        val updatedTower = MobileTower(
                            Location(coordinates = listOf(lonConverted, latConverted)),
                            provider,
                            type,
                            confirmed,
                            selectedOption.second,
                        )
                        if(HttpMobileTower(sessionManager).insert(updatedTower)){
                            status = "Success"
                        } else {
                            status = "fail"
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(Color.White)
            ) {
                Text("✓")
            }

            Text(
                text = status
            )
        }
    }
}