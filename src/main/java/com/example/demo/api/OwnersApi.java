package com.example.demo.api;

import net.truej.sql.TrueSql;
import net.truej.sql.dsl.Constraint;
import net.truej.sql.dsl.ConstraintViolationException;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.demo.PetClinic.MainDb;
import com.example.demo.api.OwnersApiTrueSql.*;

@CrossOrigin(origins = "http://localhost:4200")
@TrueSql @RestController class OwnersApi {
    // Функции
    // f(x, y) =
    // f(self, x, y) =
    // F: (S, Args) -> (Result, S')
    // The test:
    //  1. задание контекста S
    //  2. apply F (S, Args)
    //  3. assert Result
    //  4. assert S'
    // Module ~ Function

    // bar() {
    //     writeFile()
    //     httpRequestBody()
    // }

    @Autowired MainDb ds;

    private List<Owner> find(@Nullable Integer id, @Nullable String lastName) {
        return ds.q("""
                select
                    o.id                                          ,
                    o.first_name                                  ,
                    o.last_name                                   ,
                    o.address                                     ,
                    o.city                                        ,
                    o.telephone                                   ,
                    p.id          as "Pet pets.                  ",
                    p.name        as "    pets.                  ",
                    p.birth_date  as "    pets.                  ",
                    p.owner_id    as "    pets.                  ",
                    p.type_id     as "    pets.                  ",
                    t.name        as "    pets.typeName          ",
                    v.id          as "    pets.Visit visits.     ",
                    v.pet_id      as "    pets.      visits.     ",
                    v.visit_date  as "    pets.      visits.date ",
                    v.description as "    pets.      visits.     "
                from owners o
                    left join pets   p on o.id     = p.owner_id
                    left join types  t on t.id     = p.type_id
                    left join visits v on v.pet_id = p.id
                where
                     (o.last_name like ? or ?::text is null) and
                     (o.id           = ? or ?::int  is null)
                order by o.id, p.id
                """,
            (lastName == null ? "" : lastName) + "%", lastName,
            id, id
        ).g.fetchList(Owner.class);
    }

    @GetMapping("/owners") List<Owner> list(
        @RequestParam(required = false) @Nullable String lastName
    ) {
        return find(null, lastName);
    }

    @GetMapping("/owners/{ownerId}") Owner get(
        @PathVariable("ownerId") int ownerId
    ) {
        var owners = find(ownerId, null);
        return owners.isEmpty() ? null : owners.getFirst();
    }

    record OwnerFields(
        @Nullable String firstName,
        @Nullable String lastName,
        @Nullable String address,
        @Nullable String city,
        @Nullable String telephone
    ) { }

    @PostMapping("/owners") Owner add(
        @RequestBody OwnerFields f
    ) {
        var id = ds.q(
            "insert into owners values(default, ?, ?, ?, ?, ?)",
            f.firstName, f.lastName, f.address, f.city, f.telephone
        ).asGeneratedKeys("id").fetchOne(int.class);

        return find(id, null).getFirst();
    }

    @PutMapping("/owners/{ownerId}") Owner update(
        @PathVariable("ownerId") int ownerId, @RequestBody OwnerFields f
    ) {
        ds.q("""
                update owners set
                    first_name = ?, last_name = ?, address = ?, city = ?, telephone = ?
                where id = ?""",
            f.firstName, f.lastName, f.address, f.city, f.telephone, ownerId
        ).fetchNone();

        return find(ownerId, null).getFirst();
    }

    @DeleteMapping("/owners/{ownerId}") void delete(
        @PathVariable("ownerId") Integer ownerId
    ) {
        // FIXME: cascade ???
        ds.q("delete from owners where id = ?", ownerId).fetchNone();
    }
}
