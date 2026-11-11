package lt.vilniustech.gsk.mkdf_23_2.povilionis

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import lt.vilniustech.gsk.mkdf_23_2.povilionis.data.AppDatabase
import lt.vilniustech.gsk.mkdf_23_2.povilionis.data.CvEntry
import lt.vilniustech.gsk.mkdf_23_2.povilionis.data.CvSection
import lt.vilniustech.gsk.mkdf_23_2.povilionis.data.PortfolioItem

// Ekranų duomenys: vartotojo įrašai iš duomenų bazės ir profilio nuotrauka
class CardViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.get(application)
    private val profilePhoto = ProfilePhoto(application)

    // Vartotojo pridėti CV įrašai (atsinaujina automatiškai, kai keičiasi duomenų bazė)
    val cvEntries: StateFlow<List<CvEntry>> = db.cvDao().getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Vartotojo pridėti portfolio darbai
    val portfolioItems: StateFlow<List<PortfolioItem>> = db.portfolioDao().getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Nuotraukos versija: pasikeitus nuotraukai ekranas ją perkrauna (0 – nuotraukos nėra)
    var photoVersion by mutableLongStateOf(profilePhoto.version())
        private set

    val photoFile get() = profilePhoto.file

    fun addCvEntry(section: CvSection, title: String, details: String) {
        viewModelScope.launch {
            db.cvDao().insert(CvEntry(section = section, title = title.trim(), details = details.trim()))
        }
    }

    fun deleteCvEntry(entry: CvEntry) {
        viewModelScope.launch { db.cvDao().delete(entry) }
    }

    fun addPortfolioItem(title: String, description: String, imageUrl: String) {
        viewModelScope.launch {
            db.portfolioDao().insert(
                PortfolioItem(title = title.trim(), description = description.trim(), imageUrl = imageUrl.trim())
            )
        }
    }

    fun deletePortfolioItem(item: PortfolioItem) {
        viewModelScope.launch { db.portfolioDao().delete(item) }
    }

    // Laikinas failas, į kurį kameros programėlė įrašys nuotrauką
    fun newPhotoUri() = profilePhoto.newCaptureUri()

    // Kviečiama grįžus iš kameros: sėkmės atveju nuotrauka pakeičiama
    fun onPhotoTaken(success: Boolean) {
        if (success && profilePhoto.commitCapture()) {
            photoVersion = profilePhoto.version()
        } else {
            profilePhoto.discardCapture()
        }
    }
}
