import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import dao.mongodb.MongoUser
import dslCity.ForForeachFFFAutomaton
import dslCity.Parser
import dslCity.Scanner
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import org.bson.types.ObjectId
import java.awt.FileDialog
import java.awt.Frame
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.time.LocalDateTime
import speedTest.*
import util.*
import kotlin.concurrent.thread

var conn : MongoDatabase? = null

@Composable
fun Navigation(
    onDataClicked: () -> Unit,
    onMeasureClicked: () -> Unit,
    onTowerClicked: () -> Unit,
    onAboutAppClicked: () -> Unit,
    onDslCityClicked: () -> Unit,
    onScraperClicked: () -> Unit,
    onGeneratorClicked: () -> Unit
) {
    Surface(
        modifier = Modifier.padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.30f)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier.clickable(onClick = onMeasureClicked).fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(7.dp),
                    text = "⚡Measure speed",
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier.clickable(onClick = onTowerClicked).fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(7.dp),
                    text = "\uD83D\uDDFCTower Confirm",
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier.clickable(onClick = onDataClicked).fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(7.dp),
                    text = "\uD83D\uDDC4Data",
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier.clickable(onClick = onDslCityClicked).fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(7.dp),
                    text = "✎ DSLCity Editor",
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier.clickable(onClick = onScraperClicked).fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(7.dp),
                    text = "\uD83C\uDF10 Scraper",
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier.clickable(onClick = onGeneratorClicked).fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(7.dp),
                    text = "\uD83D\uDD27 Generator",
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.weight(3.0f))
            Box(
                modifier = Modifier.clickable(onClick = onAboutAppClicked).fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(7.dp),
                    text = "ⓘ About app",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun Content(currentContent: @Composable () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.fillMaxSize().then(modifier)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            currentContent()
        }
    }
}

var speedglobal = 0L
var globalProgress = 0f
var globalLocation = Location(coordinates = listOf(0.0, 0.0))
var globalProvider = ""
@Composable
fun Measure(

) {
    var speed by remember { mutableStateOf(speedglobal) }
    var currentProgress by remember { mutableStateOf(globalProgress) }
    var location by remember { mutableStateOf(globalLocation) }
    var provider by remember { mutableStateOf(globalProvider) }

    Column(
        modifier = Modifier.fillMaxHeight().fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(

                onClick = {
                    thread {
                        globalProgress = 0f
                        currentProgress = globalProgress
                        //This code is 1 to 1 copy of measure cycle function of SpeedTest.kt but applied here to allow increasing progress bar over time
                        val speedTest = SpeedTest()
                        val results = Array<Long>(10) { 0 }
                        for (i in 0..<10) {
                            results[i] = speedTest.measure()
                            println(results[i])
                            globalProgress += 0.1f
                            currentProgress = globalProgress
                        }
                        var sum = 0L
                        for (i in 3..<10) {
                            sum += results[i]
                        }
                        speedglobal = sum / 7
                        println("Final $speedglobal")
                        speed = speedglobal

                        try {
                            globalLocation = LocationUtil.getLocation()
                        } catch (e: RuntimeException) {
                            println(e)
                        }

                        try {
                            globalProvider = IPInfoUtil.getProvider()
                        } catch (e: RuntimeException) {
                            println(e)
                        }

                        location = globalLocation
                        provider = globalProvider

                    }
                },
            ) {
                Text(
                    text = "Measure speed"
                )
            }
            LinearProgressIndicator(
                progress = currentProgress,
                modifier = Modifier.fillMaxWidth(0.8f),
            )
        }

        Text(
            text = "Measured speed = $speed",
            modifier = Modifier.fillMaxWidth().wrapContentSize()
        )

        Text(
            text = "(${location.coordinates[0]}, ${location.coordinates[1]})",
            modifier = Modifier.fillMaxWidth().wrapContentSize()
        )

        Text(
            text = "Provider: $provider",
            modifier = Modifier.fillMaxWidth().wrapContentSize()
        )
    }
}

@Composable
fun Towers() {
    Text(
        text = "You are viewing invoices tab.",
        modifier = Modifier.fillMaxSize().wrapContentSize()
    )
}


