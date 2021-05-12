package com.fypmoney.database


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.database.dao.ContactDao
import com.fypmoney.database.dao.InterestDao
import com.fypmoney.database.dao.MemberDao
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.database.entity.MemberEntity
import com.fypmoney.model.InterestEntity


// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(
    entities = [ContactEntity::class, MemberEntity::class, InterestEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao
    abstract fun memberDao(): MemberDao
    abstract fun interestDao(): InterestDao


    companion object {
        private var mAppDatabase: AppDatabase? = null

        /**
        Description:This method is used to get database instance
         **/
        fun getInstance(): AppDatabase? {
            if (mAppDatabase == null) {
                synchronized(AppDatabase::class) {
                    mAppDatabase = Room.databaseBuilder(
                        PockketApplication.instance,
                        AppDatabase::class.java,
                        PockketApplication.instance.getString(
                            R.string.dreamFolksDb
                        )
                    )
                        //.addMigrations(MIGRATION_1_2) // For recover all data from data base after migration
                        //.fallbackToDestructiveMigration()   // For delete all data from data base after migration
                        .build()

                }
            }
            return mAppDatabase
        }
    }
}


