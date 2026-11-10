package lt.vilniustech.gsk.mkdf_23_2.povilionis.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import lt.vilniustech.gsk.mkdf_23_2.povilionis.CardViewModel
import lt.vilniustech.gsk.mkdf_23_2.povilionis.R
import lt.vilniustech.gsk.mkdf_23_2.povilionis.builtInCv
import lt.vilniustech.gsk.mkdf_23_2.povilionis.data.CvEntry
import lt.vilniustech.gsk.mkdf_23_2.povilionis.data.CvSection

@StringRes
fun CvSection.label(): Int = when (this) {
    CvSection.EDUCATION -> R.string.cv_section_education
    CvSection.EXPERIENCE -> R.string.cv_section_experience
    CvSection.SKILLS -> R.string.cv_section_skills
    CvSection.OTHER -> R.string.cv_section_other
}

// CV: įmontuoti įrašai + vartotojo pridėti (iš duomenų bazės), sugrupuoti pagal skyrius
@Composable
fun CvScreen(viewModel: CardViewModel, showMessage: (String) -> Unit) {
    val userEntries by viewModel.cvEntries.collectAsState()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    val added = stringResource(R.string.cv_added)
    val deleted = stringResource(R.string.deleted)

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CvSection.entries.forEach { section ->
                val builtIn = builtInCv.filter { it.section == section }
                val mine = userEntries.filter { it.section == section }
                if (builtIn.isEmpty() && mine.isEmpty()) return@forEach

                item(key = "header-$section") {
                    Text(
                        stringResource(section.label()).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(builtIn, key = { "builtin-${it.title}" }) { entry ->
                    CvCard(title = stringResource(entry.title), details = stringResource(entry.details))
                }
                items(mine, key = { "user-${it.id}" }) { entry ->
                    CvCard(
                        title = entry.title,
                        details = entry.details,
                        onDelete = {
                            viewModel.deleteCvEntry(entry)
                            showMessage(deleted)
                        },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { showAddDialog = true },
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            text = { Text(stringResource(R.string.cv_add)) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        )
    }

    if (showAddDialog) {
        AddCvDialog(
            onDismiss = { showAddDialog = false },
            onSave = { section, title, details ->
                viewModel.addCvEntry(section, title, details)
                showAddDialog = false
                showMessage(added)
            }
        )
    }
}

// Vienas CV įrašas; vartotojo įrašai turi trynimo mygtuką
@Composable
private fun CvCard(
    title: String,
    details: String,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                if (details.isNotBlank()) {
                    Text(
                        details,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (onDelete != null) {
                    Text(
                        stringResource(R.string.user_entry_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                }
            }
        }
    }
}

// Langas naujam CV įrašui: skyrius, pavadinimas, aprašymas
@Composable
private fun AddCvDialog(onDismiss: () -> Unit, onSave: (CvSection, String, String) -> Unit) {
    var section by rememberSaveable { mutableStateOf(CvSection.EXPERIENCE) }
    var title by rememberSaveable { mutableStateOf("") }
    var details by rememberSaveable { mutableStateOf("") }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    EntryDialog(
        title = stringResource(R.string.cv_add),
        onDismiss = onDismiss,
        onSave = {
            showErrors = true
            if (title.isNotBlank()) onSave(section, title, details)
        }
    ) {
        Text(stringResource(R.string.field_section), style = MaterialTheme.typography.labelLarge)
        // Skyrių pasirinkimas; slenkama horizontaliai, jei netelpa mažame ekrane
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CvSection.entries.forEach { option ->
                FilterChip(
                    selected = section == option,
                    onClick = { section = option },
                    label = { Text(stringResource(option.label()), maxLines = 1) }
                )
            }
        }
        DialogTextField(
            value = title,
            onValueChange = { title = it },
            label = stringResource(R.string.field_title),
            error = if (showErrors && title.isBlank()) stringResource(R.string.error_required) else null
        )
        DialogTextField(
            value = details,
            onValueChange = { details = it },
            label = stringResource(R.string.field_details),
            singleLine = false
        )
    }
}
