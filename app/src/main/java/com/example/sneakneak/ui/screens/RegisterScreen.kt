package com.example.sneakneak.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.example.sneakneak.ui.components.buttons.BaseBlueButton
import com.example.sneakneak.ui.components.buttons.BaseTextField

@Composable
fun RegisterScreen(){

    var title by remember { mutableStateOf("") }
    var consent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){

        Spacer(modifier = Modifier.height(80.dp))

        Text(text = "Регистрация",
            color = Color.Black,
            fontSize = 32.sp,
            fontWeight = FontWeight.Normal
        )

        Text(text = "Заполните свои данные",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Light
        )

        Spacer(modifier = Modifier.height(50.dp))

        BaseTextField(
            value = title,
            onValueChange = { title = it },
            label = "Ваше имя",
            enabled = true,
            isPassword = false,
            placeholder = "xxxxxxxx"
        )

        Spacer(modifier = Modifier.height(20.dp))

        BaseTextField(
            value = title,
            onValueChange = { title = it },
            label = "Email",
            enabled = true,
            isPassword = false,
            placeholder = "xyz@gmail.com"
        )


        Spacer(modifier = Modifier.height(20.dp))

        BaseTextField(
            value = title,
            onValueChange = { title = it },
            label = "Пароль",
            enabled = true,
            isPassword = true,
            placeholder = "********"
        )

        Spacer(modifier = Modifier.height(60.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Checkbox(
                checked = consent,
                OnCheckedChange = {consent = it }
            )
        }

        BaseBlueButton()

        Spacer(modifier = Modifier.height(200.dp))

        Text(text = "Есть аккаунт? Войти",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )

    }
}

@Preview
@Composable
fun RegisterScreenPreview(){
    RegisterScreen()
}