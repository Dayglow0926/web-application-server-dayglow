package strategy;

import java.util.Map;

public class BodyArgumentMappingResolver extends ArgumentMappingResolverStrategy{

    protected Map<String, String> exchenge(String ...values) {
        if( validation(values) ) throw new IllegalArgumentException("매핑 데이터가 존재하지 않음");
        // POST 의 데이터 매핑을 위한 전략

        // FATCH, PUT 등등 여러 매핑에서도 이 전략을 사용할 수 있음
        return null;
    }
}