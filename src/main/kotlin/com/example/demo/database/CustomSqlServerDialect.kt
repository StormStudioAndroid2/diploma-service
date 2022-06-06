package com.example.demo.database

import org.hibernate.boot.MetadataBuilder
import org.hibernate.boot.spi.MetadataBuilderContributor
import org.hibernate.dialect.function.StandardSQLFunction
import org.hibernate.type.StandardBasicTypes


class SqlFunctionsMetadataBuilderContributor : MetadataBuilderContributor {
    override fun contribute(metadataBuilder: MetadataBuilder) {
        metadataBuilder.applySqlFunction(
            "ts_stat",
            StandardSQLFunction(
                "ts_stat",
                StandardBasicTypes.STRING
            )
        )
    }
}