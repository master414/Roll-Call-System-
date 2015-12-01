package web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Test1")

public class Test1 extends HttpServlet{

	public String JSONFilePath = "C:\\Program Files\\Apache Software Foundation\\Tomcat 8.0\\webapps\\Named_json\\NewMsg.json";
	/**
	 * 
	 */
	private static final long serialVersionUID = 3968791874190509438L;

	/**
	* 處理post方式提交的資料
	*/
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//doGet(request, response);
		
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		PrintWriter JSONOut = response.getWriter();
		String JSONText = null, JSONString = null;

		File f = new File(JSONFilePath);
		StringBuilder Builder = new StringBuilder();

		try {
			BufferedReader Reader = new BufferedReader(new FileReader(f));
			while ((JSONText = Reader.readLine()) != null) {
				Builder.append(JSONText + "\n");
			}
			JSONString = Builder.toString();
			Reader.close();
		} catch (FileNotFoundException e) {
			// TODO 自動產生的 catch 區塊
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動產生的 catch 區塊
			e.printStackTrace();
		}
		//JSONString = new String(JSONString.getBytes("ISO-8859-1"), "UTF-8");
		JSONOut.write(JSONString);
		//JSONOut.print(JSONString);
		
		JSONOut.flush();
		JSONOut.close();
	}
	
	/**
	* 處理get方式提交的資料
	*/
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
}