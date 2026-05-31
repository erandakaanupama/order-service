package com.diningplate.orderservice.menu

import com.diningplate.orderservice.api.MenuApiDelegate
import com.diningplate.orderservice.api.model.CreateMenuItemRequest
import com.diningplate.orderservice.api.model.MenuItem
import com.diningplate.orderservice.model.Item
import com.diningplate.orderservice.model.MenuCategory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.math.BigDecimal
import com.diningplate.orderservice.api.model.MenuCategory as ApiMenuCategory

@Service
class MenuService(private val menuRepository: MenuRepository) : MenuApiDelegate {

    override fun createMenuItem(createMenuItemRequest: CreateMenuItemRequest): ResponseEntity<MenuItem> {
        val item = Item(
            name = createMenuItemRequest.name,
            price = BigDecimal.valueOf(createMenuItemRequest.price),
            description = createMenuItemRequest.description ?: "",
            category = MenuCategory.valueOf(createMenuItemRequest.category.name)
        )
        val saved = menuRepository.save(item)
        return ResponseEntity.status(HttpStatus.CREATED).body(saved.toApiModel(createMenuItemRequest.available ?: true))
    }

    private fun Item.toApiModel(available: Boolean) = MenuItem(
        id = id!!,
        name = name,
        price = price.toDouble(),
        category = ApiMenuCategory.valueOf(category.name),
        available = available,
        description = description.ifEmpty { null }
    )
}
