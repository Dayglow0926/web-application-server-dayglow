package util;

import org.junit.Test;

import java.util.Map;

public class RequestLineTest {
    @Test
    public void create_method(){
        RequestLine line = new RequestLine("GET /index.html HTTP/1.1");
        System.out.println(line.getMethod());
        System.out.println(line.getPath());

        line = new RequestLine("POST /index.html HTTP/1.1");
        System.out.println(line.getPath());
    }

    @Test
    public void create_path_and_params(){
        RequestLine line = new RequestLine("GET /user/create?userId=dayglow&password=1234 HTTP/1.1");
        System.out.println(line.getMethod());
        System.out.println(line.getPath());

        Map<String,String> params = line.getParams();
        System.out.println(params.size());
    }
}
