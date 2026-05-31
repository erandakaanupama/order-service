package com.diningplate.orderservice.exception

import java.util.UUID

class ResourceNotFoundException(val unavailableIds: List<UUID>) :
    RuntimeException("Items not available: ${unavailableIds.joinToString()}")
