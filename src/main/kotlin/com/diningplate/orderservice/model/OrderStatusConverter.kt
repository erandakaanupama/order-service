package com.diningplate.orderservice.model

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class OrderStatusConverter : AttributeConverter<OrderStatus, Int> {
    override fun convertToDatabaseColumn(attribute: OrderStatus): Int = attribute.value
    override fun convertToEntityAttribute(dbData: Int): OrderStatus = OrderStatus.fromValue(dbData)
}
