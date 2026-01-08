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

// Couleurs personnalisées - exactement comme le web
private val DarkText = Color(0xFF1e293b)
private val GrayText = Color(0xFF64748b)
private val OrangePrimary = Color(0xFFf59e0b)
private val OrangeDark = Color(0xFFd97706)
private val ErrorRed = Color(0xFFEF4444)
private val GoldBorder = Color(0xFFd4af37)
private val CardBackground = Color(0xFF2a2a2a)
private val InputBackground = Color(0xFF1a1a1a)
private val WhiteText = Color(0xFFffffff)

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
    
    // Couleurs exactes comme le web - fond NOIR
    val darkBackground = Color(0xFF1a1a1a)
    
    // Couleurs des champs de texte - TEXTE BLANC sur fond sombre
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        // Texte saisi - BLANC
        focusedTextColor = WhiteText,
        unfocusedTextColor = WhiteText,
        // Bordure
        focusedBorderColor = GoldBorder,
        unfocusedBorderColor = Color(0xFF3a3a3a),
        // Label
        focusedLabelColor = GoldBorder,
        unfocusedLabelColor = WhiteText,
        // Icônes
        focusedLeadingIconColor = GoldBorder,
        unfocusedLeadingIconColor = WhiteText,
        focusedTrailingIconColor = WhiteText,
        unfocusedTrailingIconColor = WhiteText,
        // Curseur
        cursorColor = GoldBorder,
        // Placeholder
        focusedPlaceholderColor = GrayText,
        unfocusedPlaceholderColor = GrayText,
        // Container/Background
        focusedContainerColor = InputBackground,
        unfocusedContainerColor = InputBackground
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
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
            
            // Carte de login - fond sombre avec bordure dorée
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, GoldBorder)
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
                        // Logo circulaire orange
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(OrangePrimary, OrangeDark),
                                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                        end = androidx.compose.ui.geometry.Offset(100f, 100f)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "M",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = "Mbongo",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = WhiteText
                            )
                            Text(
                                text = "Gérez facilement vos dépenses",
                                fontSize = 14.sp,
                                color = GoldBorder
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
                            shape = RoundedCornerShape(8.dp),
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                color = WhiteText,
                                fontSize = 14.sp
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
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            color = WhiteText,
                            fontSize = 14.sp
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
                        shape = RoundedCornerShape(8.dp),
                        visualTransformation = if (passwordVisible) 
                            VisualTransformation.None 
                        else 
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            color = WhiteText,
                            fontSize = 14.sp
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
                            shape = RoundedCornerShape(8.dp),
                            visualTransformation = if (confirmPasswordVisible) 
                                VisualTransformation.None 
                            else 
                                PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                color = WhiteText,
                                fontSize = 14.sp
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
                    
                    // Bouton de connexion/inscription - orange
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
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(OrangePrimary, OrangeDark),
                                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                        end = androidx.compose.ui.geometry.Offset(200f, 200f)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isLoginMode) "Se connecter" else "Créer un compte",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Lien inscription/connexion
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isLoginMode) "Pas de compte ? " else "Déjà un compte ? ",
                            color = WhiteText,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isLoginMode) "Créer un compte" else "Se connecter",
                            color = GoldBorder,
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
    // Symboles dorés et argentés comme le web
    val symbols = remember {
        listOf(
            SymbolData("$", 0.15f, 0.10f, 8000, true),
            SymbolData("€", 0.85f, 0.20f, 9000, false),
            SymbolData("£", 0.05f, 0.35f, 7500, true),
            SymbolData("¥", 0.75f, 0.45f, 8500, true),
            SymbolData("₹", 0.25f, 0.60f, 9500, true),
            SymbolData("FCFA", 0.90f, 0.75f, 10000, true),
            SymbolData("₿", 0.60f, 0.15f, 7000, false),
            SymbolData("💰", 0.45f, 0.12f, 8200, false),
            SymbolData("💎", 0.78f, 0.28f, 9200, false),
            SymbolData("🪙", 0.12f, 0.42f, 8800, true),
            SymbolData("₩", 0.85f, 0.58f, 7800, false),
            SymbolData("₽", 0.32f, 0.72f, 8600, true),
            SymbolData("₺", 0.68f, 0.08f, 9000, true),
            SymbolData("₸", 0.38f, 0.88f, 8400, false),
            SymbolData("$", 0.52f, 0.06f, 7600, true),
            SymbolData("€", 0.08f, 0.31f, 8800, false),
            SymbolData("£", 0.92f, 0.48f, 9200, true),
            SymbolData("¥", 0.28f, 0.17f, 7400, false),
            SymbolData("₹", 0.82f, 0.85f, 8200, true),
            SymbolData("💰", 0.55f, 0.55f, 9600, true),
            SymbolData("💎", 0.18f, 0.78f, 8000, false),
            SymbolData("🪙", 0.72f, 0.32f, 7800, true),
            SymbolData("₩", 0.42f, 0.92f, 9400, false),
            SymbolData("₽", 0.88f, 0.38f, 8600, true)
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
    val yStart: Float,
    val duration: Int,
    val isGold: Boolean
)

@Composable
fun AnimatedMoneySymbol(data: SymbolData) {
    val infiniteTransition = rememberInfiniteTransition(label = "symbol_${data.symbol}_${data.xPosition}")
    
    // Couleurs dorées et argentées comme le web
    val goldColor = Color(0xFFFFD700).copy(alpha = 0.4f)
    val silverColor = Color(0xFFC0C0C0).copy(alpha = 0.35f)
    val baseColor = if (data.isGold) goldColor else silverColor
    
    val offsetY by infiniteTransition.animateFloat(
        initialValue = data.yStart * 1000f - 100f,
        targetValue = data.yStart * 1000f + 1200f,
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
        initialValue = if (data.isGold) 0.4f else 0.3f,
        targetValue = if (data.isGold) 0.5f else 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_${data.symbol}"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(
                x = (data.xPosition * 350).dp,
                y = offsetY.dp
            )
    ) {
        Text(
            text = data.symbol,
            color = baseColor.copy(alpha = alpha),
            fontSize = if (data.symbol == "FCFA") 20.sp else 32.sp,
            fontWeight = FontWeight.Bold,
            style = androidx.compose.ui.text.TextStyle(
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = if (data.isGold) goldColor else silverColor,
                    blurRadius = 10f
                )
            )
        )
    }
}
