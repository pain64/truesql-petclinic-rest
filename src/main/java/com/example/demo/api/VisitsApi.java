package com.example.demo.api;

import com.example.demo.PetClinic.MainDb;
import net.truej.sql.TrueSql;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import com.example.demo.api.VisitsApiTrueSql.*;

@CrossOrigin(origins = "http://localhost:4200")
@TrueSql @RestController class VisitsApi {

    @Autowired MainDb ds;

    @GetMapping("/visits/{visitId}") @Nullable Visit get(
        @PathVariable("visitId") int visitId
    ) {
        return ds.q("""
            select id, pet_id, visit_date as "date", description
            from visits where id = ?""", visitId
        ).g.fetchOneOrZero(Visit.class);
    }

    record VisitFields(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date,
        String description
    ) { }

    @PostMapping("/owners/{ownerId}/pets/{petId}/visits") void add(
        @PathVariable("petId") int petId,
        @RequestBody VisitFields f
    ) {
        ds.q(
            "insert into visits values(default, ?, ?, ?)",
            petId, f.date, f.description
        ).fetchNone();
    }

    @PutMapping("/visits/{visitId}") void updateVisit(
        @PathVariable("visitId") int visitId, @RequestBody VisitFields f
    ) {
        ds.q(
            "update visits set visit_date = ?, description = ? where id = ?",
            f.date, f.description, visitId
        ).fetchNone();
    }

    @DeleteMapping("/visits/{visitId}") void deleteVisit(
        @PathVariable("visitId") int visitId
    ) {
        ds.q("delete from visits where id = ?", visitId).fetchNone();
    }
}
