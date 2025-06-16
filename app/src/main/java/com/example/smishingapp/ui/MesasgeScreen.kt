package com.example.smishingapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smishingapp.ui.chat.ChatScreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*



private fun mToast(context: Context){
    Toast.makeText(context, "Thank you for your feedback", Toast.LENGTH_LONG).show()
}
@Composable
fun MyAppNavHost(
    modifier: Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination:String = "All Messages",
    messageViewModel: MessageViewModel
){
    val messageUiState by messageViewModel.uiState.collectAsState()

    val onMessageClick = { address: String ->
        messageViewModel.updateConversation(address)
        navController.navigate("Chat")
    }
    val context = LocalContext.current
    val onMenuClick = { message:Message ->
        mToast(context)
        messageViewModel.updateChange(message)
    }
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("All Messages"){
            MessageScreen(
                modifier,
                navController,
                title = "All Messages",
                messageUiState.allMessage,
                onMessageClick,
                onMenuClick
            )
        }
        composable("Spam"){
            MessageScreen(
                modifier,
                navController,
                title = "Spam",
                messageUiState.spam,
                onMessageClick,
                onMenuClick
            )
        }
        composable("Ham"){
            MessageScreen(
                modifier,
                navController,
                title = "Ham",
                messageUiState.ham,
                onMessageClick,
                onMenuClick
            )
        }
        composable("Chat"){
            ChatScreen(
                conversation = messageUiState.chat,
                navController = navController
            )
        }
    }
}
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MessageScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    title: String,
    messageList:SortedSet<Message>,
    onMessageClick:(String) ->Unit,
    onMenuClick: (Message) -> Unit
){
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, top = 5.dp)
            ){
                Card(modifier = Modifier
                    .fillMaxWidth()
                ){
                    Button(onClick = {navController.navigate("All Messages")}
                    ) {
                        Text("Home")
                    }
                }
                Card(modifier = Modifier.fillMaxWidth()){
                    Button(onClick = {navController.navigate("Spam")}) {
                        Text("Spam")
                    }
                }
                Card(modifier = Modifier.fillMaxWidth()){
                    Button(onClick = {navController.navigate("Ham")}) {
                        Text("Ham")
                    }
                }
            }
        },
        topBar = {
            MessageTopAppBar(
                modifier,
                title
            ) {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }
        }
    ){
        MessageList(messageList,onMessageClick,onMenuClick)
    }
}


@Composable
fun MessageTopAppBar(modifier: Modifier = Modifier,
                     title: String,
                     onMenuClick:()->Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Email, contentDescription = "Message Icon")
                Text(text = title, modifier = modifier.padding(start = 5.dp))
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.Menu, "Menu Button")
            }
        },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
        elevation = 10.dp
    )
}


@Composable
fun MessageList(
    messageList: SortedSet<Message>,
    onMessageClick: (String) -> Unit,
    onMenuClick: (Message) -> Unit
) {
    LazyColumn{
            messageList.forEach {message->
                items(1) {
                    MessageView(
                        message = message,
                        onMessageClick,
                        onMenuClick
                    )
                }
            }
    }
}

@Composable
fun MessageView(
    message: Message,
    onMessageClick: (String) -> Unit,
    onMenuClick: (Message) -> Unit
){
    var expanded by remember{ mutableStateOf(false) }
    Card(modifier = Modifier
        .padding(4.dp)
        .clickable(onClick = {onMessageClick(message.sender)}),
        elevation = 4.dp) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp, 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Person,"Person Icon",
                modifier = Modifier
                    .fillMaxHeight()
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(start = 5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(message.name == "")
                        Text(message.sender,Modifier.weight(1f), fontSize = 20.sp)
                    else
                        Text(message.name,Modifier.weight(1f), fontSize = 20.sp)

                    val current = Calendar.getInstance().time
                    val format = SimpleDateFormat("MM/dd, yy", Locale.getDefault())
                    val date = format.format(current)
                    val formattedDate = format.format(message.date)
                    if(date == formattedDate)
                    {
                        Text("Today", fontSize = 15.sp)
                    }
                    else
                        Text(formattedDate, fontSize = 15.sp)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(message.text, fontSize = 15.sp, maxLines = 1)
                    Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)) {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(onClick = { onMenuClick(message) }) {
                                if(message.isSpam)
                                {
                                    Text("Mark not as Spam")
                                }
                                else
                                {
                                    Text("Mark not as Ham")
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}


//@Preview
//@Composable
//fun MessageScreenPreview(){
//    SmashingAppTheme {
//        // A surface container using the 'background' color from the theme
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colors.background
//        ) {
//            MyAppNavHost(modifier = Modifier)
//        }
//    }
//}
