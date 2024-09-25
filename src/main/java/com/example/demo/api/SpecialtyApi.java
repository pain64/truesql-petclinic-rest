package com.example.demo.api;

import net.truej.sql.TrueSql;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.PetClinic.*;

import com.example.demo.api.SpecialtyApiTrueSql.*;

@CrossOrigin(origins = "http://localhost:4200")
@TrueSql @RestController class SpecialtyApi {

    @Autowired MainDb ds;

    private List<Specialty> find(@Nullable Integer id) {
        return ds.q("""
            select id, name from specialties
            where (id = ? or ?::int is null) order by id""",
            id, id
        ).g.fetchList(Specialty.class);
    }


    @GetMapping("/specialties") List<Specialty> list() {
        return find(null);
    }

    @GetMapping("/specialties/{specialtyId}") @Nullable Specialty get(
        @PathVariable("specialtyId") int specialtyId
    ) {
        var specialties = find(specialtyId);
        return specialties.isEmpty() ? null : specialties.getFirst();
    }

    record SpecialtyFields(String name) { }

    @PostMapping("/specialties") int add(
        @RequestBody SpecialtyFields f
    ) {
        return ds.q("insert into specialties values(default, ?) returning id", f.name)
            .fetchOne(Integer.class);
    }

    @PutMapping(value = "/specialties/{specialtyId}") void update(
        @PathVariable("specialtyId") int specialtyId, @RequestBody SpecialtyFields f
    ) {
        ds.q("update specialties set name = ? where id = ?", f.name, specialtyId).fetchNone();
    }


    @DeleteMapping("/specialties/{specialtyId}") void delete(
        @PathVariable("specialtyId") int specialtyId
    ) {
        ds.q("delete from specialties where id = ?", specialtyId).fetchNone();
    }
}
