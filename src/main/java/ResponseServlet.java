import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jasper.tagplugins.jstl.core.Out;


@WebServlet(
	urlPatterns = { "/response/*" }, 
	initParams = { 
			@WebInitParam(name = "Url", value = "jdbc:mysql://localhost:3306/Linux"), 
			@WebInitParam(name = "UserName", value = "root"), 
			@WebInitParam(name = "Password", value = "Admin@123")
	})

public class ResponseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private PreparedStatement pstmt = null;
	private Integer idToBeUpdated = null;
	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver loaded successfully");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init() throws ServletException {
		String jdbcUrl = getInitParameter("Url");
		String user = getInitParameter("UserName");
		String password = getInitParameter("Password");
		try {
			connection = DriverManager.getConnection(jdbcUrl, user, password);
			System.out.println("Connection established successfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
//		System.out.println(request.getRequestURI());
//		System.out.println(request.getContextPath());
//		System.out.println(request.getServletPath());
//		System.out.println(request.getPathInfo());
//		System.out.println(request.getQueryString());
		System.out.println("Hello hi");
		
		String requestByUser = request.getPathInfo();
		PrintWriter out = response.getWriter();
		System.out.println(requestByUser);
		
		if(requestByUser.endsWith("insert")) {
			Integer id = Integer.parseInt(request.getParameter("id"));
			String name = request.getParameter("name");
			String address = request.getParameter("address");
			Integer age = Integer.parseInt(request.getParameter("age"));
	
			String insertQuery = "insert into student(sid, sname, saddress, sage) values(?,?,?,?)";
			String checkQuery = "select count(*) from student where sid = " + id;
			
			if(connection != null) {
				boolean flag = false;
				try {
					pstmt  = connection.prepareStatement(checkQuery);
					if(pstmt != null) {
						ResultSet resultSet = pstmt.executeQuery();
						if(resultSet.next()) {
							out.println("<center><h1 style='color:red'>Student with given id already exists</h1></center>");
						} else {
							pstmt = connection.prepareStatement(insertQuery);
							pstmt.setInt(1,  id);
							pstmt.setString(2, name);
							pstmt.setString(3, address);
							pstmt.setInt(4,  age);
							flag = true;
						}
					}
					
					if(flag && pstmt != null) {
						int rowAffected = pstmt.executeUpdate();
						if(rowAffected == 1) {
							out.println("<h1 style='color:green;text-align:center;'> REGISTRATION SUCCESSFULL</h1>");
							out.close();
						} else {
							out.println("<h1 style='color:red; text-align:center;'> REGISTRATION FAILED</h1>");
						}
					}
				} catch (SQLException e) {
	 				e.printStackTrace();
				}
			}
		} else if(requestByUser.endsWith("read")) {
			Integer id = Integer.parseInt(request.getParameter("id"));
			
			String query = "select sname, saddress, sage from student where sid = " + id;
			if(connection != null) {
				try {
					pstmt  = connection.prepareStatement(query);
					if(pstmt != null) {
						ResultSet resultSet = pstmt.executeQuery();
						boolean flag = false;
						while(resultSet.next()) {
							out.println("<center><table border='1'");
							out.println("<tr><th>Name</th><th>Address</th><th>Age</th>");
							out.println("<tr><td>" + resultSet.getString(1) + "</td><td>" + resultSet.getString(2) + "</td><td>" + resultSet.getInt(3) + "</td></tr>");
							out.println("</table></center>");
							flag = true;
						}
						if(!flag) {
							out.println("<center><h1 style='color:red'>Student with given id does not exist</h1></center");
						}
					}
				} catch (SQLException e) {
	 				e.printStackTrace();
				}
			}
		} else if(requestByUser.endsWith("delete")) {
			Integer id = Integer.parseInt(request.getParameter("id"));
			
			String query = "delete from student where sid = " + id;
			if(connection != null) {
				try {
					pstmt  = connection.prepareStatement(query);
					if(pstmt != null) {
						int rowAffected = pstmt.executeUpdate();
						if(rowAffected > 0) {
							out.println("<center><h1 style='color:green'>Record has been deleted successfully</h1></center>");
						} else {
							out.println("<center><h1 style='color:red'>Student with given id does not exist</h1></center>");
						}
					}
				} catch (SQLException e) {
	 				e.printStackTrace();
				}
			}
		}else if(requestByUser.endsWith("update")) {
			idToBeUpdated = Integer.parseInt(request.getParameter("id"));
			String query = "select sid, sname, saddress, sage from student where sid = " + idToBeUpdated;
			if(connection != null) {
				try {
					pstmt  = connection.prepareStatement(query);
					if(pstmt != null) {
						ResultSet resultSet = pstmt.executeQuery();
						boolean flag = false;
						while(resultSet.next()) {
							out.println("""
									<!DOCTYPE html>
									<html>
									<head>
									    <title>Student Updation Form</title>
									    <style>
									        body {
									            font-family: Arial, sans-serif;
									            background-color: #f5f5f5;
									            margin: 0;
									            padding: 0;
									            display: flex;
									            justify-content: center;
									            align-items: center;
									            min-height: 100vh;
									        }
									        
									        .container {
									            background-color: #ffffff;
									            border-radius: 10px;
									            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
									            padding: 40px;
									            width: 400px;
									        }
									        
									        h1 {
									            text-align: center;
									            margin-bottom: 20px;
									            color: #333333;
									        }
									        
									        label {
									            font-weight: bold;
									            color: #555555;
									        }
									        
									        input[type="text"],
									        textarea,
									        input[type="number"] {
									            width: 100%;
									            padding: 10px;
									            margin-bottom: 20px;
									            border: 1px solid #dddddd;
									            border-radius: 5px;
									            font-size: 16px;
									        }
									        
									        textarea {
									            resize: vertical;
									        }
									        
									        input[type="submit"] {
									            background-color: #4caf50;
									            color: white;
									            border: none;
									            padding: 10px 20px;
									            border-radius: 5px;
									            cursor: pointer;
									            font-size: 16px;
									        }
									        
									        input[type="submit"]:hover {
									            background-color: #45a049;
									        }
									    </style>
									</head>
									<body>
									    <div class="container">
									        <h1>Student Updation Form</h1>
									        <form action="./update_request" method="get">
												<label for="id">Student id</label>
									"""+
												"<input type=\"number\" name=\"id\" placeholder="+resultSet.getInt(1)+" readonly>\n\n"+
												
									            "<label for=\"name\">Name:</label>\n"+
									            "<input type=\"text\" id=\"name\" name=\"name\" placeholder="+resultSet.getString(2)+" required>\n\n"+
									            
									            "<label for=\"address\">Address:</label>\n"+
									            "<textarea id=\"address\" name=\"address\" rows=\"4\" placeholder="+resultSet.getString(3)+" required></textarea>\n\n"+
									            
									            "<label for=\"age\">Age:</label>\n"+
									            "<input type=\"number\" id=\"age\" name=\"age\" placeholder="+resultSet.getInt(4)+" required>\n\n"+
									            
									            "<input type=\"submit\" value=\"Submit\">\n"+
									            
									        "</form>\n"+
									    "</div>\n"+
									"</body>\n"+
									"</html>\n"
									);
							flag = true;
						}
						if(!flag) {
							out.println("<center><h1 style='color:red'>Student with given id does not exist</h1></center");
						}
					}
				} catch (SQLException e) {
	 				e.printStackTrace();
				}
			}
		} else if(requestByUser.endsWith("update_request")) {
			Integer id = idToBeUpdated;
			String name = request.getParameter("name");
			String address = request.getParameter("address");
			Integer age = Integer.parseInt(request.getParameter("age"));
			System.out.println(id + " " + name + " " + address + " " + age);
			
			String query = String.format("update student set sname = '%s', saddress = '%s', sage = %d where sid = %d;", name, address, age, id);
			if(connection != null) {
				try {
					pstmt  = connection.prepareStatement(query);
					if(pstmt != null) {
						int rowAffected = pstmt.executeUpdate();
						if(rowAffected == 1) {
							out.println("<h1 style='color:green;text-align:center;'> UPDATION SUCCESSFULL</h1>");
						} else {
							out.println("<h1 style='color:red; text-align:center;'> UPDATION FAILED</h1>");
						}
						out.close();
					}
				} catch (SQLException e) {
	 				e.printStackTrace();
				}
			}
	
		}
	}
	
	@Override
	public void destroy() {
		System.out.println("Servlet has been deleted");
		if(connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}

