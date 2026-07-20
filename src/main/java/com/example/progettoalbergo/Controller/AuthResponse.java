package com.example.progettoalbergo.Controller;

import com.example.progettoalbergo.Model.Utente;

// Non e' un DTO che duplica l'entita': serve solo a restituire token+utente insieme
public record AuthResponse(String token, Utente user) {}
