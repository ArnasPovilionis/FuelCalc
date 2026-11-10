package lt.vilniustech.gsk.mkdf_23_2.povilionis.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import lt.vilniustech.gsk.mkdf_23_2.povilionis.CardViewModel
import lt.vilniustech.gsk.mkdf_23_2.povilionis.R
import lt.vilniustech.gsk.mkdf_23_2.povilionis.builtInPortfolio
import lt.vilniustech.gsk.mkdf_23_2.povilionis.isValidImageUrl

// Portfolio: darbų tinklelis; stulpelių skaičius priklauso nuo ekrano pločio
@Composable
fun PortfolioScreen(viewModel: CardViewModel, showMessage: (String) -> Unit) {
    val userItems by viewModel.portfolioItems.collectAsState()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    val added = stringResource(R.string.portfolio_added)
    val deleted = stringResource(R.string.deleted)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 280.dp),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 96.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(builtInPortfolio, key = { "builtin-${it.title}" }) { item ->
                PortfolioCard(
                    title = stringResource(item.title),
                    description = stringResource(item.description),
                    imageUrl = item.imageUrl
                )
            }
            items(userItems, key = { "user-${it.id}" }) { item ->
                PortfolioCard(
                    title = item.title,
                    description = item.description,
                    imageUrl = item.imageUrl,
                    onDelete = {
                        viewModel.deletePortfolioItem(item)
                        showMessage(deleted)
                    },
                    modifier = Modifier.animateItem()
                )
            }
        }

        ExtendedFloatingActionButton(
            onClick = { showAddDialog = true },
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            text = { Text(stringResource(R.string.portfolio_add)) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        )
    }

    if (showAddDialog) {
        AddPortfolioDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, description, url ->
                viewModel.addPortfolioItem(title, description, url)
                showAddDialog = false
                showMessage(added)
            }
        )
    }
}

// Viena portfolio kortelė: vaizdas iš interneto, pavadinimas, aprašymas
@Composable
private fun PortfolioCard(
    title: String,
    description: String,
    imageUrl: String,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        RemoteImage(url = imageUrl, contentDescription = title)
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                if (description.isNotBlank()) {
                    Text(
                        description,
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

// Vaizdas iš interneto (Coil): kol kraunama – pakaitinis vaizdas ir indikatorius,
// nepavykus – klaidos vaizdas ir mygtukas „Bandyti dar kartą“
@Composable
private fun RemoteImage(url: String, contentDescription: String) {
    // Padidinus šį skaičių, vaizdas kraunamas iš naujo
    var attempt by rememberSaveable(url) { mutableIntStateOf(0) }
    val imageModifier = Modifier.fillMaxWidth().aspectRatio(16f / 10f)

    if (url.isBlank()) {
        Image(
            painterResource(R.drawable.ic_image_placeholder),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = imageModifier
        )
        return
    }

    key(attempt) {
        SubcomposeAsyncImage(
            model = url,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = imageModifier,
            loading = {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painterResource(R.drawable.ic_image_placeholder),
                        contentDescription = stringResource(R.string.image_loading),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    CircularProgressIndicator(modifier = Modifier.size(36.dp))
                }
            },
            error = {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painterResource(R.drawable.ic_image_error),
                        contentDescription = stringResource(R.string.image_error),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    FilledTonalButton(
                        onClick = { attempt++ },
                        modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null)
                        Text(stringResource(R.string.image_retry), modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }
        )
    }
}

// Langas naujam portfolio darbui
@Composable
private fun AddPortfolioDialog(onDismiss: () -> Unit, onSave: (String, String, String) -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var url by rememberSaveable { mutableStateOf("") }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    EntryDialog(
        title = stringResource(R.string.portfolio_add),
        onDismiss = onDismiss,
        onSave = {
            showErrors = true
            if (title.isNotBlank() && isValidImageUrl(url)) onSave(title, description, url)
        }
    ) {
        DialogTextField(
            value = title,
            onValueChange = { title = it },
            label = stringResource(R.string.field_title),
            error = if (showErrors && title.isBlank()) stringResource(R.string.error_required) else null
        )
        DialogTextField(
            value = description,
            onValueChange = { description = it },
            label = stringResource(R.string.field_description),
            singleLine = false
        )
        DialogTextField(
            value = url,
            onValueChange = { url = it },
            label = stringResource(R.string.field_image_url),
            error = if (showErrors && !isValidImageUrl(url)) stringResource(R.string.error_url) else null,
            keyboardType = KeyboardType.Uri
        )
    }
}
