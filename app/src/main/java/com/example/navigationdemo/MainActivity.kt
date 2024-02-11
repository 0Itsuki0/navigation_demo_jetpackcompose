@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.navigationdemo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.navigationdemo.ui.theme.NavigationDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationDemoTheme {
                ContentView()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentView() {
    val navController = rememberNavController()

    var canNavigateBack by remember { mutableStateOf(false) }
    navController.addOnDestinationChangedListener{ controller, _, _ ->
        canNavigateBack = controller.previousBackStackEntry != null
    }


//    val backStackEntry by navController.currentBackStackEntryAsState()
//
//    val currentScreen = Route.valueOf(
//        backStackEntry?.destination?.route ?: Route.Start.name
//    )
//    val currentScreenTitle = currentScreen.title

    val onItemSelect:(Int) -> Unit = {
        val routeName = when(it) {
            1 -> Route.First.name
            2 -> "${Route.Second.name}/1"
            3 -> Route.Third.name
            4 -> "${Route.Third.name}?index=1"
            5 -> "${Route.Third.name}?index=2"
            else -> Route.First.name
        }
        navController.navigate(route = routeName)
    }
    Log.d("test", (navController.previousBackStackEntry).toString())

    Scaffold(
        topBar = {
            TopBar(
                title = "some title",
                canNavigateBack = canNavigateBack,
                onBackButtonPress = {
//                    navController.navigateUp()
                    navController.popBackStack(
                        destinationId = navController.graph.findStartDestination().id,
                        inclusive = false
                    )
                })
        }
    ) {padding->
        NavHost(
            navController = navController,
            startDestination = Route.Start.name,
            modifier = Modifier
                .padding(padding)
        ) {
            composable(route = Route.Start.name) {
                StartScreen(onItemSelect = onItemSelect)
            }

            composable(route = Route.First.name) {
                FirstScreen(
                    title = "first"
                )
            }

            composable(
                route = "${Route.Second.name}/{index}",
                arguments = listOf(
                    navArgument("index") {
                        type = NavType.IntType
                        nullable = false
                        defaultValue = 1
                    }
                )
            ) {entry->
                val index = entry.arguments?.getInt("index")
                SecondScreen(
                    index = index ?: 1
                )
            }


            composable(route = "${Route.Third.name}?index={index}",
                arguments = listOf(
                    navArgument("index") {
                        defaultValue = 123
                    })
            ) { entry->
                val index = entry.arguments?.getInt("index")
                ThirdScreen(
                    index = index ?: 1
                )
            }

        }
    }
}

@Composable
fun StartScreen(onItemSelect: (Int) -> Unit) {
    val itemList = listOf<String>(
        "To first screen",
        "To second screen",
        "To third screen",
        "To 3-1",
        "To 3-2"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        itemList.forEachIndexed { index, item ->
            if (index != 0) {
                Divider(modifier = Modifier, thickness = 1.dp, color = Color.Black)
            }
            Text(
                text = item,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.LightGray)
                    .padding(10.dp)
                    .fillMaxWidth()
                    .clickable { onItemSelect(index + 1) }
            )
        }

    }
}


@Composable
fun TopBar(title: String, canNavigateBack: Boolean, onBackButtonPress: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (canNavigateBack) {
            Text(
                text = "<",
                modifier = Modifier
                    .clickable { onBackButtonPress() }
            )
        } else {
            Spacer(modifier = Modifier)
        }

        Text(
            text = title,
        )
        Spacer(modifier = Modifier)
    }

}

@Composable
fun FirstScreen(title:String) {
    Text(text = "first screen")
}


@Composable
fun SecondScreen(index: Int) {
    Text(text = "2-${index}")
}

@Composable
fun ThirdScreen(index: Int) {
    Text(text = "3-${index}")
}



enum class Route(val title: String) {
    Start(title = "Start Screen Title"),
    First(title = "First Screen Title"),
    Second(title = "Second Screen Title"),
    Third(title = "Third Screen Title"),
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NavigationDemoTheme {
        ContentView()
    }
}