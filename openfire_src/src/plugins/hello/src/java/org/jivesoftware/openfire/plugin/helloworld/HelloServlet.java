package org.jivesoftware.openfire.plugin.helloworld;

import java.io.IOException;  
import java.io.PrintWriter;  
  
import javax.servlet.ServletException;  
import javax.servlet.http.HttpServlet;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
  
import org.jivesoftware.admin.AuthCheckFilter;  
  
public class HelloServlet extends HttpServlet {  
    private static final String SERVICE_NAME = "hello/hello";
  
    /** 
     *  
     */  
    private static final long serialVersionUID = 1L;  
  
    @Override  
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)  
            throws ServletException, IOException {  
        // TODO Auto-generated method stub  
    //  super.doGet(req, resp);  
        resp.setContentType("text/plain");  
        PrintWriter out = resp.getWriter();  
        System.out.println("HelloServlet GET Method");  
        out.print("HelloServlet GET Method");  
        out.flush();  
    }  
  
    @Override  
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)  
            throws ServletException, IOException {  
        // TODO Auto-generated method stub  
    //  super.doPost(req, resp);  
        resp.setContentType("text/plain");  
        PrintWriter out = resp.getWriter();  
        System.out.println("HelloServlet GET Method");  
        out.print("HelloServlet POST Method");  
        out.flush();          
    }  
  
    @Override  
    public void init() throws ServletException {  
        // TODO Auto-generated method stub  
        super.init();  
        AuthCheckFilter.addExclude(SERVICE_NAME);
    }  
  
    @Override  
    public void destroy() {  
        AuthCheckFilter.removeExclude(SERVICE_NAME); 
        // TODO Auto-generated method stub  
        super.destroy();  
    }  
  
}  