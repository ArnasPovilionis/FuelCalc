package lt.vilniustech.gsk.mkdf_23_2.povilionis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContactFormTest {

    // Teisingai užpildyta forma – klaidų nėra
    @Test
    fun validate_filledForm_noErrors() {
        assertTrue(ContactForm.validate("Jonas", "jonas@example.com", "Ar galime susitikti?").isEmpty())
    }

    // Tuščias vardas, blogas el. paštas ir tuščias klausimas – visi trys laukai pažymimi
    @Test
    fun validate_emptyForm_allFieldsMarked() {
        assertEquals(
            setOf(ContactField.NAME, ContactField.EMAIL, ContactField.QUESTION),
            ContactForm.validate(" ", "jonas.example.com", "")
        )
    }

    // Laiške yra klausimas ir klausiančiojo kontaktai; tuščias telefonas neįrašomas
    @Test
    fun buildBody_containsQuestionAndContacts() {
        val body = ContactForm.buildBody("Ar galime susitikti?", "Jonas", "jonas@example.com", "")
        assertEquals("Ar galime susitikti?\n\n--\nJonas\njonas@example.com", body)
    }

    // Portfolio nuoroda: tik https arba tuščia
    @Test
    fun isValidImageUrl_acceptsOnlyHttps() {
        assertTrue(isValidImageUrl("https://example.com/a.png"))
        assertTrue(isValidImageUrl(""))
        assertFalse(isValidImageUrl("http://example.com/a.png"))
        assertFalse(isValidImageUrl("example.com/a.png"))
    }
}
