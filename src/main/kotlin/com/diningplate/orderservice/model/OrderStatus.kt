package com.diningplate.orderservice.model

enum class OrderStatus(val value: Int) {
    NEW(1), ACCEPTED(2), READY(3), DISPATCHED(4), COMPLETED(5);

    companion object {
        fun fromValue(value: Int): OrderStatus = entries.first { it.value == value }
    }
}
