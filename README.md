# Vizitinė kortelė – Android programėlė

**Autorius:** Arnas Povilionis, grupė MKDF23/2
**Paketas:** `lt.vilniustech.gsk.mkdf_23_2.povilionis`

Programėlė – mano vizitinė kortelė: nuotrauka, kontaktai, CV, portfolio ir forma „Susisiekti su manimi“.
Kotlin + Jetpack Compose + Material 3.

> ⚠️ Prieš atsiskaitymą ir publikavimą įrašykite savo el. pašto adresą faile
> `app/src/main/java/lt/vilniustech/gsk/mkdf_23_2/povilionis/Profile.kt`
> (eilutė `const val OWNER_EMAIL = "your.email@example.com"`).

## Kaip paleisti

1. Android Studio → **File → Open** → projekto aplankas.
2. Palaukite „Gradle Sync“.
3. Pasirinkite emuliatorių ar telefoną (Android 8.0 / API 26 ar naujesnį) ir spauskite **Run ▶**.
4. Testai: `app/src/test/.../ContactFormTest.kt` → žalias ▶ (arba `gradlew test`).

## Reikalavimų atitikimas

| Reikalavimas | Kur realizuota |
|---|---|
| Kontaktai (el. paštas), nuotrauka | `ui/screens/HomeScreen.kt`, duomenys – `Profile.kt` |
| Mygtukai „Rodyti CV“, „Rodyti portfolio“ | `HomeScreen.kt` → `CvScreen.kt`, `PortfolioScreen.kt` |
| **Data Storage**: nauji CV / portfolio įrašai išlieka paleidus iš naujo | Room duomenų bazė: `data/Entities.kt`, `data/AppDatabase.kt`; `CardViewModel.kt` |
| **Kontaktų forma** – laiškas per kitą programėlę (Intent) | `ui/screens/ContactScreen.kt` (`Intent.ACTION_SENDTO`, `mailto:`), tikrinimas – `ContactForm.kt` |
| **Lokalizacija** LT / EN, tekstai atskiruose failuose | `res/values/strings.xml` (EN), `res/values-lt/strings.xml` (LT); perjungiklis „LT / EN“ viršutinėje juostoje (`BusinessCardApp.kt`, `AppCompatDelegate.setApplicationLocales`) |
| **Skirtingi ekranai**, vertikaliai ir horizontaliai | `MainActivity.kt` (`calculateWindowSizeClass`) → siaurame ekrane apatinė navigacija, plačiame – šoninė (`NavigationRail`); kortelė plačiame ekrane – dviem stulpeliais; portfolio – `LazyVerticalGrid(GridCells.Adaptive)`; turinio plotis ribojamas `widthIn(max)` |
| **(+1) Kamera** – nauja nuotrauka | `HomeScreen.kt` (`ActivityResultContracts.TakePicture`), `ProfilePhoto.kt`, `FileProvider` (`AndroidManifest.xml`, `res/xml/file_paths.xml`) |
| **(+1) Vaizdai iš interneto** su Coil, pakaitiniai vaizdai ir „Bandyti dar kartą“ | `PortfolioScreen.kt` (`SubcomposeAsyncImage`: `loading` – pakaitinis vaizdas + indikatorius, `error` – klaidos vaizdas + mygtukas); vaizdai talpinami aplanke `portfolio/` (viešai per GitHub) |
| Paketo pavadinimas `lt.vilniustech.gsk.<grupė>.<pavadinimas>` | `app/build.gradle.kts` |
| Unit testai | `app/src/test/.../ContactFormTest.kt` |

## Kur saugomi duomenys

- **CV ir portfolio įrašai** – Room (SQLite) duomenų bazė `business_card.db` telefono atmintyje.
  Room pasirinkta, nes įrašai yra sąrašas su keliais laukais – duomenų bazei tai natūralu,
  o DataStore labiau tinka pavieniams nustatymams.
