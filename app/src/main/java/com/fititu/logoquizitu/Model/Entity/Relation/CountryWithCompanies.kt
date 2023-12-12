package com.fititu.logoquizitu.Model.Entity.Relation

import androidx.room.Embedded
import androidx.room.Relation
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import com.fititu.logoquizitu.Model.Entity.CountryEntity

data class CountryWithCompanies(
    @Embedded val countryEntity: CountryEntity,
    @Relation(
        parentColumn = "name",
        entityColumn = "countryOfOriginName"
    )
    val companies: List<CompanyEntity>
)
