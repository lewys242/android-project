package com.mbongo.app.ui.theme

import androidx.compose.ui.graphics.Color

// Theme principale - Noir et Or (identique à l'app web)
val Black = Color(0xFF1A1A1A)
val DarkGray = Color(0xFF2A2A2A)
val MediumGray = Color(0xFF3A3A3A)
val LightGray = Color(0xFFCCCCCC)
val Gold = Color(0xFFD4AF37)
val LightGold = Color(0xFFFFED4E)
val White = Color(0xFFFFFFFF)

// Couleurs fonctionnelles
val Success = Color(0xFF10B981)  // Vert émeraude
val Error = Color(0xFFEF4444)    // Rouge
val Warning = Color(0xFFFBBF24) // Jaune/Orange
val Info = Color(0xFF3B82F6)    // Bleu

// Couleurs du texte
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFCCCCCC)
val TextMuted = Color(0xFF9CA3AF)

// Couleurs spécifiques de l'app web
val GreenPrimary = Color(0xFF10B981)
val GreenDark = Color(0xFF059669)
val RedPrimary = Color(0xFFEF4444)
val BluePrimary = Color(0xFF667EEA)

// Couleurs pour les champs de formulaire (fond blanc)
val InputBackground = Color(0xFFFFFFFF)
val InputText = Color(0xFF1E293B)
val InputBorder = Color(0xFFCBD5E1)
val InputBorderFocused = Color(0xFF10B981)

// Couleurs pour les catégories (matching web version)
val CategoryColors = mapOf(
    "Alimentation" to Color(0xFF10B981),
    "Transport" to Color(0xFF3B82F6),
    "Logement" to Color(0xFF8B5CF6),
    "Santé" to Color(0xFFEF4444),
    "Loisirs" to Color(0xFFF59E0B),
    "Éducation" to Color(0xFF06B6D4),
    "Vêtements" to Color(0xFFEC4899),
    "Autres" to Color(0xFF6B7280)
)
