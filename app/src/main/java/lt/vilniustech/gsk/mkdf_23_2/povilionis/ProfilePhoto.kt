package lt.vilniustech.gsk.mkdf_23_2.povilionis

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

// Profilio nuotrauka saugoma programėlės aplanke (filesDir/photos/profile.jpg)
class ProfilePhoto(private val context: Context) {

    private val dir = File(context.filesDir, "photos")
    val file = File(dir, "profile.jpg")
    private val captureFile = File(dir, "capture.jpg")

    /** Paskutinio pakeitimo laikas arba 0, jei nuotraukos dar nėra. */
    fun version(): Long = if (file.exists()) file.lastModified() else 0L

    /** URI laikinam failui; per FileProvider jį gali įrašyti kameros programėlė. */
    fun newCaptureUri(): Uri {
        dir.mkdirs()
        captureFile.delete()
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", captureFile)
    }

    /** Nauja nuotrauka pakeičia senąją tik jei kamera ją tikrai įrašė. */
    fun commitCapture(): Boolean {
        if (!captureFile.exists() || captureFile.length() == 0L) return false
        file.delete()
        return captureFile.renameTo(file)
    }

    fun discardCapture() {
        captureFile.delete()
    }
}
