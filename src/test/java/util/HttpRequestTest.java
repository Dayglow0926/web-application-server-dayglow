package util;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;




public class HttpRequestTest {
    private String testDirectory = "./src/test/resources/";

    @Test
    public void request_GET() throws Exception{
        InputStream in = new FileInputStream(new File(testDirectory + "Http_GET.txt"));
        HttpRequest request = new HttpRequest(in);

        System.out.println(request.getMethod());
        System.out.println(request.getParams());
        System.out.println(request.getPath());
        System.out.println(request.getHeader());


    }
}
