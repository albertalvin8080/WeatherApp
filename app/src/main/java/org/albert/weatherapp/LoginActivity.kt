package org.albert.weatherapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.albert.weatherapp.ui.theme.WeatherAppTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPage(modifier: Modifier = Modifier) {
    var email by rememberSaveable { mutableStateOf("albertalvin@gmail.com") }
    var password by rememberSaveable { mutableStateOf("123456") }
//    val activity = LocalContext.current as? Activity
    val activity = LocalActivity.current
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Bem-vindo/a!",
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.size(24.dp))
        OutlinedTextField(
            value = email,
            label = { Text(text = "Digite seu e-mail") },
            modifier = Modifier.fillMaxWidth(0.9f),
            onValueChange = { email = it }
        )
        Spacer(modifier = Modifier.size(24.dp))
        OutlinedTextField(
            value = password,
            label = { Text(text = "Digite sua senha") },
            modifier = Modifier.fillMaxWidth(0.9f),
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            modifier = Modifier,
        ) {
            Button(
                onClick = {
                    Firebase.auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(activity!!) { task ->
                            if (task.isSuccessful) {
                                // removed on 5th practice
//                            activity.startActivity(
//                                Intent(activity, MainActivity::class.java).setFlags(
//                                    Intent.FLAG_ACTIVITY_SINGLE_TOP
//                                )
//                            )
                                Toast.makeText(activity, "Login OK!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(activity, "Login FALHOU!", Toast.LENGTH_LONG).show()
                            }
                        }
                },
                enabled = email.isNotEmpty() && password.isNotEmpty()
            ) {
                Text("Login")
            }
            Spacer(modifier = Modifier.size(24.dp))
            Button(
                onClick = { email = ""; password = "" }
            ) {
                Text("Limpar")
            }
        }
        Spacer(modifier = Modifier.size(24.dp))
        Button(
            onClick = {
                activity?.startActivity(
                    Intent(activity, RegisterActivity::class.java).setFlags(
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                    )
                )
            }
        ) {
            Text("Register")
        }
    }
}