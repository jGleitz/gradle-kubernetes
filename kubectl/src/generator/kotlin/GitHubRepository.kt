package de.joshuagleitze.gradle.kubectl.generator

import de.joshuagleitze.gradle.kubectl.generator.GitHubApi.Companion.MAX_PAGE_SIZE
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.Closeable

class GitHubRepository(val owner: String, val name: String): Closeable {
	private val gitHubApi = GitHubApi()
	fun listTags(): Flow<GitHubTag> = gitHubApi.readAllPages("repos/$owner/$name/tags", pageSize = MAX_PAGE_SIZE)

	override fun close() = gitHubApi.close()
}

@Serializable
data class GitHubTag(
	val name: String,
	@Serializable(with = UrlSerializer::class) @SerialName("zipball_url") val zipballUrl: Url,
	@Serializable(with = UrlSerializer::class) @SerialName("tarball_url") val tarballUrl: Url,
	val commit: GitHubCommit,
	@SerialName("node_id") val nodeId: String
)

@Serializable()
data class GitHubCommit(@SerialName("sha") val shaHash: String, @Serializable(with = UrlSerializer::class) val url: Url)

@Serializer(forClass = Url::class)
object UrlSerializer: KSerializer<Url> {
	override val descriptor get() = PrimitiveSerialDescriptor("UrlString", STRING)

	override fun deserialize(decoder: Decoder) = URLBuilder(decoder.decodeString()).build()
	override fun serialize(encoder: Encoder, value: Url) {
		encoder.encodeString(value.toString())
	}
}
