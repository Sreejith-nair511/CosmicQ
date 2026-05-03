package com.example.cosmoq1.repository

import com.example.cosmoq1.data.CardCategory
import com.example.cosmoq1.data.SwipeCard
import com.example.cosmoq1.db.AppDatabase
import com.example.cosmoq1.db.SavedCardEntity
import com.example.cosmoq1.network.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SwipeRepository(private val db: AppDatabase) {

    // ---- Remote ----

    suspend fun fetchCards(): List<SwipeCard> = coroutineScope {
        val newsDeferred   = async { runCatching { fetchNews() }.getOrElse { emptyList() } }
        val apodDeferred   = async { runCatching { fetchApod() }.getOrElse { null } }
        val spaceXDeferred = async { runCatching { fetchSpaceX() }.getOrElse { null } }

        val cards = mutableListOf<SwipeCard>()
        cards.addAll(newsDeferred.await())
        apodDeferred.await()?.let { cards.add(it) }
        spaceXDeferred.await()?.let { cards.add(it) }
        cards.shuffled()
    }

    private suspend fun fetchNews(): List<SwipeCard> =
        RetrofitClient.spaceNewsApi.getArticles(limit = 20).results.map { article ->
            SwipeCard(
                id          = "news_${article.id}",
                title       = article.title,
                summary     = article.summary.take(200),
                imageUrl    = article.imageUrl,
                category    = CardCategory.NEWS,
                source      = article.newsSite,
                publishedAt = article.publishedAt.take(10),
                url         = article.url
            )
        }

    private suspend fun fetchApod(): SwipeCard {
        val apod = RetrofitClient.nasaApi.getApod()
        return SwipeCard(
            id          = "apod_${apod.date}",
            title       = apod.title,
            summary     = apod.explanation.take(200),
            imageUrl    = if (apod.mediaType == "image") apod.url else "",
            category    = CardCategory.NASA,
            source      = "NASA APOD",
            publishedAt = apod.date,
            url         = apod.url
        )
    }

    private suspend fun fetchSpaceX(): SwipeCard {
        val launch = RetrofitClient.spaceXApi.getLatestLaunch()
        return SwipeCard(
            id          = "spacex_${launch.id}",
            title       = "SpaceX: ${launch.name}",
            summary     = launch.details?.take(200) ?: "Latest SpaceX rocket launch mission.",
            imageUrl    = launch.links?.patch?.large ?: launch.links?.patch?.small ?: "",
            category    = CardCategory.ROCKET,
            source      = "SpaceX",
            publishedAt = launch.dateUtc.take(10),
            url         = launch.links?.article ?: launch.links?.webcast ?: ""
        )
    }

    // ---- Local (Room) ----

    fun getSavedCards(): Flow<List<SwipeCard>> =
        db.savedCardDao().getAllSaved().map { entities ->
            entities.map { it.toSwipeCard() }
        }

    suspend fun saveCard(card: SwipeCard) {
        db.savedCardDao().insert(card.toEntity())
    }

    suspend fun removeCard(id: String) {
        db.savedCardDao().deleteById(id)
    }

    suspend fun isSaved(id: String): Boolean = db.savedCardDao().isSaved(id)

    // ---- Mappers ----

    private fun SavedCardEntity.toSwipeCard() = SwipeCard(
        id          = id,
        title       = title,
        summary     = summary,
        imageUrl    = imageUrl,
        category    = CardCategory.valueOf(category),
        source      = source,
        publishedAt = publishedAt,
        url         = url
    )

    private fun SwipeCard.toEntity() = SavedCardEntity(
        id          = id,
        title       = title,
        summary     = summary,
        imageUrl    = imageUrl,
        category    = category.name,
        source      = source,
        publishedAt = publishedAt,
        url         = url
    )
}
