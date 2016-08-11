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
    private String domain = JiveProperties.getInstance().get("xmpp.domain");
    //private String domain = "172.16.230.6";
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        plugin = (NotificationPlugin) XMPPServer.getInstance().getPluginManager().getPlugin("notification");
        AuthCheckFilter.addExclude(SERVICE_NAME);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		String toJID = request.getParameter("toJID");
		String subject = request.getParameter("subject");
		String content = request.getParameter("content");		
		/*
		 * System.out.println("\n-------------\ninter params:\n");
		 * System.out.println("toJID\t" + toJID); System.out.println("subject\t"
		 * + subject); System.out.println("content\t" + content);
		 */
		new PushThread(toJID, subject, content).start();

		out.write("Hello,This is server!");
    }


   @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	doGet(req,resp);
    }


    @Override
    public void destroy() {
        super.destroy();
        AuthCheckFilter.removeExclude(SERVICE_NAME);
    }
	public int getUserCount(String where) {
		//SysTime.PrintCurrentTime(TAG, "getUserCount() begin");
		int count = 0;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con
					.prepareStatement("SELECT count(1) FROM ofUser " + where);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
			System.out.println("User Number:"+count);
		} catch (SQLException e) {
			Log.error(e.getMessage(), e);
		} finally {
			DbConnectionManager.closeConnection(rs, pstmt, con);
		}
		//SysTime.PrintCurrentTime(TAG, "getUserCount() end");
		return count;
	}
    
    class PushThread extends Thread {

		private String toJid;
		private String subject;
		private String content;
        public PushThread(String toJID, String subject, String content) {
        	this.content = content;
        	this.subject  = subject;
        	this.toJid = toJID;
			System.out.println("Push:  toJid:"+toJid+"  subject:"+subject+"  content:"+content);
		
		}


		public void run() {
			//SysTime.PrintCurrentTime(TAG, "run() begin");
			if (toJid != "-1") {
				//routePacket(toJid, subject, content);
				//push2User(toJid, subject, content, i * limit, limit);
				
				XMPPServer.getInstance().getSessionManager()
		           .sendServerMessage(new JID(toJid+ "@" + domain), subject, content);
			} else {
	
				int limit = 50;
				//if (toJid == "-1") { // all users
			    
				int count = getUserCount("");

				int cycleCount = count / limit + (count % limit == 0 ? 0 : 1);
				for (int i = 0; i < cycleCount; i++) {
					push2User(toJid, subject, content, i * limit, limit);
				}
				
				//} 
					/*else { // company users
					int count = getUserCount(String.format(
							" where companyId like '%s' ", toJid));
					int cycleCount = count / limit
							+ (count % limit == 0 ? 0 : 1);
					for (int i = 0; i < cycleCount; i++) {
						push2User(fromJid, toJid, subject, content, i * limit,
								limit);
					}
				}*/
			}
			//SysTime.PrintCurrentTime(TAG, "run() end ");
		}
		

	}
    
	private Collection<String> push2User(String toJid,
			String subject, String content, int startIndex, int numResults) {
		//SysTime.PrintCurrentTime(TAG, "push2User() begin");
		List<String> usernames = new ArrayList<String>(500);
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = String
					.format(
							"SELECT username FROM ofUser ORDER BY username",
							toJid);
			con = DbConnectionManager.getConnection();
			if ((startIndex == 0) && (numResults == Integer.MAX_VALUE)) {

				pstmt = con.prepareStatement(sql);
				// Set the fetch size. This will prevent some JDBC drivers from
				// trying
				// to load the entire result set into memory.
				DbConnectionManager.setFetchSize(pstmt, 500);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					/*
					 * if(isApple(rs.getString(2))){ new
					 * ApplePushThread(rs.getString(2),fromJid,content); }else{
					 */
					//routePacket(rs.getString(1), subject, content);
//					/System.out.println(x);
					XMPPServer.getInstance().getSessionManager()
					           .sendServerMessage(new JID(rs.getString(1)+ "@" + domain), subject, content);
					//}
					
				}
			} else {
				pstmt = DbConnectionManager.createScrollablePreparedStatement(
						con, sql);
				DbConnectionManager.limitRowsAndFetchSize(pstmt, startIndex,
						numResults);
				rs = pstmt.executeQuery();
				DbConnectionManager.scrollResultSet(rs, startIndex);
				int count = 0;
				while (rs.next() && count < numResults) {
					/*
					 * if(isApple(rs.getString(2))){ new
					 * ApplePushThread(rs.getString(2),fromJid,content); }else{
					 */
					//routePacket(rs.getString(1), subject, content);
					XMPPServer.getInstance().getSessionManager()
			           .sendServerMessage(new JID(rs.getString(1)+ "@" + domain), subject, content);
					
					// }
				}
			}
		} catch (SQLException e) {
			Log.error(e.getMessage(), e);
		} finally {
			DbConnectionManager.closeConnection(rs, pstmt, con);
		}
		//SysTime.PrintCurrentTime(TAG, "push2User() end");
		return usernames;
	}
}
