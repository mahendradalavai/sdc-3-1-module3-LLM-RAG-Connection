package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rag.DocumentChunker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        DocumentEntity::class,
        DocumentChunkEntity::class,
        ComparisonHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rag_knowledge_db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Prepopulate with rich sample knowledge documents
                            scope.launch(Dispatchers.IO) {
                                INSTANCE?.let { appDb ->
                                    val docDao = appDb.documentDao()
                                    for (doc in PreloadedData.sampleDocuments) {
                                        val docId = docDao.insertDocument(doc)
                                        val savedDoc = doc.copy(id = docId)
                                        val chunks = DocumentChunker.chunkDocument(savedDoc)
                                        docDao.insertChunks(chunks)
                                    }
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
