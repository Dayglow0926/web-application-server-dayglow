package util;


import lombok.AllArgsConstructor;
import lombok.Getter;
import strategy.ArgumentMappingResolverStrategy;
import strategy.BodyArgumentMappingResolver;
import strategy.QueryArgumentMappingResolver;

@Getter
@AllArgsConstructor
public enum HttpMethod {
     GET   (new QueryArgumentMappingResolver())
    ,POST  (new BodyArgumentMappingResolver() )
    ,PUT   (new BodyArgumentMappingResolver() )
    ,DELETE(new BodyArgumentMappingResolver() );

    private ArgumentMappingResolverStrategy strategy;

    public boolean isPost() {
        return this.equals(POST);
    }
}
