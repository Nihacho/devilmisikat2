package com.example.proyectofinal.data.model

import com.google.gson.annotations.SerializedName

data class XtreamAuthResponse(
    @SerializedName("user_info") val userInfo: XtreamUserInfo,
    @SerializedName("server_info") val serverInfo: XtreamServerInfo
)

data class XtreamUserInfo(
    val username: String,
    val password: String,
    val status: String,
    val exp_date: String?
)

data class XtreamServerInfo(
    val url: String,
    val port: String,
    val https_port: String,
    val server_protocol: String,
    val rtmp_port: String,
    val timezone: String,
    val timestamp_now: Int,
    val time_now: String,
    val process: Boolean
)

data class XtreamStream(
    @SerializedName("num") val num: Int,
    @SerializedName("name") val name: String,
    @SerializedName("stream_type") val streamType: String,
    @SerializedName("stream_id") val streamId: Int,
    @SerializedName("stream_icon") val streamIcon: String?,
    @SerializedName("epg_channel_id") val epgChannelId: String?,
    @SerializedName("added") val added: String,
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("custom_sid") val customSid: String?,
    @SerializedName("tv_archive") val tvArchive: Int,
    @SerializedName("direct_source") val directSource: String?,
    @SerializedName("tv_archive_duration") val tvArchiveDuration: Int,
    @SerializedName("container_extension") val containerExtension: String?
)
