import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun OutlinedTextFieldWithLabel(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .border(width = 1.dp, color = Color.Gray, shape = MaterialTheme.shapes.medium),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { /* Handle keyboard done action */ }),
            singleLine = true
        )
        Text(
            text = label,
            modifier = Modifier
                .padding(start = 6.dp)
                .background(color = Color.White)
                .padding(horizontal = 4.dp),
            style = MaterialTheme.typography.caption,
            color = Color.Gray
        )
    }
}