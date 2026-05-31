package com.diningplate.orderservice.model

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class MenuCategoryConverter : AttributeConverter<MenuCategory, Int> {
    override fun convertToDatabaseColumn(attribute: MenuCategory): Int = attribute.value
    override fun convertToEntityAttribute(dbData: Int): MenuCategory = MenuCategory.fromValue(dbData)
}
