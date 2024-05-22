import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dao.http.HttpMeasurement

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
    Column {
        //TODO add textfields
        Row(){
            Button(
                onClick = {
                    //TODO add cal to update
                    cancelEdit()
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