package org.albert.weatherapp.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun HomePage() {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color.Blue)
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Home",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
    }
}

//@Composable
//fun HomePage(modifier: Modifier = Modifier) {
//    val activity = LocalActivity.current
//    Column(
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = modifier
//            .fillMaxSize()
//    ) {
//        Text(
//            text = "Bem-vindo/a!",
//            fontSize = 24.sp
//        )
//        Spacer(modifier = Modifier.size(24.dp))
//        Button(
//            onClick = {
//                activity?.finish()
//            }
//        ) {
//            Text(text = "Sair")
//        }
//    }
//}