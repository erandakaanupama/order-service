package com.diningplate.orderservice.model

import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.*

@Embeddable
data class OrderItemId(
    val orderId: UUID = UUID(0, 0),
    val itemId: UUID = UUID(0, 0)
) : Serializable
