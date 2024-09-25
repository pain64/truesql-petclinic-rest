package com.example.demo.api;

import net.truej.sql.TrueSql;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.api.PetsApiTrueSql.Pet;

import java.time.LocalDate;

import static com.example.demo.PetClinic.*;

@CrossOrigin(origins = "http://localhost:4200")
@TrueSql @RestController class PetsApi {

    @Autowired MainDb ds;

    @GetMapping("/pets/{petId}") @Nullable Pet get(
        @PathVariable("petId") int petId
    ) {
        return ds.q("""
            select
                p.id                                ,
                p.name                              ,
                p.birth_date                        ,
                p.owner_id                          ,
                p.type_id                           ,
                t.name        as "typeName         ",
                v.id          as "Visit visits.    ",
                v.pet_id      as "      visits.    ",
                v.visit_date  as "      visits.date",
                v.description as "      visits.    "
            from pets p
                left join types  t on t.id = p.type_id
                left join visits v on v.pet_id = p.id
            where
                 p.id = ? order by p.id""", petId
        ).g.fetchOneOrZero(Pet.class);
    }

    record PetType(int id, String name) { }
    record PetFields(String name, LocalDate birthDate, PetType type) { }

    @PostMapping("/owners/{ownerId}/pets") void add(
        @PathVariable("ownerId") int ownerId, @RequestBody PetFields f
    ) {
        ds.q(
            "insert into pets values(default, ?, ?, ?, ?)",
            f.name, f.birthDate, f.type.id, ownerId
        ).fetchNone();
    }

    @PutMapping("/pets/{petId}") void update(
        @PathVariable("petId") int petId, @RequestBody PetFields f
    ) {
        ds.q(
            "update pets set name = ?, birth_date = ?, type_id = ?",
            f.name, f.birthDate, f.type.id
        ).fetchNone();
    }

    @DeleteMapping(value = "/pets/{petId}") void delete(
        @PathVariable("petId") int petId
    ) {
        // FIXME: cascade
        ds.q("delete from pets where id = ?", petId).fetchNone();
    }
}
