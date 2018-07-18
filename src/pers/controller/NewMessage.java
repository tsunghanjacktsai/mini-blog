package pers.controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pers.model.UserService;

@WebServlet(
		urlPatterns={"/new_message"},
		initParams={
				@WebInitParam(name = "MEMBER_PATH", value = "member")
		}
)
public class NewMessage extends HttpServlet {
	private final String USERS = "c:/workspace/users";
	private final String LOGIN_PATH = "index.html";
	private final String MEMBER_PATH = "member";
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 若無 login 屬性，直接重導向至登入網頁
		 */
		if(request.getSession().getAttribute("login") == null) {
			response.sendRedirect(LOGIN_PATH);
			return;
		}
		request.setCharacterEncoding("UTF-8");
		String blabla = request.getParameter("blabla");
		
		if(blabla == null || blabla.length() == 0) { //無信息時直接重導向至會員頁面
			response.sendRedirect(getInitParameter("MEMBER_PATH"));
			return;
		}
		
		if(blabla.length() <= 140) {
			UserService userService = (UserService) getServletContext().getAttribute("userService");
			userService.addMessage(getUsername(request), blabla);
			response.sendRedirect(getInitParameter("MEMBER_PATH"));
		} else {
			request.getRequestDispatcher(getInitParameter("MEMBER_PATH")).forward(request, response);
		}
	}
	
	private String getUsername(HttpServletRequest request) {
		return (String) request.getSession().getAttribute("login");
	}

	private void addMessage(String username, String blabla) throws IOException {
		Path txt = Paths.get(
				USERS, 
				username, 
				String.format("%s.txt", Instant.now().toEpochMilli()));
		try(BufferedWriter writer = Files.newBufferedWriter(txt)) {
			writer.write(blabla);
		}
	}
}
