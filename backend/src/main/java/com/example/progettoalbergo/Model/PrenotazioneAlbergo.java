/*
 * GUIDA DIDATTICA BACKEND
 * ---------------------------------------------------------------------------
 * ENTITY JPA: rappresenta una tabella (o relazione) del database e definisce il mapping tra Java e MySQL.
 * File: PrenotazioneAlbergo.java
 * Segui le annotazioni Spring (@RestController, @Entity, @Service...) per capire
 * quale ruolo assume la classe nel flusso richiesta -> logica -> database.
 */
package com.example.progettoalbergo.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "prenotazione_albergo")
public class PrenotazioneAlbergo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idprenotazione_albergo")
    private Long idPrenotazioneAlbergo;

    @Column(name = "fk_prenotazione_utente")
    private Long idUtente;

    @Column(name = "fk_prenotazione_ospite")
    private Long idOspite;

    @Column(name = "fk_prenotazione_camera")
    private Long idCamera;

    @Column(name = "data_pr")
    private LocalDate dataPrenotazione;

    @Column(name = "data_arr")
    private LocalDate dataArrivo;

    @Column(name = "data_par")
    private LocalDate dataPartenza;

    private String nominativo;
    private boolean stato;

    @Column(name = "numero_ospiti")
    private int numeroOspiti;

    @Column(name = "origine", length = 30)
    private String origine;

    public PrenotazioneAlbergo(Long idPrenotazioneAlbergo, Long idUtente, Long idCamera,
            String nominativo, boolean stato, LocalDate dataPrenotazione,
            LocalDate dataArrivo, LocalDate dataPartenza) {
        setIdPrenotazioneAlbergo(idPrenotazioneAlbergo);
        setidUtente(idUtente);
        setIdCamera(idCamera);
        setNominativo(nominativo);
        setStato(stato);
        setDataPrenotazione(dataPrenotazione);
        setDataArrivo(dataArrivo);
        setDataPartenza(dataPartenza);
    }

    public PrenotazioneAlbergo() {
    }

    public Long getIdPrenotazioneAlbergo() {
        return idPrenotazioneAlbergo;
    }

    public void setIdPrenotazioneAlbergo(Long idPrenotazioneAlbergo) {
        this.idPrenotazioneAlbergo = idPrenotazioneAlbergo;
    }

    public Long getidUtente() {
        return idUtente;
    }

    public void setidUtente(Long idUtente) {
        this.idUtente = idUtente;
    }

    public Long getIdOspite() {
        return idOspite;
    }

    public void setIdOspite(Long idOspite) {
        this.idOspite = idOspite;
    }

    public Long getIdCamera() {
        return idCamera;
    }

    public void setIdCamera(Long idCamera) {
        this.idCamera = idCamera;
    }

    public String getNominativo() {
        return nominativo;
    }

    public void setNominativo(String nominativo) {
        this.nominativo = nominativo;
    }

    public boolean isStato() {
        return stato;
    }

    public void setStato(boolean stato) {
        this.stato = stato;
    }

    public LocalDate getDataPrenotazione() {
        return dataPrenotazione;
    }

    public void setDataPrenotazione(LocalDate dataPrenotazione) {
        this.dataPrenotazione = dataPrenotazione;
    }

    public LocalDate getDataArrivo() {
        return dataArrivo;
    }

    public void setDataArrivo(LocalDate dataArrivo) {
        this.dataArrivo = dataArrivo;
    }

    public LocalDate getDataPartenza() {
        return dataPartenza;
    }

    public void setDataPartenza(LocalDate dataPartenza) {
        this.dataPartenza = dataPartenza;
    }

    public int getNumeroOspiti() {
        return numeroOspiti;
    }

    public void setNumeroOspiti(int numeroOspiti) {
        this.numeroOspiti = numeroOspiti;
    }

    public String getOrigine() {
        return origine;
    }

    public void setOrigine(String origine) {
        this.origine = origine;
    }
}
