package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequest;
import util.HttpRequestUtils;
import util.HttpResponse;
import util.IOUtils;

import javax.xml.crypto.Data;

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

			HttpRequest request = new HttpRequest(in);

			HttpResponse response = new HttpResponse(out);

			String path = getDefaultPath(request.getPath());

			// url 부분을 변수 처리를 통한 데이터 처리
			if(path.equals("/user/create")){
				User user = new User(request.getParams());

				log.debug("User : {}",user);

				DataBase.addUser(user);

				DataOutputStream dos = new DataOutputStream(out);
				response302Header(dos,"/index.html");

			}else if(path.startsWith("/user/create")){

				//User 클래스에 매핑
				User user = new User(request.getParams());
				log.debug("User : {}",user);

				DataOutputStream dos = new DataOutputStream(out);
				response302Header(dos,"/index.html");

			}else if("/user/login".equals(path)){

				Map<String, String> params = request.getParams();

				log.debug("params : {}", params);

				User user = DataBase.findUserById(params.get("userId"));
				if (user == null) {
					responseResource(out, "/user/login_failed.html");
					return;
				}

				if (user.getPassword().equals(params.get("password"))) {
					DataOutputStream dos = new DataOutputStream(out);
					response302LoginSuccessHeader(dos);
				} else {
					responseResource(out, "/user/login_failed.html");
				}
			}else if("/user/logout".equals(path)){

				DataOutputStream dos = new DataOutputStream(out);
				response302LogOutHeader(dos);

			} else if("/user/list".equals(path)){
				if(!isLogin(request.getHeader("Cookie"))){
					responseResource(out, "/user/login.html");
					return;
				}

				Collection<User> users = DataBase.findAll();

				StringBuilder sb = new StringBuilder();
				sb.append("<table border='1'>");

				System.out.println("==============");
				System.out.println(users.toString());
				System.out.println("==============");

				for(User user : users){
					sb.append("<tr>");
					sb.append("<td>"+user.getUserId()+"</td>");
					sb.append("<td>"+user.getName()+"</td>");
					sb.append("<td>"+user.getEmail()+"</td>");
					sb.append("</tr>");
				}

				sb.append("</table>");
				byte[] body = sb.toString().getBytes();
				DataOutputStream dos = new DataOutputStream(out);
				response200Header(dos, body.length);
				responseBody(dos,body);

			}else {

				/*if(url.contains("login.html") && login){
					DataOutputStream dos = new DataOutputStream(out);
					response302LoginSuccessHeader(dos);
				}*/

				responseResource(out,path);
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

	private int getContentLength(String line){
		// contentlength 의 형태를 생각해서 : 나눈 후 공백을 제외한 값을 전
		String[] headerTokens = line.split(":");
		return Integer.parseInt(headerTokens[1].trim());
	}

	private void responseResource(OutputStream out, String url) throws IOException{
		DataOutputStream dos = new DataOutputStream(out);
		byte[] body = Files.readAllBytes(new File("./webapp"+url).toPath());

		if (url.endsWith(".css")) response200CssHeader(dos, body.length);
		else response200Header(dos, body.length);

		responseBody(dos, body);
	}

	private void response302LoginSuccessHeader(DataOutputStream dos){
		try{
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Set-Cookie: logined=true \r\n");
			dos.writeBytes("Location: /index.html \r\n");
			dos.writeBytes("\r\n");
		}catch (IOException e){
			log.error(e.getMessage());
		}
	}

	private void response302LogOutHeader(DataOutputStream dos){
		try{
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Set-Cookie: logined=false \r\n");
			dos.writeBytes("Location: /index.html \r\n");
			dos.writeBytes("\r\n");
		}catch (IOException e){
			log.error(e.getMessage());
		}
	}


	private String getDefaultPath(String path){
		if(path.equals("/")){
			return "/index.html";
		}
		return path;
	}

	private boolean isLogin(String cookie){
		Map<String,String> cookies = HttpRequestUtils.parseCookies(cookie);

		String value = cookies.get("logined");
		if(value == null){
			return false;
		}

		return Boolean.parseBoolean(value);
	}

}