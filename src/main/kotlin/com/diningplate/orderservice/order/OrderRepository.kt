package com.diningplate.orderservice.order

import com.diningplate.orderservice.model.Order
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OrderRepository : JpaRepository<Order, UUID>
