package com.diningplate.orderservice.model

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "item")
class Item(
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    val id: UUID? = null,
    val name: String,
    val price: BigDecimal,
    val description: String,
    @Convert(converter = MenuCategoryConverter::class)
    @Column(columnDefinition = "TINYINT")
    val category: MenuCategory
)
