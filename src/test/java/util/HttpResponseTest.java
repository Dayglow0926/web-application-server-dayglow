package util;

import org.junit.Test;

import java.io.*;
import java.net.http.HttpResponse;
import java.nio.file.Files;

public class HttpResponseTest {
    private String testDirectory = "./src/test/resources";

    @Test
    public void responseForward() throws Exception{
        //HttpResponse response = new HttpResponse(createOutputStream("Http_Forward.txt"));

    }

    private OutputStream createOutputStream(String filename) throws FileNotFoundException {
        return new FileOutputStream(new File(testDirectory + filename));
    }

}
