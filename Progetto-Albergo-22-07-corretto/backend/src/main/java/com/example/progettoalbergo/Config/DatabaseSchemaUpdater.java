package com.example.progettoalbergo.Config;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Piccola migrazione del database eseguita all'avvio dell'applicazione.
 *
 * Serve a riallineare i database creati con le versioni precedenti del
 * progetto al modello attuale delle prenotazioni, senza cancellare i dati.
 *
 * Non e' un DTO e non modifica il flusso HTTP: lavora esclusivamente sullo
 * schema MySQL dopo l'inizializzazione di Hibernate.
 */
@Component
public class DatabaseSchemaUpdater implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSchemaUpdater.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSchemaUpdater(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        String schema = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
        if (schema == null || schema.isBlank()) {
            log.warn("Migrazione DB saltata: nessun database selezionato.");
            return;
        }

        if (!tableExists(schema, "prenotazione_albergo") || !tableExists(schema, "ospite")) {
            log.warn("Migrazione DB saltata: tabelle prenotazione_albergo/ospite non ancora disponibili.");
            return;
        }

        log.info("Controllo schema database '{}' per le prenotazioni...", schema);

        ensureAutoIncrement(schema, "ospite", "idospite");
        ensureAutoIncrement(schema, "prenotazione_albergo", "idprenotazione_albergo");

        // Una prenotazione online senza account o creata dalla reception non ha un Utente.
        makeColumnNullable(schema, "prenotazione_albergo", "fk_prenotazione_utente");

        // La FK ospite usa esattamente lo stesso tipo della PK ospite, così la FK MySQL è valida.
        String guestIdType = columnType(schema, "ospite", "idospite");
        if (guestIdType == null || guestIdType.isBlank()) {
            guestIdType = "BIGINT";
        }
        ensureGuestColumn(schema, guestIdType);

        ensureColumn(schema, "prenotazione_albergo", "numero_ospiti",
                "INT NOT NULL DEFAULT 1");
        ensureColumn(schema, "prenotazione_albergo", "origine",
                "VARCHAR(30) NULL");

        normalizeExistingBookings();
        ensureGuestForeignKey(schema);

        log.info("Schema prenotazioni allineato correttamente.");
    }

    private boolean tableExists(String schema, String table) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.TABLES "
                        + "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?",
                Integer.class, schema, table);
        return count != null && count > 0;
    }

    private boolean columnExists(String schema, String table, String column) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.COLUMNS "
                        + "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                Integer.class, schema, table, column);
        return count != null && count > 0;
    }

    private Map<String, Object> columnInfo(String schema, String table, String column) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT COLUMN_TYPE, IS_NULLABLE, EXTRA "
                        + "FROM information_schema.COLUMNS "
                        + "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                schema, table, column);
        return rows.isEmpty() ? null : rows.get(0);
    }

    private String columnType(String schema, String table, String column) {
        Map<String, Object> info = columnInfo(schema, table, column);
        return info == null ? null : String.valueOf(info.get("COLUMN_TYPE"));
    }

    private void makeColumnNullable(String schema, String table, String column) {
        Map<String, Object> info = columnInfo(schema, table, column);
        if (info == null) {
            log.warn("Colonna {}.{} non trovata: impossibile renderla nullable.", table, column);
            return;
        }

        String nullable = String.valueOf(info.get("IS_NULLABLE"));
        if ("YES".equalsIgnoreCase(nullable)) {
            return;
        }

        String type = String.valueOf(info.get("COLUMN_TYPE"));
        jdbcTemplate.execute("ALTER TABLE `" + table + "` MODIFY COLUMN `" + column + "` "
                + type + " NULL");
        log.info("Modificata {}.{}: ora accetta NULL.", table, column);
    }

    private void ensureColumn(String schema, String table, String column, String definition) {
        if (columnExists(schema, table, column)) {
            return;
        }
        jdbcTemplate.execute("ALTER TABLE `" + table + "` ADD COLUMN `" + column + "` " + definition);
        log.info("Aggiunta colonna {}.{}.", table, column);
    }

    private void ensureAutoIncrement(String schema, String table, String column) {
        Map<String, Object> info = columnInfo(schema, table, column);
        if (info == null) {
            return;
        }

        String extra = String.valueOf(info.get("EXTRA"));
        if (extra != null && extra.toLowerCase().contains("auto_increment")) {
            return;
        }

        String type = String.valueOf(info.get("COLUMN_TYPE"));
        jdbcTemplate.execute("ALTER TABLE `" + table + "` MODIFY COLUMN `" + column + "` "
                + type + " NOT NULL AUTO_INCREMENT");
        log.info("Abilitato AUTO_INCREMENT su {}.{}.", table, column);
    }

    private void normalizeExistingBookings() {
        jdbcTemplate.update(
                "UPDATE prenotazione_albergo SET numero_ospiti = 1 "
                        + "WHERE numero_ospiti IS NULL OR numero_ospiti < 1");

        jdbcTemplate.update(
                "UPDATE prenotazione_albergo "
                        + "SET origine = CASE "
                        + "WHEN fk_prenotazione_utente IS NOT NULL THEN 'ONLINE_UTENTE' "
                        + "WHEN fk_prenotazione_ospite IS NOT NULL THEN 'ONLINE_OSPITE' "
                        + "ELSE 'NON_SPECIFICATA' END "
                        + "WHERE origine IS NULL OR TRIM(origine) = ''");
    }

    private void ensureGuestColumn(String schema, String guestIdType) {
        if (!columnExists(schema, "prenotazione_albergo", "fk_prenotazione_ospite")) {
            ensureColumn(schema, "prenotazione_albergo", "fk_prenotazione_ospite",
                    guestIdType + " NULL");
            return;
        }

        // Se Hibernate aveva gia' creato la colonna con un tipo diverso dal vecchio
        // ospite.idospite, la rendiamo compatibile prima di creare la foreign key.
        if (!guestForeignKeyExists(schema)) {
            Map<String, Object> info = columnInfo(
                    schema, "prenotazione_albergo", "fk_prenotazione_ospite");
            if (info == null) {
                return;
            }
            String currentType = String.valueOf(info.get("COLUMN_TYPE"));
            String nullable = String.valueOf(info.get("IS_NULLABLE"));
            if (!guestIdType.equalsIgnoreCase(currentType) || !"YES".equalsIgnoreCase(nullable)) {
                jdbcTemplate.execute(
                        "ALTER TABLE prenotazione_albergo "
                                + "MODIFY COLUMN fk_prenotazione_ospite " + guestIdType + " NULL");
                log.info("Allineato il tipo di prenotazione_albergo.fk_prenotazione_ospite a ospite.idospite.");
            }
        }
    }

    private boolean guestForeignKeyExists(String schema) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.KEY_COLUMN_USAGE "
                        + "WHERE TABLE_SCHEMA = ? "
                        + "AND TABLE_NAME = 'prenotazione_albergo' "
                        + "AND COLUMN_NAME = 'fk_prenotazione_ospite' "
                        + "AND REFERENCED_TABLE_NAME = 'ospite' "
                        + "AND REFERENCED_COLUMN_NAME = 'idospite'",
                Integer.class, schema);
        return count != null && count > 0;
    }

    private void ensureGuestForeignKey(String schema) {
        if (guestForeignKeyExists(schema)) {
            return;
        }

        jdbcTemplate.execute(
                "ALTER TABLE prenotazione_albergo "
                        + "ADD CONSTRAINT fk_prenotazione_ospite_ref "
                        + "FOREIGN KEY (fk_prenotazione_ospite) REFERENCES ospite(idospite)");
        log.info("Aggiunta foreign key prenotazione_albergo.fk_prenotazione_ospite -> ospite.idospite.");
    }
}
