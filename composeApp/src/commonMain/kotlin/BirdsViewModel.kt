import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BirdsViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<BirdsUiState> =
        MutableStateFlow(BirdsUiState(emptyList()))
    val uiState: StateFlow<BirdsUiState> = _uiState.asStateFlow()

    private val httpClient = HttpClient() {
        install(ContentNegotiation) {
            json()
        }
    }

    private suspend fun getImages(): List<BirdImage> =
        httpClient.get("https://sebi.io/demo-image-api/pictures.json")
            .body<List<BirdImage>>()

    fun updateImages() {
        viewModelScope.launch {
            val images = getImages()
            _uiState.update {
                it.copy(images = images)
            }
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { state ->
            if (state.selectedCategory == category) {
                state.copy(selectedCategory = null)
            } else {
                state.copy(selectedCategory = category)
            }
        }
    }

    fun selectImage(image: BirdImage?) {
        _uiState.update { it.copy(selectedImage = image) }
    }

    override fun onCleared() {
        httpClient.close()
    }

}

data class BirdsUiState(
    val images: List<BirdImage>,
    val selectedCategory: String? = null,
    val selectedImage: BirdImage? = null
) {
    val categories = images.map { it.category }.toSet()
    val selectedImages = images.filter { it.category == selectedCategory }
}