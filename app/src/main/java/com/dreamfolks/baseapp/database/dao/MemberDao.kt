package com.dreamfolks.baseapp.database.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dreamfolks.baseapp.database.entity.MemberEntity

@Dao
abstract class MemberDao : BaseDao<MemberEntity>("member_entity"){
    // insert members
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllMembers(list: List<MemberEntity>)


    // fetch all members and return as a list of entity
    @Query("SELECT * FROM member_entity")
    abstract suspend fun getAllMembers(): List<MemberEntity>


    //delete members
    @Query("DELETE FROM member_entity")
    abstract suspend fun deleteAllMembers()

}