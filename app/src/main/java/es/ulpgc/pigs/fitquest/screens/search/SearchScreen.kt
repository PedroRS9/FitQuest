package es.ulpgc.pigs.fitquest.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import es.ulpgc.pigs.fitquest.R
import es.ulpgc.pigs.fitquest.components.FitquestSearchBar
import es.ulpgc.pigs.fitquest.data.SearchResult
import es.ulpgc.pigs.fitquest.data.User
import es.ulpgc.pigs.fitquest.extensions.fitquestBackground
import es.ulpgc.pigs.fitquest.navigation.BottomNavigationBar
import es.ulpgc.pigs.fitquest.navigation.TopNavigationBar
import androidx.compose.foundation.lazy.items
import es.ulpgc.pigs.fitquest.components.UserItemList
import es.ulpgc.pigs.fitquest.navigation.AppScreens

@ExperimentalMaterial3Api
@Composable
fun SearchScreen(navController: NavController, backStackEntry: NavBackStackEntry){
    val viewModel: SearchViewModel = viewModel(backStackEntry)
    val searchStateObserver by viewModel.searchState.observeAsState()
    Scaffold(
        topBar = { TopNavigationBar(navController = navController, title = stringResource(R.string.topbar_search_title))},
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        SearchBodyContent(
            navController = navController,
            onSearch = { query -> viewModel.onSearch(query) },
            searchState = searchStateObserver,
            paddingValues = paddingValues
        )
    }
}

@Composable
fun SearchBodyContent(
    navController: NavController,
    onSearch: (String) -> Unit,
    searchState: SearchResult? = null,
    paddingValues: PaddingValues
){
    Column(
        modifier = Modifier
            .fitquestBackground()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        var query = remember { mutableStateOf("") }
        var active by remember { mutableStateOf(false) }
        FitquestSearchBar(
            placeholderText = "Search user",
            query = query,
            onSearch = { searchQuery ->
                onSearch(searchQuery)
                active = false
            }
        )
        when(searchState){
            is SearchResult.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.width(100.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                return
            }
            is SearchResult.Results -> {
                LazyColumn {
                    items(searchState.results) { user ->
                        val painter = if (user.hasProfilePicture()) {
                            rememberAsyncImagePainter(user.getPicture()!!)
                        } else {
                            painterResource(id = R.drawable.default_profile_pic)
                        }
                        UserItemList(
                            profilePicture = painter,
                            username = user.getName(),
                            onClick = {
                                navController.navigate(AppScreens.DifferentProfileScreen.createRoute(user.getName()))
                            }
                        )
                    }
                }
            }
            is SearchResult.ShowError -> {
                Text(
                    text = "${searchState.exception.message}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            else -> {}
        }
    }
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun SearchScreenPreview() {
    val navController = rememberNavController()
    val fakeUsers = listOf(
        User(name = "Paquito", email = "", password = "", isDoctor = false),
        User(name = "Pepito", email = "", password = "", isDoctor = false),
        User(name = "Pablito", email = "", password = "", isDoctor = false),
        User(name = "Pepita", email = "", password = "", isDoctor = false),
    )
    Scaffold(
        topBar = { TopNavigationBar(navController = navController, title = stringResource(R.string.topbar_search_title))},
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        SearchBodyContent(
            navController = navController,
            onSearch = { },
            searchState = SearchResult.Results(fakeUsers),
            paddingValues = paddingValues
        )
    }
}