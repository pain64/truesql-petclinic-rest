package com.example.demo.api;


import net.truej.sql.TrueSql;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.PetClinic.*;
import static net.truej.sql.source.Parameters.unfold;

import com.example.demo.api.VetsApiTrueSql.*;

@CrossOrigin(origins = "http://localhost:4200")
@TrueSql @RestController class VetsApi {

    @Autowired MainDb ds;

    List<Vet> find(@Nullable Integer id) {
        return ds.q("""
                select
                    v.id,
                    v.first_name                            ,
                    v.last_name                             ,
                    sp.id        as "Specialty specialties.",
                    sp.name      as "          specialties."
                from vets v
                left join vet_specialties vsp on v.id = vsp.vet_id
                left join specialties sp on vsp.specialty_id = sp.id
                where v.id = ? or ?::int is null order by v.id""",
            id, id
        ).g.fetchList(Vet.class);
    }

    @GetMapping(value = "/vets") List<Vet> list() {
        return find(null);
    }

    @GetMapping("/vets/{vetId}") Vet get(
        @PathVariable("vetId") int vetId
    ) {
        var vets = find(vetId);
        return vets.isEmpty() ? null : vets.getFirst();
    }


    record Specialty(
        int id, String name
    ) { }
    record VetFields(
        int id, String firstName, String lastName, List<Specialty> specialties
    ) { }

    @PostMapping("/vets") void add(@RequestBody VetFields f) {
        ds.q("""
                with t1 as (
                    insert into vets values(default, ?, ?) returning id
                ), sp(id) as (
                    values ?
                )
                insert into vet_specialties
                select t1.id, sp.id::int from t1 cross join sp""",
            f.firstName, f.lastName,
            unfold(f.specialties, s -> new Object[]{s.id})
        ).fetchNone();
    }

    @PutMapping(value = "/vets/{vetId}") void update(
        @PathVariable("vetId") int vetId, @RequestBody VetFields f
    ) {
        ds.inTransaction(cn ->
            cn.q("""
                    update vets set first_name = ?, last_name = ? where id = ?;
                    delete from vet_specialties where vet_id = ?;
                    insert into vet_specialties select ?, sp.id::int from (values ?) as sp(id)""",
                f.firstName, f.lastName, vetId, vetId,
                vetId, unfold(f.specialties, s -> new Object[]{s.id})
            ).fetchNone()
        );
    }

    @DeleteMapping("/vets/{vetId}") void delete(
        @PathVariable("vetId") int vetId
    ) {
        ds.q("delete from vets where id = ?", vetId).fetchNone();
    }
}