@Composable
fun Data(){
    val options = if(conn != null) runBlocking {  DatabaseUtil.listAllCollection(conn!!).toList().toTypedArray() } else arrayOf("No options")

    var selectedOption by remember { mutableStateOf(options.first()) }
    var isSelectorOpen by remember { mutableStateOf(false) }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { isSelectorOpen = true }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
            }
            Text(selectedOption)
        }
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
    Text(
        text = "You are viewing data tab.",
        modifier = Modifier.fillMaxSize().wrapContentSize()
    )
}

@Composable
fun EditorNavbar(
    loadFromFile: () -> Unit,
    saveToFile: () -> Unit,
    saveToFileAs: () -> Unit,
    runCode: () -> Unit,
    alphaStatus: Float
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.clickable(onClick = loadFromFile)
        ) {
            Text(
                modifier = Modifier
                    .padding(7.dp),
                text = "Load",
                textAlign = TextAlign.Center
            )
        }
        Box(
            modifier = Modifier.clickable(onClick = saveToFile)
        ) {
            Text(
                modifier = Modifier
                    .padding(7.dp),
                text = "Save",
                textAlign = TextAlign.Center
            )
        }
        Box(
            modifier = Modifier.clickable(onClick = saveToFileAs)
        ) {
            Text(
                modifier = Modifier
                    .padding(7.dp),
                text = "Save as",
                textAlign = TextAlign.Center
            )
        }
        Box(
        ) {
            Text(
                modifier = Modifier
                    .padding(7.dp).alpha(alphaStatus),
                text = "Saved",
                textAlign = TextAlign.Center

            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier.clickable(onClick = runCode)
        ) {
            Text(
                modifier = Modifier
                    .padding(7.dp),
                text = "Run",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun Editor() {
    var text by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }
    var currentFilePath by remember { mutableStateOf("") }
    var alphaStatus by remember { mutableStateOf(0f) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        EditorNavbar(
            loadFromFile = {
                val fileDialog = FileDialog(Frame(), "Select File", FileDialog.LOAD)
                fileDialog.isVisible = true
                currentFilePath = fileDialog.directory + fileDialog.file

                try {
                    text = File(currentFilePath).readText()
                } catch (e: FileNotFoundException) {
                    output += "\nFile not found"
                }
                alphaStatus = 0f

            },
            saveToFile = {
                try {
                    File(currentFilePath).writeText(text)
                    alphaStatus = 0.7f
                } catch (e: FileNotFoundException) {
                    output += "\nFile not found\n"
                }

            },
            saveToFileAs = {
                val fileDialog = FileDialog(Frame(), "Select File", FileDialog.LOAD)
                fileDialog.isVisible = true
                val newFilePath = fileDialog.directory + fileDialog.file
                println(newFilePath)
                if (newFilePath != null && !File(newFilePath).exists()) {
                    File(newFilePath).createNewFile()
                }
                File(newFilePath).writeText(text)
                alphaStatus = 0.7f
            },
            runCode = {
                output += "@" + LocalDateTime.now() + "\n"
                val out = ByteArrayOutputStream()
                try{
                    Parser(Scanner(ForForeachFFFAutomaton, text.toByteArray().inputStream())).parse().eval(out)
                    output += String(out.toByteArray())
                } catch (e : Exception){
                    output += e
                } catch (e : Error){
                    output += e
                }

            },
            alphaStatus = alphaStatus
        )
        TextField(
            value = text,
            onValueChange = {

                    newText ->
                run {
                    text = newText.replace("\t", " ")
                }
                alphaStatus = 0f
            },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = MaterialTheme.shapes.small
                ).horizontalScroll(rememberScrollState()),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),

            maxLines = Int.MAX_VALUE,
            textStyle = LocalTextStyle.current.copy(
                fontSize = MaterialTheme.typography.body1.fontSize
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            )
        )
        Text(
            text = "Output:",
            style = MaterialTheme.typography.body1,
        )
        TextField(
            value = output,
            onValueChange = {
            },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = MaterialTheme.shapes.small
                ).horizontalScroll(rememberScrollState()),
            maxLines = Int.MAX_VALUE,
            textStyle = LocalTextStyle.current.copy(
                fontSize = MaterialTheme.typography.body1.fontSize
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            )
        )


    }
}


