import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable
class MenuCategory(val menu: List<String>)

private val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(contentType = ContentType("text", "plain"))
    }
}

private suspend fun getMenu(category: String): List<String> {
    return try {
        val response: Map<String, MenuCategory> =
            client
                .get("https://raw.githubusercontent.com/Meta-Mobile-Developer-PC/Working-With-Data-API/main/littleLemonMenu.json")
                .body() ?: emptyMap()
        response[category]?.menu ?: emptyList()
    } catch (e: ClientRequestException) {
        println("Error fetching data: ${e.response.status.description}")
        emptyList()
    }
}

private suspend fun fetchAndPrintMenu(category: String) {
    println("$category:")
    val menu = getMenu(category)
    menu.forEach(::println)
}

fun main() {
    runBlocking {
        val categories = listOf("Drinks", "Dessert", "Appetizers", "Salads")
        val jobs = categories.map { category ->
            launch {
                fetchAndPrintMenu(category)
            }
        }
        jobs.joinAll()
    }
}