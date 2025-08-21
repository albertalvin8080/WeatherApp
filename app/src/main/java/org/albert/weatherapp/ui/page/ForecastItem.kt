package org.albert.weatherapp.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.albert.weatherapp.model.Forecast
import java.text.DecimalFormat
import org.albert.weatherapp.R

@Composable
fun ForecastItem(
    forecast: Forecast,
    onClick: (Forecast) -> Unit,
    modifier: Modifier = Modifier
) {
    val format = DecimalFormat("#.0")
    val tempMin = format.format(forecast.tempMin)
    val tempMax = format.format(forecast.tempMax)
    Row (
        modifier = modifier.fillMaxWidth().padding(12.dp)
            .clickable( onClick = { onClick(forecast) }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = forecast.imgUrl,
            modifier = Modifier.size(40.dp),
            error = painterResource(id = R.drawable.loading),
            contentDescription = "Imagem"
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column {
//            Text(modifier = Modifier, text = forecast.weather, fontSize = 24.sp)
//            Spacer(modifier = Modifier.height(12.dp))
//            Row {
//                Column {
//                    Text(modifier = Modifier, text = "Min: $tempMin℃", fontSize = 16.sp)
//                    Spacer(modifier = Modifier.size(2.dp))
//                    Text(modifier = Modifier, text = "Max: $tempMax℃", fontSize = 16.sp)
//                }
//                Text(modifier = Modifier, text = forecast.date, fontSize = 20.sp)
//                Spacer(modifier = Modifier.size(12.dp))
//            }
            Text(modifier = Modifier, text = forecast.weather, fontSize = 24.sp)
            Row {
                Text(modifier = Modifier, text = forecast.date, fontSize = 20.sp)
                Spacer(modifier = Modifier.size(12.dp))
                Text(modifier = Modifier, text = "Min: $tempMin℃", fontSize = 16.sp)
                Spacer(modifier = Modifier.size(12.dp))
                Text(modifier = Modifier, text = "Max: $tempMax℃", fontSize = 16.sp)
            }
        }
    }
}