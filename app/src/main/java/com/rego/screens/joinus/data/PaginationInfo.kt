package com.rego.screens.joinus.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationInfo(
    @SerialName("currentPage")
    val currentPage: Int,
    @SerialName("totalPages")
    val totalPages: Int,
    @SerialName("totalCount")
    val totalCount: Int,
    @SerialName("limit")
    val limit: Int,
    @SerialName("offset")
    val offset: Int,
    @SerialName("hasNextPage")
    val hasNextPage: Boolean,
    @SerialName("hasPrevPage")
    val hasPrevPage: Boolean
)