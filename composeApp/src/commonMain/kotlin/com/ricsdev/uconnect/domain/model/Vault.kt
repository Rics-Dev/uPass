package com.ricsdev.uconnect.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Vault(
    val encrypted: Boolean?,
    val folders: List<Folder>?,
    val items: List<Item>?
)

@Serializable
data class Folder(
    val id: String?,
    val name: String?
)

@Serializable
data class Item(
    val passwordHistory: List<PasswordHistory>? = null,
    val revisionDate: String? = null,
    val creationDate: String? = null,
    val deletedDate: String? = null,
    val id: String? = null,
    val organizationId: String? = null,
    val folderId: String? = null,
    val type: Int? = null,
    val reprompt: Int? = null,
    val name: String? = null,
    val notes: String? = null,
    val favorite: Boolean? = null,
    val login: Login? = null // Default value set to null
)

@Serializable
data class PasswordHistory(
    val lastUsedDate: String?,
    val password: String?
)

@Serializable
data class Login(
    val fido2Credentials: List<String>?,
    val uris: List<Uri>?,
    val username: String?,
    val password: String?,
    val totp: String?
)

@Serializable
data class Uri(
    val match: String?,
    val uri: String?
)