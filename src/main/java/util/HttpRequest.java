package util;

import strategy.ArgumentMappingResolverStrategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private Map<String,String> headers = new HashMap<String,String>();
    private Map<String,String> params = new HashMap<String,String>();
    private RequestLine requestLine;
    private HttpMethod method;

    public HttpRequest(InputStream in){
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();

            if(line == null){
                return;
            }

            requestLine = new RequestLine(line);

            line = br.readLine();
            while (!line.equals("")){

                String[] tokens = line.split(": ");
                headers.put(tokens[0].trim(), tokens[1].trim());
                line = br.readLine();

            }

            method = HttpMethod.valueOf(requestLine.getMethod());

            ArgumentMappingResolverStrategy.exchenge(getMethod(), );

            if(method.isPost()){
                String body = IOUtils.readData(br,Integer.parseInt(headers.get("Content-Length")));
                params = HttpRequestUtils.parseQueryString(body);
            }else{
                params = requestLine.getParams();
            }

        }catch (IOException io){
            System.out.println(io);
        }
    }

    public String getHeader(String name){
        return headers.get(name);
    }
    public Map getHeader(){
        return headers;
    }

    public Map getParams(){
        return params;
    }

    public String getMethod(){
        return requestLine.getMethod();
    }

    public String getPath(){
        return requestLine.getPath();
    }

}
