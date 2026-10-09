package com.example.myapp   // <-- ӨӨРИЙН PACKAGE-ААР СОЛИНО

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// ---------- ӨНГӨ ----------
val Purple = Color(0xFF6C4CF1)
val PurpleDark = Color(0xFF4A2FD0)
val Pink = Color(0xFFEC4899)
val AppGradient = Brush.linearGradient(listOf(PurpleDark, Purple, Pink))

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppRoot() }
    }
}

// ---------- NAVIGATION ----------
@Composable
fun AppRoot() {
    MaterialTheme(colorScheme = lightColorScheme(primary = Purple)) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    onSignUp = { navController.navigate("signup") },
                    onForgot = { navController.navigate("forgot") }
                )
            }
            composable("signup") {
                SignUpScreen(
                    onBack = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }
            composable("forgot") {
                ForgotPasswordScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

// ======================================================
// ХАМТ АШИГЛАХ ХЭСГҮҮД
// ======================================================
@Composable
fun AuthLayout(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onBack: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(Modifier.fillMaxSize().background(AppGradient)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(40.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(
                title,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(subtitle, color = Color.White.copy(alpha = 0.85f))
            Spacer(Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(Modifier.padding(24.dp), content = content)
            }
        }

        if (onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.systemBarsPadding().padding(8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Буцах", tint = Color.White)
            }
        }
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    var visible by remember { mutableStateOf(false) }

    val trailing: (@Composable () -> Unit)? = if (isPassword) {
        {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        }
    } else null

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null) },
        trailingIcon = trailing,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        visualTransformation = if (isPassword && !visible) PasswordVisualTransformation()
        else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else keyboardType
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().height(54.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ErrorText(message: String) {
    if (message.isNotEmpty()) {
        Spacer(Modifier.height(8.dp))
        Text(message, color = MaterialTheme.colorScheme.error)
    }
}

// ======================================================
// 1. НЭВТРЭХ (LOGIN)
// ======================================================
@Composable
fun LoginScreen(onSignUp: () -> Unit, onForgot: () -> Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AuthLayout(
        icon = Icons.Default.Lock,
        title = "Тавтай морил",
        subtitle = "Бүртгэлдээ нэвтэрнэ үү"
    ) {
        AuthTextField(email, { email = it }, "Имэйл", Icons.Default.Email, KeyboardType.Email)
        Spacer(Modifier.height(14.dp))
        AuthTextField(password, { password = it }, "Нууц үг", Icons.Default.Lock, isPassword = true)

        ErrorText(error)

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onForgot, modifier = Modifier.align(Alignment.End)) {
            Text("Нууц үгээ мартсан уу?", color = Purple, fontWeight = FontWeight.Medium)
        }

        Spacer(Modifier.height(8.dp))
        PrimaryButton("Нэвтрэх") {
            if (email.isBlank() || password.isBlank()) {
                error = "Имэйл болон нууц үгээ оруулна уу"
            } else {
                error = ""
                // Энд өөрийн backend/API-тайгаа холбоно
                Toast.makeText(context, "Амжилттай нэвтэрлээ", Toast.LENGTH_SHORT).show()
            }
        }

        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Бүртгэл байхгүй юу?")
            TextButton(onClick = onSignUp) {
                Text("Бүртгүүлэх", color = Purple, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ======================================================
// 2. БҮРТГҮҮЛЭХ (SIGN UP)
// ======================================================
@Composable
fun SignUpScreen(onBack: () -> Unit, onSuccess: () -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AuthLayout(
        icon = Icons.Default.PersonAdd,
        title = "Бүртгүүлэх",
        subtitle = "Шинэ бүртгэл үүсгэнэ үү",
        onBack = onBack
    ) {
        AuthTextField(name, { name = it }, "Нэр", Icons.Default.Person)
        Spacer(Modifier.height(14.dp))
        AuthTextField(email, { email = it }, "Имэйл", Icons.Default.Email, KeyboardType.Email)
        Spacer(Modifier.height(14.dp))
        AuthTextField(password, { password = it }, "Нууц үг", Icons.Default.Lock, isPassword = true)
        Spacer(Modifier.height(14.dp))
        AuthTextField(confirm, { confirm = it }, "Нууц үг давтах", Icons.Default.Lock, isPassword = true)

        ErrorText(error)

        Spacer(Modifier.height(20.dp))
        PrimaryButton("Бүртгүүлэх") {
            error = when {
                name.isBlank() || email.isBlank() || password.isBlank() || confirm.isBlank() ->
                    "Бүх талбарыг бөглөнө үү"
                !email.contains("@") -> "Имэйл хаяг буруу байна"
                password.length < 6 -> "Нууц үг хамгийн багадаа 6 тэмдэгт байна"
                password != confirm -> "Нууц үг таарахгүй байна"
                else -> ""
            }
            if (error.isEmpty()) {
                // Энд өөрийн backend/API-тайгаа холбоно
                Toast.makeText(context, "Бүртгэл амжилттай үүслээ", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
        }

        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Бүртгэлтэй юу?")
            TextButton(onClick = onBack) {
                Text("Нэвтрэх", color = Purple, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ======================================================
// 3. НУУЦ ҮГ СЭРГЭЭХ (FORGOT PASSWORD)
// ======================================================
@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }

    AuthLayout(
        icon = Icons.Default.LockReset,
        title = "Нууц үг сэргээх",
        subtitle = "Имэйл хаягаа оруулна уу",
        onBack = onBack
    ) {
        if (sent) {
            Text(
                "Сэргээх линкийг $email хаяг руу илгээлээ. Имэйлээ шалгана уу.",
                color = Color(0xFF10B981),
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(20.dp))
            PrimaryButton("Нэвтрэх рүү буцах", onBack)
        } else {
            Text(
                "Бүртгэлтэй имэйл хаягаа оруулбал нууц үг сэргээх линк илгээнэ.",
                color = Color.Gray
            )
            Spacer(Modifier.height(16.dp))
            AuthTextField(email, { email = it }, "Имэйл", Icons.Default.Email, KeyboardType.Email)

            ErrorText(error)

            Spacer(Modifier.height(20.dp))
            PrimaryButton("Линк илгээх") {
                if (email.isBlank() || !email.contains("@")) {
                    error = "Зөв имэйл хаяг оруулна уу"
                } else {
                    error = ""
                    // Энд өөрийн backend/API-тайгаа холбоно
                    Toast.makeText(context, "Линк илгээлээ", Toast.LENGTH_SHORT).show()
                    sent = true
                }
            }
        }
    }
}
