package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());



		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

			BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
			String line = br.readLine();

			log.debug("request line : {}",line);
			if(line == null) return;

			String[] tokens = line.split(" ");

			for(int i=1; !line.equals("")||i<2 ; i++){
				line = br.readLine();
				log.debug("header : {}",line);
			}

			// 경로와 쿼리스트리을 분리
			String url = tokens[1];

			if(url.startsWith("/user/create")){
				int mark = url.indexOf("?");
				String queryString = url.substring(mark+1);

				//User 클래스에 매핑
				User user = new User(HttpRequestUtils.parseQueryString(queryString));
				log.debug("User : {}",user);

				DataOutputStream dos = new DataOutputStream(out);
				response302Header(dos,"/index.html");

			}else {

				// TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
				DataOutputStream dos = new DataOutputStream(out);

				byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());

				if (url.endsWith(".css")) response200CssHeader(dos, body.length);
				else response200Header(dos, body.length);

				responseBody(dos, body);

			}

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response200CssHeader(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response302Header(DataOutputStream dos, String url){
		try{
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Location: "+url+"\r\n");
			dos.writeBytes("\r\n");
		}catch (IOException e){
			log.error(e.getMessage());
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

}