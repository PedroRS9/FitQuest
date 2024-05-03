package es.ulpgc.pigs.fitquest.screens.shopscreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import es.ulpgc.pigs.fitquest.R
import es.ulpgc.pigs.fitquest.components.ConfirmDialog
import es.ulpgc.pigs.fitquest.components.ErrorDialog
import es.ulpgc.pigs.fitquest.components.FitquestSearchBar
import es.ulpgc.pigs.fitquest.components.ShopItemDisplay
import es.ulpgc.pigs.fitquest.global.UserGlobalConf
import es.ulpgc.pigs.fitquest.navigation.BottomNavigationBar
import es.ulpgc.pigs.fitquest.navigation.TopNavigationBar
import es.ulpgc.pigs.fitquest.data.Result
import es.ulpgc.pigs.fitquest.data.ShopItem
import es.ulpgc.pigs.fitquest.data.User
import es.ulpgc.pigs.fitquest.extensions.fitquestBackground
import es.ulpgc.pigs.fitquest.extensions.playSound
import es.ulpgc.pigs.fitquest.ui.theme.LightGrey

@ExperimentalMaterial3Api
@Composable
fun ShopScreen(navController: NavController, backStackEntry: NavBackStackEntry, userGlobalConf: UserGlobalConf){
    val user by userGlobalConf.currentUser.observeAsState()
    val viewModel: ShopViewModel = viewModel(backStackEntry)
    val chatListState by viewModel.shopState.observeAsState()
    LaunchedEffect(Unit){
        viewModel.getShopItems()
    }
    Scaffold(
        topBar = { TopNavigationBar(navController, title = stringResource(R.string.topbar_shop_title)) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        ShopScreenBody(
            user = user,
            navController = navController,
            paddingValues = paddingValues,
            shopState = chatListState,
            onBuy = { item -> viewModel.onBuyItem(item, user!!) },
            setNormalState = { viewModel.setNormalState() }
        )
    }
}

@Composable
fun ShopScreenBody(
    user: User?,
    navController: NavController,
    paddingValues: PaddingValues,
    shopState: Result?,
    onBuy : (ShopItem) -> Unit,
    setNormalState: () -> Unit
) {
    val query = remember { mutableStateOf("") }
    val shopItems = remember { mutableStateOf<List<ShopItem>>(listOf()) }
    val selectedItem = remember { mutableStateOf<ShopItem?>(null) }
    val showErrorDialog = remember { mutableStateOf(false) }
    val playBuySound = remember { mutableStateOf(false) }
    if(selectedItem.value != null){
        ConfirmDialog(
            title="Confirm Purchase",
            message="Are you sure you want to buy a ${selectedItem.value!!.name}?",
            onConfirm = {
                onBuy(selectedItem.value!!)
                selectedItem.value = null
            },
            onDismiss = { selectedItem.value = null }
        )
    }
    if(showErrorDialog.value){
        ErrorDialog(showDialog = showErrorDialog,
            title = "ERROR",
            message = (shopState as Result.Error).exception.message ?: "",
            onDismiss = {
                setNormalState()
                showErrorDialog.value = false
            }
        )
    }
    if(playBuySound.value){
        setNormalState()
        Log.d("ShopScreen", "Playing buy sound")
        playBuySound.value = false
        playSound(LocalContext.current, R.raw.buy_sound)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .fitquestBackground()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        when (shopState) {
            is Result.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.width(100.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                return
            }
            is Result.ShopSuccess -> {
                shopItems.value = shopState.items
            }
            is Result.Error -> {
                showErrorDialog.value = true
            }
            is Result.BuySuccess -> {
                playBuySound.value = true
                setNormalState()
            }
            else -> { }
        }
        PointsRectangle(points = user?.getPoints() ?: 0, modifier = Modifier.padding(horizontal = 10.dp, vertical=15.dp))
        FitquestSearchBar(query = query, onSearch = {})
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 125.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
        ) {
            items(shopItems.value) { item ->
                ShopItemDisplay(
                    name = item.name,
                    price = item.price,
                    image = rememberAsyncImagePainter(item.image),
                    onClick = {
                        selectedItem.value = item
                    }
                )
            }
        }
    }
}

@Composable
fun PointsRectangle(points: Int, modifier: Modifier = Modifier){
    Box(
        modifier = Modifier
            .wrapContentSize()
            .composed { modifier }
            .clip(RoundedCornerShape(10.dp))
            .background(LightGrey),
    ) {
        Text(text = "Points: $points", fontSize = 20.sp, modifier = Modifier.padding(10.dp))
    }
}

@ExperimentalMaterial3Api
@Composable
@Preview
fun ShopScreenPreview() {
    val navController = rememberNavController()
    val byteArray = byteArrayOf()
    val items = listOf(
        ShopItem(id = "item1", name="Pizza", price = 20, image=byteArray),
        ShopItem(id = "item2", name="Hamburger", price=25, image=byteArray),
        ShopItem(id = "item3", name="Cinema", price=30, image=byteArray)
    )
    val user = User(name = "Paquito", email = "asf", isDoctor = false, password = "", level = 1, points = 100)
    Scaffold(
        topBar = { TopNavigationBar(navController = navController, title = stringResource(R.string.topbar_shop_title)) },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        ShopScreenBody(
            user = user,
            navController = navController,
            paddingValues = paddingValues,
            shopState = Result.ShopSuccess(items),
            onBuy = { },
            setNormalState = { }
        )
    }
}