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
import dao.http.HttpMobileTower

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