package de.joshuagleitze.gradle.kubectl.generator

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpHeaders.Link
import io.ktor.http.LinkHeader.Rel.Next
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import java.io.Closeable

class GitHubApi: Closeable {
	val client = HttpClient(CIO) {
		expectSuccess = true
		install(Logging) {
			level = LogLevel.NONE
		}
		install(JsonFeature) {
			serializer = KotlinxSerializer()
		}
		defaultRequest {
			url {
				protocol = URLProtocol.HTTPS
				host = "api.github.com"
			}
			header(HttpHeaders.Accept, "application/vnd.github.v3+json")
		}
	}

	inline fun <reified T> readAllPages(
		path: String,
		pageSize: Int = DEFAULT_PAGE_SIZE,
		crossinline block: HttpRequestBuilder.() -> Unit = {}
	): Flow<T> = flow {
		require(pageSize <= MAX_PAGE_SIZE) {
			"The page size must not exceed $MAX_PAGE_SIZE (was $pageSize)!"
		}
		var nextTarget: (URLBuilder.() -> Unit)? = {
			path(path)
			parameters["per_page"] = pageSize.toString()
		}
		while (nextTarget != null) {
			val target = nextTarget
			val result = client.get<HttpResponse> {
				apply(block)
				url.apply(target)
			}
			emit(result.receive<List<T>>().asFlow())
			nextTarget = getNextPageUrl(result)
				?.let { nextUrl -> { takeFrom(nextUrl) } }
		}
	}
		.flattenConcat()

	override fun close() = client.close()

	companion object {
		const val DEFAULT_PAGE_SIZE = 30
		const val MAX_PAGE_SIZE = 100
		fun getNextPageUrl(response: HttpResponse) =
			response.headers.getAll(Link)
				?.flatMap { it.split(",") }
				?.map { HeaderValueWithParameters.parse(it, ::LinkHeader) }
				?.find { link -> link.parameters.any { it.value == Next } }
				?.let { link -> unpackLinkUrl(link.uri) }

		private fun unpackLinkUrl(url: String) = url.trim().dropWhile { it == '<' }.dropLastWhile { it == '>' }

		inline fun rethrowOnlyIfActive(context: CoroutineScope, block: () -> Unit) = try {
			block()
		} catch (error: Exception) {
			if (context.isActive) throw error
			else Unit // swallow error
		}
	}
}