- **Nuotrauka** – failas `filesDir/photos/profile.jpg`.
- **Pasirinkta kalba** – ją išsaugo Android (`AppCompatDelegate`; senesnėse versijose – `autoStoreLocales`).
- Įmontuoti (pradiniai) CV ir portfolio įrašai yra `strings.xml`, todėl rodomi pasirinkta kalba.

## Testai

`ContactFormTest` tikrina:
1. teisingai užpildyta forma priimama;
2. tuščias vardas, blogas el. paštas ir tuščias klausimas pažymimi kaip klaidos;
3. laiško tekste yra klausimas ir klausiančiojo kontaktai;
4. portfolio vaizdo nuoroda priimama tik `https://` (Android blokuoja `http`).

## Publikavimas Google Play (uždaras testavimas)

1. **El. paštas.** Įrašykite savo el. paštą `Profile.kt` (žr. aukščiau).
2. **Privatumo politika.** Naudokite nuorodą į `PRIVACY.md`, kai šis failas bus `master` šakoje:
   `https://github.com/ArnasPovilionis/FuelCalc/blob/master/PRIVACY.md`
   (repozitorija turi būti vieša; atidarykite nuorodą naršyklėje ir įsitikinkite, kad veikia).
3. **Pasirašytas AAB.** Android Studio → **Build → Generate Signed App Bundle / APK → Android App Bundle**
   → **Create new…** (sukurkite raktų saugyklą `.jks`, **išsaugokite ją ir slaptažodžius** – be jų
   atnaujinti programėlės nebegalėsite; į GitHub jos **nekelkite**) → **release** → gausite
   `app/release/app-release.aab`.
4. **Play Console** (katedros paskyra arba savo) → **Create app**: pavadinimas „Vizitinė kortelė“,
   kalba – lietuvių, „App“, „Free“.
5. **App content**: Privacy policy (2 punkto nuoroda), App access – „All functionality available
   without special access“, Ads – „No“, Content rating anketa, Target audience – 18+,
   Data safety – „No data collected / shared“ (duomenys lieka telefone).
6. **Store listing**: trumpas ir pilnas aprašymas, ikona `store/icon_512.png`,
   feature graphic `store/feature_graphic_1024x500.png`, bent 2 telefono ekrano nuotraukos
   (`store/screenshots/`).
7. **Testing → Closed testing** → sukurkite leidimą (release), įkelkite `app-release.aab`,
   pridėkite testuotojų sąrašą → **Send for review**.
8. Kai Google patvirtins – nukopijuokite testavimo nuorodą, padarykite ekrano nuotrauką ir įkelkite į Moodle.

## Projekto struktūra

```
app/src/main/java/lt/vilniustech/gsk/mkdf_23_2/povilionis/
├── MainActivity.kt        – paleidimas, ekrano dydžio klasė
├── BusinessCardApp.kt     – Scaffold, navigacija (apatinė / šoninė), kalbos perjungiklis
├── CardViewModel.kt       – duomenys ekranams (Room įrašai, nuotrauka)
├── Profile.kt             – vardas, grupė, el. paštas, įmontuoti CV ir portfolio įrašai
├── ContactForm.kt         – kontaktų formos tikrinimas ir laiško tekstas
├── ProfilePhoto.kt        – nuotraukos failas ir FileProvider
├── data/                  – Room: įrašai, DAO, duomenų bazė
└── ui/
    ├── screens/           – HomeScreen, CvScreen, PortfolioScreen, ContactScreen, Dialogs
    └── theme/Theme.kt     – Material 3 spalvos, šriftai, formos
app/src/main/res/values/strings.xml     – angliški tekstai
app/src/main/res/values-lt/strings.xml  – lietuviški tekstai
portfolio/                              – portfolio vaizdai (kraunami iš interneto)
store/                                  – Google Play grafika
PRIVACY.md                              – privatumo politika
```

Naudojama: Android Gradle Plugin 8.13.2, Gradle 8.13, Kotlin 2.0.21, Compose BOM 2024.12.01,
Room 2.6.1, Coil 3.0.4, minSdk 26, targetSdk 36.
