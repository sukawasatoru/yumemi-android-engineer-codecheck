/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.app.Application
import android.os.Parcelable
import androidx.annotation.Keep
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@HiltViewModel
class OneViewModel @Inject constructor(
    application: Application,
    private val httpClient: HttpClient,
) : AndroidViewModel(application) {
    private val context get() = getApplication<Application>()

    private val _searchResultFlow = MutableSharedFlow<List<Item>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val searchResultFlow: SharedFlow<List<Item>> = _searchResultFlow

    fun search(inputText: String) = viewModelScope.launch {
        val response =
            httpClient.get<RepositorySearchResponse>("https://api.github.com/search/repositories") {
                header("Accept", "application/vnd.github.v3+json")
                parameter("q", inputText)
            }

        val items = mutableListOf<Item>()

        for (jsonItem in response.items) {
            val name = jsonItem.fullName
            val ownerIconUrl = jsonItem.owner.avatarUrl
            val language = jsonItem.language
            val stargazersCount = jsonItem.stargazersCount
            val watchersCount = jsonItem.watchersCount
            val forksCount = jsonItem.forksCount
            val openIssuesCount = jsonItem.openIssuesCount

            items.add(
                Item(
                    name = name,
                    ownerIconUrl = ownerIconUrl,
                    language = context.getString(R.string.written_language, language),
                    stargazersCount = stargazersCount,
                    watchersCount = watchersCount,
                    forksCount = forksCount,
                    openIssuesCount = openIssuesCount
                )
            )
        }

        _searchResultFlow.emit(items)
    }
}

@Keep
@Parcelize
data class Item(
    val name: String,
    val ownerIconUrl: String,
    val language: String,
    val stargazersCount: Long,
    val watchersCount: Long,
    val forksCount: Long,
    val openIssuesCount: Long,
) : Parcelable

@Serializable
data class RepositorySearchResponse(
    val items: List<RepositoryItemResponse>,
)

@Serializable
data class RepositoryItemResponse(
    @SerialName("full_name")
    val fullName: String,
    val owner: OwnerResponse,
    val language: String?,
    @SerialName("stargazers_count")
    val stargazersCount: Long,
    @SerialName("watchers_count")
    val watchersCount: Long,
    @SerialName("forks_count")
    val forksCount: Long,
    @SerialName("open_issues_count")
    val openIssuesCount: Long,
)

@Serializable
data class OwnerResponse(
    @SerialName("avatar_url")
    val avatarUrl: String,
)