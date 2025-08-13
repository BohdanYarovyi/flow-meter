package com.yarovyi.flowmeter.testUtility.entityGenerator;

import com.yarovyi.flowmeter.entity.BaseEntity;
import com.yarovyi.flowmeter.entity.account.Account;
import org.instancio.Instancio;
import org.instancio.InstancioApi;

import static org.instancio.Select.field;

public class BaseEntityGenerator implements BaseEntityGeneratorRule {

    public static <T extends BaseEntity> InstancioApi<T> generateWithBaseEntity(Class<T> classType) {
        return Instancio.of(classType)
                .generate(field(Account::getId), ID_GENERATOR)
                .generate(field(Account::getCreatedAt), CREATED_AT_GENERATOR)
                .generate(field(Account::getUpdatedAt), UPDATED_AT_GENERATOR)
                .generate(field(Account::isDeleted), DELETED_GENERATOR);
    }

}
