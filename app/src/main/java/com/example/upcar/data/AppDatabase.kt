package com.example.upcar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split(",")
    }
}

@Database(
    entities = [
        Motorista::class,
        Carona::class,
        Mensagem::class,
        Passageiro::class,
        AvaliacaoCorrida::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun motoristaDao(): MotoristaDao
    abstract fun caronaDao(): CaronaDao
    abstract fun mensagemDao(): MensagemDao
    abstract fun passageiroDao(): PassageiroDao
    abstract fun avaliacaoDao(): AvaliacaoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "upcar_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
