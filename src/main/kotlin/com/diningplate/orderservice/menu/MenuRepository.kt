package com.diningplate.orderservice.menu

import com.diningplate.orderservice.model.Item
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MenuRepository : JpaRepository<Item, UUID>
