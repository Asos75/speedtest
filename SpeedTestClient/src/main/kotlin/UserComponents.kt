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
import dao.http.HttpUser

@Composable
fun ListUsers(){
    val users = HttpUser(sessionManager).getAll()
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
            items(users){user->
                UserRow(user)
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
fun UserRow(passedUser: User){
    var user by remember { (mutableStateOf(passedUser)) }
    var status by remember { mutableStateOf(1) }
    Column(
        modifier = Modifier.border(1.dp, Color.Black).padding(4.dp).fillMaxWidth(0.7f)
    ) {
        if (status == 1) {
            ShowUser(user)
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { HttpUser(sessionManager).delete(user) },
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
            EditUser(user) {
                status = 1
                user = HttpUser(sessionManager).getById(user.id)!!
            }
        }
    }
}
@Composable
fun ShowUser(user: User){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Id: ${user.id}"
        )
        Text(
            text = "Username: ${user.username}"
        )
        Text(
            text = "Email: ${user.email}"
        )
        Text(
            text = "Admin: ${user.admin}"
        )

    }
}
@Composable
fun EditUser(
    user: User,
    cancelEdit: () -> Unit
){
    var username by remember { mutableStateOf(user.username) }
    var email by remember { mutableStateOf(user.email) }
    var admin by remember { mutableStateOf(user.admin) }

    Column {
        OutlinedTextFieldWithLabel(
            value = username,
            onValueChange = { username = it },
            label = "Username",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextFieldWithLabel(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Admin: "
            )
            Switch(
                checked = admin,
                onCheckedChange = {
                    admin = it
                }
            )
        }
        Row(){
            Button(
                onClick = {
                    val newUser = User(username, user.password, email, admin, user.id)
                    HttpUser(sessionManager).update(newUser)
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