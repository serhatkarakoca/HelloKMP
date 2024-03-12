import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.resource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        val birdsViewModel = getViewModel(Unit, viewModelFactory { BirdsViewModel() })
        val uiState by birdsViewModel.uiState.collectAsState()

        LaunchedEffect(birdsViewModel) {
            birdsViewModel.updateImages()
        }
        getPlatform().name
        BirdsPage(
            uiState,
            onSelectedCategory = { birdsViewModel.selectCategory(it) },
            onSelectedImage = { birdsViewModel.selectImage(it) })
    }
}

@Composable
fun BirdsPage(
    uiState: BirdsUiState,
    onSelectedCategory: (String) -> Unit,
    onSelectedImage: (BirdImage?) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = uiState.selectedImage != null) {
            Dialog(onDismissRequest = {
                onSelectedImage(null)
            }, content = {
                Box(
                    Modifier.fillMaxSize()
                        .background(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color.Black,
                                    Color.Transparent
                                ),
                                center = Offset.Infinite
                            )
                        ).clickable { onSelectedImage(null) }
                ) {
                    Icon(
                        tint = Color.White,
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.padding(16.dp).align(Alignment.TopEnd).clickable {
                            onSelectedImage(null)
                        }
                    )
                    KamelImage(
                        resource = asyncPainterResource("https://sebi.io/demo-image-api/${uiState.selectedImage?.path}"),
                        contentDescription = "${uiState.selectedImage?.category} by ${uiState.selectedImage?.author}",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().padding(16.dp)
                    )
                }
            }, properties = DialogProperties(usePlatformDefaultWidth = false))
        }


        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                Modifier.fillMaxWidth().padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                for (category in uiState.categories) {
                    Button(
                        onClick = { onSelectedCategory(category) },
                        modifier = Modifier.aspectRatio(1f).weight(1f)
                    ) {
                        Text(category)
                    }
                }

            }

            AnimatedVisibility(visible = uiState.selectedImages.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(uiState.selectedImages) { image ->
                        BirdImageCell(image) {
                            onSelectedImage(it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BirdImageCell(image: BirdImage, onSelectedImage: (BirdImage) -> Unit) {
    KamelImage(
        resource = asyncPainterResource("https://sebi.io/demo-image-api/${image.path}"),
        contentDescription = "${image.category} by ${image.author}",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable {
            onSelectedImage(image)
        }
    )
}

@Composable
fun OpenDialog(image: BirdImage) {

}
