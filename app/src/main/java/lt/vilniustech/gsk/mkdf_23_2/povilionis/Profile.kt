package lt.vilniustech.gsk.mkdf_23_2.povilionis

import androidx.annotation.StringRes
import lt.vilniustech.gsk.mkdf_23_2.povilionis.data.CvSection

// Savininko kontaktai (rodomi kortelėje ir naudojami kontaktų formoje)
const val OWNER_NAME = "Arnas Povilionis"
const val OWNER_GROUP = "MKDF23/2"
// TODO: įrašykite savo el. pašto adresą
const val OWNER_EMAIL = "your.email@example.com"

// Portfolio vaizdai talpinami viešai GitHub repozitorijoje (aplankas portfolio/)
private const val IMAGE_BASE_URL =
    "https://raw.githubusercontent.com/ArnasPovilionis/FuelCalc/master/portfolio/"

// Įmontuotas CV įrašas: tekstai iš strings.xml, todėl rodomi pasirinkta kalba
data class BuiltInCvEntry(
    val section: CvSection,
    @StringRes val title: Int,
    @StringRes val details: Int
)

// Įmontuotas portfolio darbas
data class BuiltInPortfolioItem(
    @StringRes val title: Int,
    @StringRes val description: Int,
    val imageUrl: String
)

val builtInCv = listOf(
    BuiltInCvEntry(CvSection.EDUCATION, R.string.cv_education_title, R.string.cv_education_details),
    BuiltInCvEntry(CvSection.SKILLS, R.string.cv_skills_title, R.string.cv_skills_details),
    BuiltInCvEntry(CvSection.OTHER, R.string.cv_languages_title, R.string.cv_languages_details)
)

val builtInPortfolio = listOf(
    BuiltInPortfolioItem(
        R.string.portfolio_fuelcalc_title,
        R.string.portfolio_fuelcalc_description,
        IMAGE_BASE_URL + "fuelcalc.png"
    ),
    BuiltInPortfolioItem(
        R.string.portfolio_card_title,
        R.string.portfolio_card_description,
        IMAGE_BASE_URL + "business_card.png"
    )
)
