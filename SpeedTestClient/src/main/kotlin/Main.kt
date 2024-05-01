import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
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
import dslCity.ForForeachFFFAutomaton
import dslCity.Parser
import dslCity.Scanner
import java.awt.FileDialog
import java.awt.Frame
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.time.LocalDateTime

@Composable
fun Navigation(
    onMeasureClicked:() -> Unit,
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

@Composable
fun Measure(){
    Text(
        text = "You are viewing speedtest tab.",
        modifier = Modifier.fillMaxSize().wrapContentSize()
    )
}

@Composable
fun Towers() {
    Text(
        text = "You are viewing invoices tab.",
        modifier = Modifier.fillMaxSize().wrapContentSize()
    )
}



@Composable
fun Navbar(
    loadFromFile: () -> Unit,
    saveToFile: () -> Unit,
    saveToFileAs: () -> Unit,
    runCode: () -> Unit,
    alphaStatus: Float
){

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ){
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
@OptIn(ExperimentalComposeUiApi::class)
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
        Navbar(
            loadFromFile = {
                val fileDialog = FileDialog(Frame(), "Select File", FileDialog.LOAD)
                fileDialog.isVisible = true
                currentFilePath = fileDialog.directory+fileDialog.file

                try {
                    text = File(currentFilePath).readText()
                } catch (e: FileNotFoundException){
                    output += "\nFile not found"
                }
                alphaStatus = 0f

            },
            saveToFile = {
                try {
                    File(currentFilePath).writeText(text)
                    alphaStatus = 0.7f
                } catch (e: FileNotFoundException){
                    output += "\nFile not found"
                }

            },
            saveToFileAs = {
                val fileDialog = FileDialog(Frame(), "Select File", FileDialog.LOAD)
                fileDialog.isVisible = true
                val newFilePath = fileDialog.directory+fileDialog.file
                println(newFilePath)
                if (newFilePath != null && !File(newFilePath).exists()) {
                    File(newFilePath).createNewFile()
                }
                File(newFilePath).writeText(text)
                alphaStatus = 0.7f
            },
            runCode = {
                output += "@"+LocalDateTime.now()+"\n"
                val out = ByteArrayOutputStream()
                Parser(Scanner(ForForeachFFFAutomaton, text.toByteArray().inputStream())).parse().eval(out)
                output += String(out.toByteArray())
            },
            alphaStatus = alphaStatus
        )
        TextField(
            value = text,
            onValueChange = {

                newText ->
                run {
                    text = newText.replace("\t", " ")
                    println(text)
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
fun Generator() {
    Text(
        text = "You are viewing generator tab.",
        modifier = Modifier.fillMaxSize().wrapContentSize()
    )
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
    val currentContent = mutableStateOf<@Composable () -> Unit>({ Editor() })
    MaterialTheme {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Navigation(
                onMeasureClicked = {  currentContent.value = { Measure() } },
                onTowerClicked  = { currentContent.value = { Towers() } },
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
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
