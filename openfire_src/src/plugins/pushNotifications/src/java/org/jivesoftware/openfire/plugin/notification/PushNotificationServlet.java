package org.jivesoftware.openfire.plugin.notification;

import org.dom4j.Namespace;
import org.dom4j.tree.BaseElement;
import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.plugin.NotificationPlugin;
import org.jivesoftware.util.JiveProperties;
import org.jivesoftware.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.PacketExtension;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by zhoulq on 5/6/14.
 */
public class PushNotificationServlet extends HttpServlet {

    private static final Logger Log = LoggerFactory.getLogger(PushNotificationServlet.class);
    private static final String TAG= "PushNotificationServlet";
    
    private static final String SERVICE_NAME = "notification/pushnotification";
    private NotificationPlugin plugin;
    private String domain = JiveProperties.getInstance().get("172.16.230.6");
    //private String domain = "172.16.230.6";
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("1--PushNotificationServlet init()");
        plugin = (NotificationPlugin) XMPPServer.getInstance().getPluginManager().getPlugin("notification");
        AuthCheckFilter.addExclude(SERVICE_NAME);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	initializePlugin();
		PrintWriter out = resp.getWriter();
		out.write("Hello,This is server!!!!");
        /*resp.setContentType("text/plain");
        System.out.println("1--PushNotificationServlet doGet()");
        String title = req.getParameter("title");
        String message = req.getParameter("message");
        plugin.sendNotificationToAllUser(title, message, "");*/
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();
        System.out.println("PushNotificationServlet doPost()");
        out.print("PushNotificationServlet doPost()");
        Log.info("PushNotificationServlet doPost()");
    }
	/**
	 * init plugin
	 */
	public void initializePlugin() {

        System.out.println("initializePlugin:send hello to all users!");
        new Thread(){
            @Override
            public void run() {
                while(true){
                    try {
                        sleep(10000);                  
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("hello");
                    XMPPServer.getInstance().getSessionManager().sendServerMessage(null, "hello PushNotification! ");
                }
            };
        }.start();
	}

    @Override
    public void destroy() {
        super.destroy();
        AuthCheckFilter.removeExclude(SERVICE_NAME);
    }
}
