package com.fypmoney.database.dao

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

abstract class BaseDao<T>(
    private val tableName: String
) {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertAll(entities: List<T>): LongArray

    @Update
    abstract fun update(entity: T)

    @Update
    abstract fun updateAll(entities: List<T>)

    @Delete
    abstract fun delete(entity: T)

    @Delete
    abstract fun deleteAllEntities(entities: List<T>)

    @RawQuery
    protected abstract fun deleteAll(query: SupportSQLiteQuery): Int

    fun deleteAll() {
        val query = SimpleSQLiteQuery("DELETE FROM $tableName")
        deleteAll(query)
    }



}
