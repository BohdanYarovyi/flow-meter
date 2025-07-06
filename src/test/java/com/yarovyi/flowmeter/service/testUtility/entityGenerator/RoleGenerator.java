package com.yarovyi.flowmeter.service.testUtility.entityGenerator;

import com.yarovyi.flowmeter.entity.account.Role;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.List;
import java.util.Set;

import static org.instancio.Select.field;

public class RoleGenerator implements RoleGeneratorRule {

    public static Role oneRole() {
        return Instancio.of(base()).create();
    }

    public static Set<Role> roles(int count) {
        return Instancio.ofSet(base())
                .size(count)
                .create();
    }

    public static Role adminRole() {
        return new Role(1L, "ROLE_ADMIN");
    }

    public static Role userRole() {
        return new Role(2L, "ROLE_USER");
    }

    private static Model<Role> base() {
        return Instancio.of(Role.class)
                .generate(field(Role::getId), ID_GENERATOR)
                .generate(field(Role::getName), ROLE_NAME_GENERATOR)
                .toModel();
    }

}
