package com.mbongo.app.ui.screens.login

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

// Couleurs personnalisées
private val DarkText = Color(0xFF1e293b)
private val GrayText = Color(0xFF64748b)
private val GreenPrimary = Color(0xFF10B981)
private val GreenDark = Color(0xFF059669)
private val ErrorRed = Color(0xFFEF4444)

@Composable
fun LoginScreen(
    onLoginSuccess: (email: String, password: String, name: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    val gradientColors = listOf(
        Color(0xFF1a1a2e),
        Color(0xFF16213e),
        Color(0xFF0f3460)
    )
    
    // Couleurs des champs de texte - TEXTE NOIR LISIBLE
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        // Texte saisi - NOIR
        focusedTextColor = DarkText,
        unfocusedTextColor = DarkText,
        // Bordure
        focusedBorderColor = GreenPrimary,
        unfocusedBorderColor = Color(0xFFcbd5e1),
        // Label
        focusedLabelColor = GreenPrimary,
        unfocusedLabelColor = GrayText,
        // Icônes
        focusedLeadingIconColor = GreenPrimary,
        unfocusedLeadingIconColor = GrayText,
        focusedTrailingIconColor = GrayText,
        unfocusedTrailingIconColor = GrayText,
        // Curseur
        cursorColor = GreenPrimary,
        // Placeholder
        focusedPlaceholderColor = GrayText,
        unfocusedPlaceholderColor = GrayText
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        // Symboles monétaires animés en arrière-plan
        MoneySymbolsBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            // Carte de login
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo et titre
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Logo circulaire
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(GreenPrimary, GreenDark))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "M",
                                color = Color.White,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = "Mbongo",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                            Text(
                                text = "Gérez facilement vos dépenses",
                                fontSize = 14.sp,
                                color = GrayText
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Champ Nom (inscription uniquement)
                    if (!isLoginMode) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it; error = "" },
                            label = { Text("Nom") },
                            placeholder = { Text("Votre nom", color = GrayText) },
                            leadingIcon = { 
                                Icon(Icons.Default.Person, contentDescription = null) 
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                color = DarkText,
                                fontSize = 16.sp
                            ),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // Champ Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; error = "" },
                        label = { Text("Email") },
                        placeholder = { Text("votre.email@exemple.com", color = GrayText) },
                        leadingIcon = { 
                            Icon(Icons.Default.Email, contentDescription = null) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            color = DarkText,
                            fontSize = 16.sp
                        ),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Champ Mot de passe
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; error = "" },
                        label = { Text("Mot de passe") },
                        placeholder = { Text("••••••", color = GrayText) },
                        leadingIcon = { 
                            Icon(Icons.Default.Lock, contentDescription = null) 
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff 
                                    else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Masquer" else "Afficher",
                                    tint = GrayText
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (passwordVisible) 
                            VisualTransformation.None 
                        else 
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            color = DarkText,
                            fontSize = 16.sp
                        ),
                        singleLine = true
                    )
                    
                    // Confirmation mot de passe (inscription uniquement)
                    if (!isLoginMode) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it; error = "" },
                            label = { Text("Confirmer le mot de passe") },
                            placeholder = { Text("••••••", color = GrayText) },
                            leadingIcon = { 
                                Icon(Icons.Default.Lock, contentDescription = null) 
                            },
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        if (confirmPasswordVisible) Icons.Default.VisibilityOff 
                                        else Icons.Default.Visibility,
                                        contentDescription = if (confirmPasswordVisible) "Masquer" else "Afficher",
                                        tint = GrayText
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            visualTransformation = if (confirmPasswordVisible) 
                                VisualTransformation.None 
                            else 
                                PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                color = DarkText,
                                fontSize = 16.sp
                            ),
                            singleLine = true
                        )
                    }
                    
                    // Message d'erreur
                    if (error.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = error,
                            color = ErrorRed,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Bouton de connexion/inscription
                    Button(
                        onClick = {
                            when {
                                email.isEmpty() || password.isEmpty() -> {
                                    error = "Email et mot de passe requis"
                                }
                                !isLoginMode && name.isEmpty() -> {
                                    error = "Nom requis"
                                }
                                !isLoginMode && password != confirmPassword -> {
                                    error = "Les mots de passe ne correspondent pas"
                                }
                                else -> {
                                    onLoginSuccess(email, password, name)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Text(
                            text = if (isLoginMode) "Se connecter" else "Créer un compte",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Lien inscription/connexion
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isLoginMode) "Pas de compte ? " else "Déjà un compte ? ",
                            color = GrayText,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isLoginMode) "Créer un compte" else "Se connecter",
                            color = GreenPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                isLoginMode = !isLoginMode
                                error = ""
                                // Reset fields
                                if (isLoginMode) {
                                    name = ""
                                    confirmPassword = ""
                                }
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun MoneySymbolsBackground() {
    val symbols = remember {
        listOf(
            SymbolData("$", 0.05f, 8000),
            SymbolData("€", 0.15f, 9000),
            SymbolData("£", 0.25f, 7500),
            SymbolData("¥", 0.35f, 8500),
            SymbolData("₹", 0.45f, 9500),
            SymbolData("FCFA", 0.55f, 10000),
            SymbolData("₿", 0.65f, 7000),
            SymbolData("💰", 0.75f, 8200),
            SymbolData("💎", 0.85f, 9200),
            SymbolData("🪙", 0.95f, 8800),
            SymbolData("₩", 0.10f, 7800),
            SymbolData("₽", 0.90f, 8600)
        )
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        symbols.forEach { symbolData ->
            AnimatedMoneySymbol(symbolData)
        }
    }
}

data class SymbolData(
    val symbol: String,
    val xPosition: Float,
    val duration: Int
)

@Composable
fun AnimatedMoneySymbol(data: SymbolData) {
    val infiniteTransition = rememberInfiniteTransition(label = "symbol_${data.symbol}")
    
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = data.duration,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "offsetY_${data.symbol}"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_${data.symbol}"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(y = offsetY.dp)
    ) {
        Text(
            text = data.symbol,
            color = Color.White.copy(alpha = alpha),
            fontSize = if (data.symbol == "FCFA") 16.sp else 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = (data.xPosition * 350).dp)
        )
    }
}