@Composable
fun Scraper() {
    Text(
        text = "You are viewing scraper tab.",
        modifier = Modifier.fillMaxSize().wrapContentSize()
    )
}

@Composable
fun GeneratorNavbar(
    generateToCSV: () -> Unit,
    generateToMongo: () -> Unit,
    alphaStatus: Float
) {
    val options = listOf("CSV", "MongoDB")
    var selectedOption by remember { mutableStateOf(options.first()) }
    var isSelectorOpen by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { isSelectorOpen = true }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
            }
            Text(selectedOption)
        }
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
        Box(
        ) {
            Text(
                modifier = Modifier
                    .padding(7.dp).alpha(alphaStatus),
                text = "Generated",
                textAlign = TextAlign.Center

            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier.clickable(onClick = {
                when (selectedOption) {
                    "CSV" -> {
                        generateToCSV()
                    }

                    "MongoDB" -> {
                        generateToMongo()
                    }
                }
            })
        ) {
            Text(
                modifier = Modifier
                    .padding(7.dp),
                text = "Generate",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun standardTextField(modVal: String){

}

var userOptions = mutableListOf<Pair<String, ObjectId>>()
@Composable
fun Generator() {
    var alphaStatus by remember { mutableStateOf(0f) }

    //SPEED
    var minValue by remember { mutableStateOf("50000") }
    var maxValue by remember { mutableStateOf("100000") }

    //TYPE
    val options = listOf("Data", "WiFi")
    var selectedOption by remember { mutableStateOf(options.first()) }
    var isSelectorOpen by remember { mutableStateOf(false) }

    //OPERATOR
    var operator by remember { mutableStateOf("") }

    //LOCATION
    var lat1 by remember { mutableStateOf("") }
    var lon1 by remember { mutableStateOf("") }
    var lat2 by remember { mutableStateOf("") }
    var lon2 by remember { mutableStateOf("") }
    var locationMarker1 by remember { mutableStateOf( Location(coordinates = listOf(0.0, 0.0))) }
    var locationMarker2 by remember { mutableStateOf( Location(coordinates = listOf(0.0, 0.0))) }

    //USER
    val mongoUser = MongoUser(conn)
    if(userOptions.isEmpty()) userOptions = runBlocking {
        val users = mongoUser.getAll()
        users.map { user ->
            user.username to user.id
        }.toMutableList()
    }
    var selectedUser by remember { mutableStateOf(userOptions.first()) }
    var isUserSelectorOpen by remember { mutableStateOf(false) }

    //COUNT
    var count by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        GeneratorNavbar(
            generateToCSV = {
                try {
                    GeneratorUtil.generateToCSV(
                        minValue.toLong(),
                        maxValue.toLong(),
                        if (selectedOption == "Data") Type.data else Type.wifi,
                        operator,
                        Location(coordinates = listOf(lat1.replace(" ", "").toDouble(), lon1.replace(" ", "").toDouble())),
                        Location(coordinates = listOf(lat2.replace(" ", "").toDouble(), lon2.replace(" ", "").toDouble())),
                        selectedUser.second,
                        count.replace(" ", "").toInt()
                    )
                } catch (e: Exception) {
                    println(e)
                }; alphaStatus = 0.7f
            },
            generateToMongo = {
                try {
                    GeneratorUtil.generateToMongo(
                        minValue.toLong(),
                        maxValue.toLong(),
                        if (selectedOption == "Data") Type.data else Type.wifi,
                        operator,
                        Location(coordinates = listOf(lat1.replace(" ", "").toDouble(), lon1.replace(" ", "").toDouble())),
                        Location(coordinates = listOf(lat2.replace(" ", "").toDouble(), lon2.replace(" ", "").toDouble())),
                        selectedUser.second,
                        count.replace(" ", "").toInt(),
                        conn
                    )
                } catch (e: Exception) {
                    println(e)
                }; alphaStatus = 0.7f
            },
            alphaStatus = alphaStatus
        )
        Text(
            text = "Parameters:"
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = minValue,
                onValueChange = { newText ->
                    minValue = newText
                },
                maxLines = 1,
                modifier = Modifier.border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = MaterialTheme.shapes.small
                ).weight(0.3f),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = MaterialTheme.typography.body1.fontSize
                ),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black
                )
            )
            Text(
                text = "< Generated Value <",
                modifier = Modifier.weight(0.3f).wrapContentSize(),
            )
            TextField(
                value = maxValue,
                onValueChange = { newText ->
                    maxValue = newText
                },
                maxLines = 1,
                modifier = Modifier.border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = MaterialTheme.shapes.small
                ).weight(0.3f),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = MaterialTheme.typography.body1.fontSize
                ),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black
                )
            )
        }

        Row (
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text(
                    text = "Position 1:"
                )
                TextField(
                    value = lat1,
                    onValueChange = { newText ->
                        lat1 = newText
                    },
                    maxLines = 1,
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = MaterialTheme.shapes.small
                    ).fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = MaterialTheme.typography.body1.fontSize
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Black
                    )
                )
                TextField(
                    value = lon1,
                    onValueChange = { newText ->
                        lon1 = newText
                    },
                    maxLines = 1,
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = MaterialTheme.shapes.small
                    ).fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = MaterialTheme.typography.body1.fontSize
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Black
                    )
                )

            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Position 2:"
                )
                TextField(
                    value = lat2,
                    onValueChange = { newText ->
                        lat2 = newText
                    },
                    maxLines = 1,
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = MaterialTheme.shapes.small
                    ).fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = MaterialTheme.typography.body1.fontSize
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Black
                    )
                )
                TextField(
                    value = lon2,
                    onValueChange = { newText ->
                        lon2 = newText
                    },
                    maxLines = 1,
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = MaterialTheme.shapes.small
                    ).fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = MaterialTheme.typography.body1.fontSize
                    ),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Black
                    )
                )
            }
        }

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
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Provider: "
            )
            TextField(
                value = operator,
                onValueChange = { newText ->
                    operator = newText
                },
                maxLines = 1,
                modifier = Modifier.border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = MaterialTheme.shapes.small
                ).fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = MaterialTheme.typography.body1.fontSize
                ),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black
                )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { isUserSelectorOpen = true }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
            }
            Text(text = selectedUser.first)
            DropdownMenu(
                expanded = isUserSelectorOpen,
                onDismissRequest = { isUserSelectorOpen = false },
            ) {
                userOptions.forEach { option ->
                    DropdownMenuItem(onClick = {
                        selectedUser = option
                        isUserSelectorOpen = false
                    }) {
                        Text(option.first)
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = "Count #: "
            )
            TextField(
                value = count,
                onValueChange = { newText ->
                    count = newText
                },
                maxLines = 1,
                modifier = Modifier.border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = MaterialTheme.shapes.small
                ).fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = MaterialTheme.typography.body1.fontSize
                ),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black
                )
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            userOptions.clear()
        }
    }
}


@Composable
fun AboutApp() {
    Column {
        Text(
            text = "About application",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text(
            text = "Made for a project focusing on Internet network coverage and speeds",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 15.sp
        )
        Text(
            text = "Authors: Andraž Šošterič, David Rajlič, Domen Pahole",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 15.sp
        )

    }

}

@Composable
@Preview
fun App() {
    val currentContent = mutableStateOf<@Composable () -> Unit>({ Measure() })
    MaterialTheme {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Navigation(
                onDataClicked = { currentContent.value = { Data() } },
                onMeasureClicked = { currentContent.value = { Measure() } },
                onTowerClicked = { currentContent.value = { Towers() } },
                onAboutAppClicked = { currentContent.value = { AboutApp() } },
                onDslCityClicked = { currentContent.value = { Editor() } },
                onScraperClicked = { currentContent.value = { Scraper() } },
                onGeneratorClicked = { currentContent.value = { Generator() } },
            )
            Content(currentContent.value, modifier = Modifier.weight(1f))

        }
    }
}

fun main() = application {
    runBlocking { conn = DatabaseUtil.setupConnection() }

    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
