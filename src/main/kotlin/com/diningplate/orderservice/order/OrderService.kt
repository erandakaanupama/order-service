package com.diningplate.orderservice.order

import com.diningplate.orderservice.api.OrdersApiDelegate
import com.diningplate.orderservice.api.model.OrderPage
import com.diningplate.orderservice.model.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*
import com.diningplate.orderservice.api.model.Order as ApiOrder
import com.diningplate.orderservice.api.model.OrderItem as ApiOrderItem
import com.diningplate.orderservice.api.model.OrderStatus as ApiOrderStatus

@Service
class OrderService(private val orderRepository: OrderRepository) : OrdersApiDelegate {

    override fun listOrders(
        status: ApiOrderStatus?,
        date: LocalDate?,
        page: Int,
        size: Int
    ): ResponseEntity<OrderPage> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

    override fun getOrder(orderId: UUID): ResponseEntity<ApiOrder> {
        val order = orderRepository.findById(orderId).orElse(null)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity.ok(order.toApiModel())
    }

    private fun Order.toApiModel(): ApiOrder {
        val apiItems = items.map { oi ->
            ApiOrderItem(
                menuItemId = oi.item.id!!,
                name = oi.item.name,
                quantity = oi.count,
                unitPrice = oi.item.price.toDouble()
            )
        }
        val total = items.sumOf { it.count * it.item.price.toDouble() }
        val ts = dateTime.atOffset(ZoneOffset.UTC)
        return ApiOrder(
            id = id,
            customerId = customer.id,
            items = apiItems,
            status = ApiOrderStatus.valueOf(status.name),
            totalAmount = total,
            createdAt = ts,
            updatedAt = ts,
            location = customer.mapLocation
        )
    }
}
