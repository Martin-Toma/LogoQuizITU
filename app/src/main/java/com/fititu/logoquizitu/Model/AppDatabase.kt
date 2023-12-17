package com.fititu.logoquizitu.Model

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fititu.logoquizitu.Model.Dao.CategoryDao
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Dao.CountryDao
import com.fititu.logoquizitu.Model.Dao.GlobalProfileDao
import com.fititu.logoquizitu.Model.Dao.LevelDao
import com.fititu.logoquizitu.Model.Entity.CategoryEntity
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import com.fititu.logoquizitu.Model.Entity.Conv
import com.fititu.logoquizitu.Model.Entity.CountryEntity
import com.fititu.logoquizitu.Model.Entity.GlobalProfileEntity
import com.fititu.logoquizitu.Model.Entity.LevelEntity
import com.fititu.logoquizitu.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@TypeConverters(Conv::class)
@Database(
    entities = [
        LogoEntity::class,
        CategoryEntity::class,
        CompanyEntity::class,
        CountryEntity::class,
        GlobalProfileEntity::class,
        LevelEntity::class,
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun logoEntityDao(): LogoEntityDao
    abstract fun categoryDao(): CategoryDao
    abstract fun companyDao(): CompanyDao
    abstract fun countryDao(): CountryDao
    abstract fun globalProfileDao(): GlobalProfileDao
    abstract fun levelDao(): LevelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DbConstants.DB_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun initDb(context: Context, lifecycleCoroutineScope: LifecycleCoroutineScope) {
            val ins = getInstance(context)

            // check if initialization is necessary, by fetching profile
            val profileDao = ins.globalProfileDao()
            val levelDao = ins.levelDao()
            val categoryDao = ins.categoryDao()
            val companyDao = ins.companyDao()
            val countryDao = ins.countryDao()

            val dateParser = SimpleDateFormat("yyyy")

            val categories = listOf(
                CategoryEntity("E-commerce", R.drawable.e_commerce),
                CategoryEntity("Fast-food", R.drawable.fast_food),
                CategoryEntity("Furniture", R.drawable.furniture),
                CategoryEntity("Technology", R.drawable.technology),
            )

            val companies = listOf<CompanyEntity>(
                CompanyEntity(
                    0,
                    "",
                    "",
                    R.drawable.amazon_logo_hidden,
                    R.drawable.amazon_logo,
                    false,
                    "Amazon",
                    "American multinational technology company focusing on " +
                            "e-commerce, cloud computing, online advertising, digital streaming, " +
                            "and artificial intelligence.",
                    dateParser.parse("1994")!!,
                    false,
                    null,
                    "United States",
                    "E-commerce",
                    1
                ),
                CompanyEntity(
                    0,
                    "",
                    "",
                    R.drawable.burger_king_logo_hidden,
                    R.drawable.burger_king_logo,
                    false,
                    "Burger King",
                    "American multinational chain of hamburger fast food " +
                            "restaurants. Headquartered in Miami-Dade County, Florida.",
                    dateParser.parse("1953")!!,
                    false,
                    null,
                    "United States",
                    "Fast-food",
                    2
                ),
                CompanyEntity(
                    0,
                    "",
                    "",
                    R.drawable.fanta_logo_hidden,
                    R.drawable.fanta_logo,
                    false,
                    "Fanta",
                    "Fanta is an American-owned brand of fruit-flavored " +
                            "carbonated soft drink created by Coca-Cola Deutschland under the" +
                            " leadership of German businessman Max Keith.",
                    dateParser.parse("1940")!!,
                    false,
                    null,
                    "Germany",
                    "Food",
                    1
                ),
                CompanyEntity(
                    0,
                    "",
                    "",
                    R.drawable.ikea_logo_hidden,
                    R.drawable.ikea_logo,
                    false,
                    "IKEA",
                    "Swedish multinational conglomerate that designs and sells" +
                            "ready-to-assemble furniture, kitchen appliances, decoration, home " +
                            "accessories, and various other goods and home services.",
                    dateParser.parse("1943")!!,
                    false,
                    null,
                    "Sweden",
                    "Furniture",
                    2
                ),
                CompanyEntity(
                    0,
                    "",
                    "",
                    R.drawable.mcdonalds_logo_hidden,
                    R.drawable.mcdonalds_logo,
                    false,
                    "McDonald's",
                    "American multinational fast food chain, founded " +
                            "in 1940 as a restaurant operated by Richard and Maurice McDonald",
                    dateParser.parse("1955")!!,
                    false,
                    null,
                    "United States",
                    "Fast-food",
                    1
                ),
                CompanyEntity(
                    0,
                    "",
                    "",
                    R.drawable.microsoft_logo_hidden,
                    R.drawable.microsoft_logo_hidden,
                    false,
                    "Microsoft",
                    "American multinational technology corporation " +
                            "headquartered in Redmond, Washington. Microsoft's best-known" +
                            " software products are the Windows line of operating systems," +
                            " the Microsoft 365 suite of productivity applications, " +
                            "and the Edge web browser.",
                    dateParser.parse("1975")!!,
                    false,
                    null,
                    "United States",
                    "Technology",
                    1
                )
            )

            val countries = listOf(
                CountryEntity("United States"),
                CountryEntity("Germany"),
                CountryEntity("Sweden"),
            )

            val levels = listOf(
                LevelEntity(1),
                LevelEntity(2),
                LevelEntity(3),
                LevelEntity(4),
                LevelEntity(5)
            )

            val globalProfileEntity = GlobalProfileEntity(
                0, 5, 0, 0, -1, "", "", "", ""
            )


            lifecycleCoroutineScope.launch {
                val profileList = profileDao.get()
                if (profileList.isEmpty()) {
                    levels.forEach { levelDao.add(it) }
                    categories.forEach { categoryDao.add(it) }
                    countries.forEach { countryDao.add(it) }
                    companies.forEach { companyDao.add(it) }
                    profileDao.add(globalProfileEntity)
                }
            }
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE logo_entity ADD COLUMN imageBitmap BLOB NOT NULL DEFAULT ''")
            }
        }
        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //database.execSQL("DROP TABLE logo_entity")
                //database.execSQL("CREATE TABLE logo_entity (id INTEGER PRIMARY KEY AUTOINCREMENT, imagePath TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, imageBitmap BLOB DEFAULT '')")
                database.execSQL("CREATE TABLE temp_table " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "imagePath TEXT NOT NULL, " +
                        "name TEXT NOT NULL, " +
                        "description TEXT NOT NULL)")

                // Copy data from the old table to the temporary table
                database.execSQL("INSERT INTO temp_table (imagePath, name, description) SELECT imagePath, name, description FROM logo_entity")

                // Drop the old table
                database.execSQL("DROP TABLE logo_entity")

                // Rename the temporary table to the original table name
                database.execSQL("ALTER TABLE temp_table RENAME TO logo_entity")
            }
        }
    }
}


