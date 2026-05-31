package com.diningplate.orderservice.order

import com.diningplate.orderservice.model.OrderItem
import com.diningplate.orderservice.model.OrderItemId
import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemRepository : JpaRepository<OrderItem, OrderItemId>
