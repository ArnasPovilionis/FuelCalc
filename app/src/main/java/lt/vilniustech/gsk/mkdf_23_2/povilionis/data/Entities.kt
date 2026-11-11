package lt.vilniustech.gsk.mkdf_23_2.povilionis.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// CV skyriai, į kuriuos vartotojas gali pridėti įrašą
enum class CvSection { EDUCATION, EXPERIENCE, SKILLS, OTHER }

// Vartotojo pridėtas CV įrašas (saugomas Room duomenų bazėje)
@Entity(tableName = "cv_entries")
data class CvEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val section: CvSection,
    val title: String,
    val details: String
)

// Vartotojo pridėtas portfolio darbas (vaizdas kraunamas iš interneto pagal imageUrl)
@Entity(tableName = "portfolio_items")
data class PortfolioItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val imageUrl: String
)
