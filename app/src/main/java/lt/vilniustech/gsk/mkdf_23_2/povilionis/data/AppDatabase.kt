package lt.vilniustech.gsk.mkdf_23_2.povilionis.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow

// Užklausos CV įrašams
@Dao
interface CvDao {
    @Query("SELECT * FROM cv_entries ORDER BY id")
    fun getAll(): Flow<List<CvEntry>>

    @Insert
    suspend fun insert(entry: CvEntry)

    @Delete
    suspend fun delete(entry: CvEntry)
}

// Užklausos portfolio įrašams
@Dao
interface PortfolioDao {
    @Query("SELECT * FROM portfolio_items ORDER BY id")
    fun getAll(): Flow<List<PortfolioItem>>

    @Insert
    suspend fun insert(item: PortfolioItem)

    @Delete
    suspend fun delete(item: PortfolioItem)
}

// CvSection saugomas kaip tekstas (pvz. "SKILLS")
class Converters {
    @TypeConverter
    fun fromSection(section: CvSection): String = section.name

    @TypeConverter
    fun toSection(value: String): CvSection = CvSection.valueOf(value)
}

// Duomenų bazė telefono pastoviojoje atmintyje – duomenys išlieka iš naujo paleidus programėlę
@Database(entities = [CvEntry::class, PortfolioItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cvDao(): CvDao
    abstract fun portfolioDao(): PortfolioDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "business_card.db"
                ).build().also { instance = it }
            }
    }
}
