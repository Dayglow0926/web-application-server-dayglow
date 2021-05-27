package strategy;

import util.HttpMethod;

import java.util.Map;
import java.util.Objects;

/*
* Strategy */
public abstract class ArgumentMappingResolverStrategy {

    public abstract Map<String, String> exchange(HttpMethod method, String... values);

    /**
     * URL 유효성 검사
     */
    protected static boolean validation(String... values){

        return false;
    }
}

