package com.diningplate.orderservice.order

import com.diningplate.orderservice.model.Customer
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CustomerRepository : JpaRepository<Customer, UUID> {
    fun findByContactNo(contactNo: String): Customer?
}
