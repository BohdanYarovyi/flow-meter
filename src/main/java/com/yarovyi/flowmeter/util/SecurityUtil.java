package com.yarovyi.flowmeter.util;

import java.util.function.Function;

public class SecurityUtil {

    public static final Function<String,String> PERFORM_WITH_ROLE_PREFIX = (nameOfRole) -> "ROLE_" + nameOfRole;
}
