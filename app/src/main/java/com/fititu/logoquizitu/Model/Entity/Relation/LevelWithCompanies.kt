package com.fititu.logoquizitu.Model.Entity.Relation
// Author: Ondřej Vrána (xvrana32)

import androidx.room.Embedded
import androidx.room.Relation
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import com.fititu.logoquizitu.Model.Entity.LevelEntity

data class LevelWithCompanies(
    @Embedded val levelEntity: LevelEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "levelId"
    )
    val companies: List<CompanyEntity>
)
