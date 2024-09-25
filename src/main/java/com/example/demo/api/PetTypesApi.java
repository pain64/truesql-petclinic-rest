package com.example.demo.api;

import net.truej.sql.TrueSql;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.demo.api.PetTypesApiTrueSql.*;

import static com.example.demo.PetClinic.*;

@CrossOrigin(origins = "http://localhost:4200")
@TrueSql @RestController class PetTypesApi {

    @Autowired MainDb ds;

    private List<PetType> find(@Nullable Integer id) {
        return ds.q(
            "select id, name from types where (id = ? or ?::int is null)", id, id
        ).g.fetchList(PetType.class);
    }

    @GetMapping(value = "/pettypes") List<PetType> list() {
        return find(null);
    }

    @GetMapping("/pettypes/{petTypeId}") @Nullable PetType get(
        @PathVariable("petTypeId") int petTypeId
    ) {
        var petTypes = find(petTypeId);
        return petTypes.isEmpty() ? null : petTypes.getFirst();
    }

    record PetTypeFields(String name) { }

    @PostMapping("/pettypes") PetType add(
        @RequestBody PetTypeFields f
    ) {
        return new PetType(
            ds.q("insert into types values(default, ?) returning id", f.name)
                .fetchOne(Integer.class), f.name
        );
    }

    @PutMapping("/pettypes/{petTypeId}") void update(
        @PathVariable("petTypeId") int petTypeId, @RequestBody PetTypeFields f
    ) {
        ds.q("update types set name = ? where id = ?", f.name, petTypeId).fetchNone();
    }

    @DeleteMapping("/pettypes/{petTypeId}") void delete(
        @PathVariable("petTypeId") int petTypeId
    ) {
        ds.q("delete from types where id = ?", petTypeId).fetchNone();
    }
}
