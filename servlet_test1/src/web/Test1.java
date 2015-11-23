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



/**
 * Servlet implementation class Test1
 */
@WebServlet("/Test1")
public class Test1 extends HttpServlet {
	
	public String JSONFilePath = "C:\\Users\\jenny\\Downloads\\asd.json";
	
	
	private static final long serialVersionUID = 3968791874190509438L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
   /** public Test1() {
        super();
        // TODO Auto-generated constructor stub
    }**/

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
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

				JSONOut.write(JSONString);
				JSONOut.print(JSONString);
				
				JSONOut.flush();
				JSONOut.close();
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		doPost(request, response);
		
	}

}
