package com.fititu.logoquizitu.Model.Entity.Relation

import androidx.room.Embedded
import androidx.room.Relation
import com.fititu.logoquizitu.Model.Entity.CategoryEntity
import com.fititu.logoquizitu.Model.Entity.CompanyEntity

data class CategoryWithCompanies(
    @Embedded val category: CategoryEntity,
    @Relation(
        parentColumn = "name",
        entityColumn = "categoryName"
    )
    val companies: List<CompanyEntity>
)
