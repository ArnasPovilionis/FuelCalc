package lt.vilniustech.gsk.mkdf_23_2.povilionis.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import lt.vilniustech.gsk.mkdf_23_2.povilionis.ContactField
import lt.vilniustech.gsk.mkdf_23_2.povilionis.ContactForm
import lt.vilniustech.gsk.mkdf_23_2.povilionis.OWNER_EMAIL
import lt.vilniustech.gsk.mkdf_23_2.povilionis.R

// Forma „Susisiekti su manimi“: klausimas ir kontaktai siunčiami per el. pašto programėlę (Intent)
@Composable
fun ContactScreen(showMessage: (String) -> Unit) {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var question by rememberSaveable { mutableStateOf("") }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    val errors = if (showErrors) ContactForm.validate(name, email, question) else emptySet()
    val required = stringResource(R.string.error_required)
    val checkFields = stringResource(R.string.contact_check_fields)
    val noEmailApp = stringResource(R.string.contact_no_email_app)
    val subject = stringResource(R.string.contact_subject, name.trim())

    val onSend = {
        showErrors = true
        if (ContactForm.validate(name, email, question).isEmpty()) {
            val body = ContactForm.buildBody(question, name, email, phone)
            // ACTION_SENDTO + mailto: – atsidaro tik el. pašto programėlės
            val intent = Intent(
                Intent.ACTION_SENDTO,
                "mailto:$OWNER_EMAIL?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}".toUri()
            ).apply {
                putExtra(Intent.EXTRA_EMAIL, arrayOf(OWNER_EMAIL))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                showMessage(noEmailApp)
            }
        } else {
            showMessage(checkFields)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(stringResource(R.string.contact_intro), style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.size(4.dp))
            ContactTextField(
                value = name,
                onValueChange = { name = it },
                label = stringResource(R.string.contact_name),
                error = if (ContactField.NAME in errors) required else null
            )
            ContactTextField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(R.string.contact_email),
                error = if (ContactField.EMAIL in errors) stringResource(R.string.contact_error_email) else null,
                keyboardType = KeyboardType.Email
            )
            ContactTextField(
                value = phone,
                onValueChange = { phone = it },
                label = stringResource(R.string.contact_phone),
                keyboardType = KeyboardType.Phone
            )
            ContactTextField(
                value = question,
                onValueChange = { question = it },
                label = stringResource(R.string.contact_question),
                error = if (ContactField.QUESTION in errors) required else null,
                singleLine = false
            )
            Button(onClick = onSend, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.contact_send))
            }
        }
    }
}

@Composable
private fun ContactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 4,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = if (singleLine) ImeAction.Next else ImeAction.Default
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
