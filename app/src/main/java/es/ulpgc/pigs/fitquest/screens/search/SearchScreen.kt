package es.ulpgc.pigs.fitquest.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import es.ulpgc.pigs.fitquest.R
import es.ulpgc.pigs.fitquest.components.FitquestProfilePicture
import es.ulpgc.pigs.fitquest.data.SearchResult
import es.ulpgc.pigs.fitquest.extensions.fitquestBackground
import es.ulpgc.pigs.fitquest.navigation.BottomNavigationBar

@Composable
fun SearchScreen(navController: NavController, backStackEntry: NavBackStackEntry){
    val viewModel: SearchViewModel = viewModel(backStackEntry)
    val searchStateObserver by viewModel.searchState.observeAsState()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        paddingValues /* TODO: Remove this line. paddingValues should be passed as a parameter */
        SearchBodyContent(
            navController = navController,
            onSearch = { query -> viewModel.onSearch(query) },
            searchState = searchStateObserver
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBodyContent(
    navController: NavController,
    onSearch: (String) -> Unit,
    searchState: SearchResult? = null
){
    Column(
        modifier = Modifier.fitquestBackground(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        var query by remember { mutableStateOf("") }
        var active by remember { mutableStateOf(false) }
        SearchBar(
            placeholder = { Text(text = "Search user") },
            query = query,
            onQueryChange = { query = it },
            onSearch = {
                onSearch(query)
                active = false
            },
            active = active,
            onActiveChange = { active = it },
            trailingIcon = {
                IconButton(onClick = { onSearch(query) }, enabled = query.isNotEmpty() ){
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_search),
                        contentDescription = "Search user"
                    )
                }
            },
            colors = SearchBarDefaults.colors(
                containerColor = Color(0xFFE2CCFC)
            ),
        ){
            when(searchState){
                is SearchResult.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.width(100.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
                is SearchResult.ShowResults -> {
                    searchState.results.forEach { user ->
                        Row(
                            modifier = Modifier
                                .padding(6.dp)
                                .padding(start = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre elementos
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val painter = if (user.hasProfilePicture()) {
                                rememberAsyncImagePainter(user.getPicture()!!)
                            } else {
                                painterResource(id = R.drawable.default_profile_pic)
                            }
                            FitquestProfilePicture(
                                userProfileImage = painter,
                                modifier = Modifier.size(50.dp)
                            )
                            // Username
                            Text(
                                text = user.getName(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                is SearchResult.NoResults -> {
                    Text(
                        text = "No results found",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                else -> {}
            }
        }

    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    SearchBodyContent(navController = NavController(LocalContext.current), onSearch = {})
}