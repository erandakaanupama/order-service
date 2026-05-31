package com.diningplate.orderservice.model

import jakarta.persistence.*

@Entity
@Table(name = "order_item")
class OrderItem(
    @EmbeddedId
    val id: OrderItemId = OrderItemId(),
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    val order: Order,
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("itemId")
    @JoinColumn(name = "item_id")
    val item: Item,
    val count: Int
)
