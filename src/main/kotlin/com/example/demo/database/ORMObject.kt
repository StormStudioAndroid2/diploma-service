package com.example.demo.database

import org.hibernate.Hibernate
import java.util.UUID

abstract class ORMObject(open val id: UUID) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ORMObject

        return id == other.id
    }

    override fun hashCode(): Int = 1756406093
}