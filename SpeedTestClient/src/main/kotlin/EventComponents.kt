import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dao.http.HttpEvent

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