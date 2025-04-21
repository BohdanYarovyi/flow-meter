package com.yarovyi.flowmeter.entity.domainDto;

/**
 * Here no transportation passwords, because it is in another place.
 * @see com.yarovyi.flowmeter.util.AccountMapper#COMMIT_CREDENTIALS_UPDATES
 *
 * @param login
 * @param email
 */
public record CredentialsDto(
        String login,
        String email
) {
}
