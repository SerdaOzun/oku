package com.okuread.db.data

data class ActivateResponse(
    val activated: Boolean,
    val error: String?,
    val license_key: LicenseKey?,
    val instance: Instance?,
    val meta: Meta?
)

data class LicenseKey(
    val id: Long?,
    val status: String?,
    val key: String?,
    val activation_limit: Int?,
    val activation_usage: Int?,
    val created_at: String?,
    val expires_at: String?,
)

data class Instance(
    val id: String?,
    val name: String?,
    val created_at: String?
)

data class Meta(
    val store_id: Int?,
    val order_id: Int?,
    val order_item_id: Int?,
    val product_id: Int?,
    val product_name: String?,
    val variant_id: Int?,
    val variant_name: String?,
    val customer_id: Int?,
    val customer_name: String?,
    val customer_email: String?
)