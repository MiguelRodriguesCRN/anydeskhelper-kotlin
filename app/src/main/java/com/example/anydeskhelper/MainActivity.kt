package com.example.anydeskhelper

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anydeskhelper.ui.theme.AnyDeskHelperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnyDeskHelperTheme {
                SplashScreen() // Primeira tela com logo
            }
        }
    }
}

@Composable
fun SplashScreen() {
    var startMainScreen by remember { mutableStateOf(false) }

    if (startMainScreen) {
        MainScreen() // Redireciona para a tela principal após alguns segundos
    } else {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000) // Exibe a tela inicial por 2 segundos
            startMainScreen = true
        }

        // Tela inicial com o logo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.anydesk_logo), // Coloque o logo aqui
                contentDescription = "Logo AnyDesk",
                modifier = Modifier.size(200.dp)
            )
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    var anyDeskInstalled by remember { mutableStateOf(false) }
    var anyDeskAd1Installed by remember { mutableStateOf(false) }
    var showInstructions by remember { mutableStateOf(false) }
    var showAnyDeskDialog by remember { mutableStateOf(false) }
    var showAnyDeskAd1Dialog by remember { mutableStateOf(false) }
    var showAD1ConfirmationDialog by remember { mutableStateOf(false) } // Controle para o diálogo de confirmação AD1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
    ) {
        Text(
            text = "Assistente de Instalação",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.Red,
            modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
        )

        Text(
            text = "Bem-vindo ao assistente de instalação do AnyDesk.",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Cards com botões elevados
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F4F4))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (!anyDeskInstalled) {
                    InstallButton(context, "Instalar AnyDesk", "https://play.google.com/store/apps/details?id=com.anydesk.anydeskandroid") {
                        showAnyDeskDialog = true
                    }
                } else if (!anyDeskAd1Installed) {
                    InstallButton(context, "Instalar AnyDesk AD1", "https://play.google.com/store/apps/details?id=com.anydesk.adcontrol.ad1") {
                        showAnyDeskAd1Dialog = true
                    }
                } else {
                    showInstructions = true
                }
            }
        }

        if (showInstructions) {
            InstructionsScreen(context) { showAD1ConfirmationDialog = true }
        }

        Spacer(modifier = Modifier.weight(1f))
    }

    // Diálogos de confirmação de instalação
    if (showAnyDeskDialog) {
        ConfirmationDialog("Você instalou o AnyDesk?", { anyDeskInstalled = true; showAnyDeskDialog = false }) { showAnyDeskDialog = false }
    }
    if (showAnyDeskAd1Dialog) {
        ConfirmationDialog("Você instalou o AnyDesk AD1?", { anyDeskAd1Installed = true; showAnyDeskAd1Dialog = false }) { showAnyDeskAd1Dialog = false }
    }

    // Diálogo de confirmação AD1
    if (showAD1ConfirmationDialog) {
        ConfirmationDialog("Você ativou o AnyDesk AD1 nas configurações de acessibilidade?", { showAD1ConfirmationDialog = false }) {
            showAD1ConfirmationDialog = false
        }
    }
}

@Composable
fun InstallButton(context: Context, label: String, url: String, onClick: () -> Unit) {
    Button(
        onClick = {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun ConfirmationDialog(message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Confirmação!", fontWeight = FontWeight.Bold) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = { onConfirm() }) {
                Text("Sim")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Ainda não")
            }
        },
        containerColor = Color(0xFFF4F4F4)
    )
}

@Composable
fun InstructionsScreen(context: Context, onActionComplete: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Para ativar o AnyDesk AD1: ",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.Red
        )
        Spacer(modifier = Modifier.height(8.dp))

        val steps = listOf(
            "1. Ative o AnyDesk AD1.",
            "2. Abra o AnyDesk.",
            "3. Clique nos 3 pontinhos no canto superior esquerdo.",
            "4. Vá em Configurações (que estará em vermelho).",
            "5. Autorize o Plugin AD1.",
            "6. Reinicie o dispositivo.",
            "7. Após a reinicialização, informe o número do AnyDesk ao agente."
        )

        steps.forEach { step ->
            Text(text = step, style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                openAccessibilitySettings(context)
                onActionComplete() // Aciona a confirmação AD1 após abrir a acessibilidade
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text(
                text = "Abrir Configurações de Acessibilidade",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

private fun openAccessibilitySettings(context: Context) {
    val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}
