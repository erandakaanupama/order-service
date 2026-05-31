package com.diningplate.orderservice.model

enum class MenuCategory(val value: Int) {
    STARTER(1), MAIN(2), DESSERT(3), BEVERAGE(4), SIDE(5);

    companion object {
        fun fromValue(value: Int): MenuCategory = entries.first { it.value == value }
    }
}
