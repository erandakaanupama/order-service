package com.diningplate.orderservice.order

import com.diningplate.orderservice.api.OrdersApiDelegate
import com.diningplate.orderservice.api.model.CreateOrderRequest
import com.diningplate.orderservice.api.model.OrderPage
import com.diningplate.orderservice.exception.ResourceNotFoundException
import com.diningplate.orderservice.menu.MenuRepository
import com.diningplate.orderservice.model.Customer
import com.diningplate.orderservice.model.Order
import com.diningplate.orderservice.model.OrderItem
import com.diningplate.orderservice.model.OrderStatus
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import com.diningplate.orderservice.api.model.Order as ApiOrder
import com.diningplate.orderservice.api.model.OrderItem as ApiOrderItem
import com.diningplate.orderservice.api.model.OrderStatus as ApiOrderStatus

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository,
    private val menuRepository: MenuRepository,
    private val orderItemRepository: OrderItemRepository,
) : OrdersApiDelegate {

    override fun listOrders(
        status: ApiOrderStatus?,
        date: LocalDate?,
        page: Int,
        size: Int,
    ): ResponseEntity<OrderPage> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

    override fun getOrder(orderId: UUID): ResponseEntity<ApiOrder> {
        val order = orderRepository.findById(orderId).orElse(null)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity.ok(order.toApiModel())
    }

    override fun createOrder(createOrderRequest: CreateOrderRequest): ResponseEntity<ApiOrder> {
        val requestedIds = createOrderRequest.items.map { it.menuItemId }.distinct()
        val availableItems = menuRepository.findByIdInAndAvailable(requestedIds, true)
        val availableItemsSet = availableItems.map { it.id }.toSet()

        if (!availableItemsSet.containsAll(requestedIds)) {
            val unavailable = requestedIds.filter { it !in availableItemsSet }
            throw ResourceNotFoundException(unavailable)
        }

        val qtyByItemId = createOrderRequest.items.associate { it.menuItemId to it.quantity }
        val itemsWithQty = availableItems.map { it to qtyByItemId[it.id]!! }

        val customer = customerRepository.findByContactNo(createOrderRequest.mobile)
            ?: customerRepository.save(
                Customer(
                    name = createOrderRequest.name,
                    contactNo = createOrderRequest.mobile,
                    mapLocation = createOrderRequest.location
                )
            )
        val order = orderRepository.save(
            Order(dateTime = LocalDateTime.now(), customer = customer, status = OrderStatus.NEW)
        )
        itemsWithQty.forEach { (item, qty) ->
            orderItemRepository.save(OrderItem(order = order, item = item, count = qty))
        }

        val apiItems = itemsWithQty.map { (item, qty) ->
            ApiOrderItem(
                menuItemId = item.id!!,
                name = item.name,
                quantity = qty,
                unitPrice = item.price.toDouble()
            )
        }
        val total = itemsWithQty.sumOf { (item, qty) -> qty * item.price.toDouble() }
        val ts = order.dateTime.atOffset(ZoneOffset.UTC)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiOrder(
                id = order.id!!,
                customerId = customer.id!!,
                items = apiItems,
                status = ApiOrderStatus.NEW,
                totalAmount = total,
                createdAt = ts,
                updatedAt = ts,
                location = customer.mapLocation
            )
        )
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
            id = id!!,
            customerId = customer.id!!,
            items = apiItems,
            status = ApiOrderStatus.valueOf(status.name),
            totalAmount = total,
            createdAt = ts,
            updatedAt = ts,
            location = customer.mapLocation
        )
    }
}
