package lt.vilniustech.gsk.mkdf_23_2.povilionis.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import lt.vilniustech.gsk.mkdf_23_2.povilionis.CardViewModel
import lt.vilniustech.gsk.mkdf_23_2.povilionis.Destination
import lt.vilniustech.gsk.mkdf_23_2.povilionis.OWNER_EMAIL
import lt.vilniustech.gsk.mkdf_23_2.povilionis.OWNER_GROUP
import lt.vilniustech.gsk.mkdf_23_2.povilionis.OWNER_NAME
import lt.vilniustech.gsk.mkdf_23_2.povilionis.R

// Vizitinė kortelė: nuotrauka, vardas, kontaktai ir mygtukai į CV, portfolio ir kontaktų formą
@Composable
fun HomeScreen(
    viewModel: CardViewModel,
    isWide: Boolean,
    navigate: (Destination) -> Unit,
    showMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val photoUpdated = stringResource(R.string.photo_updated)
    val noCamera = stringResource(R.string.camera_not_available)

    // Kameros programėlės iškvietimas: nuotrauka įrašoma į mūsų failą per FileProvider
    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        viewModel.onPhotoTaken(success)
        if (success) showMessage(photoUpdated)
    }
    val onTakePhoto = {
        val uri = viewModel.newPhotoUri()
        try {
            takePicture.launch(uri)
        } catch (e: ActivityNotFoundException) {
            showMessage(noCamera)
        }
    }

    // Paspaudus el. paštą – atidaroma el. pašto programėlė
    val onEmailClick = {
        val intent = Intent(Intent.ACTION_SENDTO, "mailto:$OWNER_EMAIL".toUri())
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showMessage(context.getString(R.string.contact_no_email_app))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier.widthIn(max = if (isWide) 840.dp else 560.dp).fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            if (isWide) {
                // Platus ekranas: nuotrauka kairėje, informacija dešinėje
                Row(
                    modifier = Modifier.padding(28.dp),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfilePhoto(viewModel, onTakePhoto, size = 200)
                    Column(modifier = Modifier.weight(1f)) {
                        OwnerInfo(onEmailClick, centered = false)
                        Spacer(Modifier.height(20.dp))
                        CardButtons(navigate)
                    }
                }
            } else {
                // Telefonas: viskas vienas po kito, centruota
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProfilePhoto(viewModel, onTakePhoto, size = 160)
                    Spacer(Modifier.height(16.dp))
                    OwnerInfo(onEmailClick, centered = true)
                    Spacer(Modifier.height(20.dp))
                    CardButtons(navigate)
                }
            }
        }
    }
}

// Profilio nuotrauka su mygtuku „nufotografuoti iš naujo“
@Composable
private fun ProfilePhoto(viewModel: CardViewModel, onTakePhoto: () -> Unit, size: Int) {
    val context = LocalContext.current
    Box {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(if (viewModel.photoVersion > 0) viewModel.photoFile else null)
                // Versija rakte – kad pakeitus nuotrauką nebūtų rodoma sena iš atminties
                .memoryCacheKey("profile-${viewModel.photoVersion}")
                .build(),
            contentDescription = stringResource(R.string.home_photo_description),
            placeholder = painterResource(R.drawable.ic_avatar_placeholder),
            error = painterResource(R.drawable.ic_avatar_placeholder),
            fallback = painterResource(R.drawable.ic_avatar_placeholder),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(size.dp).clip(CircleShape)
        )
        SmallFloatingActionButton(
            onClick = onTakePhoto,
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(Icons.Filled.Create, contentDescription = stringResource(R.string.home_take_photo))
        }
    }
}

// Vardas, pareigos, grupė ir el. paštas
@Composable
private fun OwnerInfo(onEmailClick: () -> Unit, centered: Boolean) {
    val align = if (centered) TextAlign.Center else TextAlign.Start
    Column(horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start) {
        Text(OWNER_NAME, style = MaterialTheme.typography.headlineMedium, textAlign = align)
        Text(
            stringResource(R.string.home_role),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = align
        )
        Spacer(Modifier.height(8.dp))
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Text(
                stringResource(R.string.home_group, OWNER_GROUP),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onEmailClick) {
            Icon(Icons.Filled.Email, contentDescription = stringResource(R.string.home_send_email, OWNER_EMAIL))
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(OWNER_EMAIL)
        }
    }
}

// Mygtukai: CV, portfolio ir kontaktų forma
@Composable
private fun CardButtons(navigate: (Destination) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(onClick = { navigate(Destination.Cv) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.AccountBox, contentDescription = null)
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(R.string.home_show_cv))
        }
        FilledTonalButton(onClick = { navigate(Destination.Portfolio) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Star, contentDescription = null)
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(R.string.home_show_portfolio))
        }
        OutlinedButton(
            onClick = { navigate(Destination.Contact) },
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Icon(Icons.Filled.Email, contentDescription = null)
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(R.string.home_contact_me))
        }
    }
}
