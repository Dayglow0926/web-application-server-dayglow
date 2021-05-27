package strategy;

import java.util.Map;
import java.util.Objects;

public class QueryArgumentMappingResolver extends ArgumentMappingResolverStrategy{

    protected Map<String, String> exchenge(String ...values) {
        if( validation(values) ) throw new IllegalArgumentException("매핑 데이터가 존재하지 않음");

        // ...
        return null;
    }
}
