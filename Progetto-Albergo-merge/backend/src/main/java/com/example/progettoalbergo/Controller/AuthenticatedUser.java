package com.example.progettoalbergo.Controller;

/** Dati pubblici restituiti dopo login/registrazione. */
public record AuthenticatedUser(
        Long idUtente,
        String nome,
        String cognome,
        String username,
        String cellulare,
        String email,
        String role) {
}
