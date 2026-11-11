package lt.vilniustech.gsk.mkdf_23_2.povilionis

// Kontaktų formos laukai, kuriuose rasta klaida
enum class ContactField { NAME, EMAIL, QUESTION }

object ContactForm {

    private val emailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

    fun isValidEmail(email: String): Boolean = emailRegex.matches(email.trim())

    /** Grąžina laukus su klaidomis; tuščias rinkinys – forma teisinga. */
    fun validate(name: String, email: String, question: String): Set<ContactField> = buildSet {
        if (name.isBlank()) add(ContactField.NAME)
        if (!isValidEmail(email)) add(ContactField.EMAIL)
        if (question.isBlank()) add(ContactField.QUESTION)
    }

    /** Laiško tekstas: klausimas ir klausiančiojo kontaktai. */
    fun buildBody(question: String, name: String, email: String, phone: String): String = buildString {
        appendLine(question.trim())
        appendLine()
        appendLine("--")
        appendLine(name.trim())
        appendLine(email.trim())
        if (phone.isNotBlank()) appendLine(phone.trim())
    }.trimEnd()
}

/**
 * Portfolio nuoroda turi būti https adresas (Android pagal nutylėjimą blokuoja http).
 * Tuščia nuoroda leidžiama – tada rodomas pakaitinis vaizdas.
 */
fun isValidImageUrl(url: String): Boolean {
    val trimmed = url.trim()
    return trimmed.isEmpty() || (trimmed.startsWith("https://") && trimmed.length > "https://".length)
}
