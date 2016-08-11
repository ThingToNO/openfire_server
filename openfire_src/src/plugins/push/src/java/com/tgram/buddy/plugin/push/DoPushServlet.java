package com.tgram.buddy.plugin.push;

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

import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Namespace;
import org.dom4j.tree.BaseElement;
import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.JiveProperties;
import org.jivesoftware.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.PacketExtension;

public class DoPushServlet extends HttpServlet {

	private PushNodesPlugin plugin;

	private String domain = JiveProperties.getInstance().get("xmpp.domain");

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		plugin = (PushNodesPlugin) XMPPServer.getInstance().getPluginManager()
				.getPlugin("push");
		System.out.println(this + "\t" + System.currentTimeMillis());
		// Exclude this servlet from requiring the user to login
		// AuthCheckFilter.addExclude("userService/userservice");
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// Printwriter for writing out responses to browser
		PrintWriter out = response.getWriter();
		String p2p = request.getParameter("p2p");
		String fromJID = request.getParameter("fromJID");
		String toJID = request.getParameter("toJID");
		String content = request.getParameter("content");
		String subject = request.getParameter("subject");
		System.out.println("\n-------------\ninter params:\n");
		System.out.println("fromJID\t" + fromJID);
		System.out.println("toJID\t" + toJID);
		System.out.println("subject\t" + subject);
		System.out.println("content\t" + content);
		new PushThread(p2p, fromJID, toJID, subject, content).start();

		// 
		initializePlugin();
		out.print("hello");

	}
	
	/**
	 * 
	 * 
	 * @param where
	 * @return
	 */
	public void initializePlugin() {
        // TODO Auto-generated method stub
        System.out.println("initializePlugin:send hello to all users!");
        /*formatter=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");*/
        new Thread(){
            @Override
            public void run() {
                while(true){
                    try {
                        sleep(10000);                  
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    System.out.println("hello");
                    XMPPServer.getInstance().getSessionManager().sendServerMessage(null, "hello ");
                }
            };
        }.start();
	}
	
	
	
	

	public int getUserCount(String where) {
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
		} catch (SQLException e) {
			Log.error(e.getMessage(), e);
		} finally {
			DbConnectionManager.closeConnection(rs, pstmt, con);
		}
		return count;
	}
	
	private boolean isApple(String deviceToken){
		if(deviceToken!=null&&deviceToken.length()>0){
			return true;
		}
		return false;
	}

	private Collection<String> push2User(String fromJid, String toJid,
			String subject, String content, int startIndex, int numResults) {
		List<String> usernames = new ArrayList<String>(500);
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = String
					.format(
							"SELECT username,deviceToken FROM ofUser where companyId like '%s' ORDER BY username",
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
					if(isApple(rs.getString(2))){
						new ApplePushThread(rs.getString(2),fromJid,content);
					}else{
						routePacket(fromJid, rs.getString(1), subject, content);
					}
					
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
					if(isApple(rs.getString(2))){
						new ApplePushThread(rs.getString(2),fromJid,content);
					}else{
						routePacket(fromJid, rs.getString(1), subject, content);
					}
				}
			}
		} catch (SQLException e) {
			Log.error(e.getMessage(), e);
		} finally {
			DbConnectionManager.closeConnection(rs, pstmt, con);
		}
		return usernames;
	}

	private void routePacket(String fromJid, String toJid, String subject,
			String content) {
		Message message = new Message();

		message.setFrom(fromJid + "@" + domain);
		message.setTo(toJid + "@" + domain);
		message.setBody(content);
		message.setSubject(subject);

		BaseElement _Element = new BaseElement("extend", new Namespace("tgram",
				"http://tgram.com/buddy"));
		BaseElement _TimeElement = new BaseElement("time");
		_TimeElement.setText(String.valueOf(new Date().getTime()));
		_Element.add(_TimeElement);
		message.addExtension(new PacketExtension(_Element));

		JID t = new JID(toJid + "@" + domain);
		System.out.println("push to : \t" + t.toString());
		XMPPServer.getInstance().getRoutingTable()
				.routePacket(t, message, true);
	}

	private void replyMessage(String message, HttpServletResponse response,
			PrintWriter out) {
		response.setContentType("text/xml");
		out.println("<result>" + message + "</result>");
		out.flush();
	}

	private void replyError(String error, HttpServletResponse response,
			PrintWriter out) {
		response.setContentType("text/xml");
		out.println("<error>" + error + "</error>");
		out.flush();

	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	public void destroy() {
		super.destroy();
		// Release the excluded URL
		// AuthCheckFilter.removeExclude("userService/userservice");
	}

	class PushThread extends Thread {

		private String fromJid;
		private String toJid;
		private String subject;
		private String content;
		private String p2p;

		public PushThread(String p2p, String fromJid, String toJid,
				String subject, String content) {
			this.p2p = p2p;
			this.fromJid = fromJid;
			this.toJid = toJid;
			this.subject = subject;
			this.content = content;
		}

		public void run() {
			if ("p2p".equals(p2p)) {
				routePacket(fromJid, toJid, subject, content);
			} else {
				int limit = 50;
				if (toJid == "-1") { // all users
					int count = getUserCount("");

					int cycleCount = count / limit
							+ (count % limit == 0 ? 0 : 1);
					for (int i = 0; i < cycleCount; i++) {
						push2User(fromJid, toJid, subject, content, i * limit,
								limit);
					}
				} else { // company users
					int count = getUserCount(String.format(
							" where companyId like '%s' ", toJid));
					int cycleCount = count / limit
							+ (count % limit == 0 ? 0 : 1);
					for (int i = 0; i < cycleCount; i++) {
						push2User(fromJid, toJid, subject, content, i * limit,
								limit);
					}
				}
			}
		}

	}
	
	class ApplePushThread extends Thread{
		
		private final Logger log = LoggerFactory.getLogger(ApplePushThread.class);
		
		private String token;
		private String sender;
		private String message;
		
		public final boolean productModel = true;
		public  final String PASSWORD = JiveGlobals.getProperty("ios.device.push.password");
		public  final String P12_PATH = JiveGlobals.getProperty("ios.device.push.p12.path");
		
		/**
		 * @param token 
		 * @param sender
		 * @param message
		 * @param count
		 */
		public ApplePushThread(String token,String sender, String message){
			this.token=token;
			this.sender=sender;
			this.message=message;
		}
		
		public void run(){
			try {
				System.out.println("push to apple :");
				PushNotificationPayload payLoad = new PushNotificationPayload();
				payLoad.addAlert(message);
				payLoad.addBadge(1); 
				payLoad.addSound("default");
				payLoad.addCustomDictionary("username", sender);
				payLoad.addCustomDictionary("type", 1); //third push server
				PushNotificationManager pushManager = new PushNotificationManager();
				pushManager.initializeConnection(new AppleNotificationServerBasicImpl(P12_PATH, PASSWORD, productModel));
				List<PushedNotification> notifications = new ArrayList<PushedNotification>();
				Device device = new BasicDevice();
				device.setToken(token);
				PushedNotification notification = pushManager.sendNotification(device, payLoad, true);
				notifications.add(notification);
				List<PushedNotification> failedNotifications = PushedNotification.findFailedNotifications(notifications);
				List<PushedNotification> successfulNotifications = PushedNotification.findSuccessfulNotifications(notifications);
				int failed = failedNotifications.size();
				int successful = successfulNotifications.size();
				if (successful > 0 && failed == 0) {
					System.out.println("success count \t: " + successfulNotifications.size());
				} else if (successful == 0 && failed > 0) {
					System.out.println("failed  count \t: " + failedNotifications.size());
				} else if (successful == 0 && failed == 0) {
					System.out.println("No notifications could be sent, probably because of a critical error");
				} else {
					System.out.println("success count \t: " + successfulNotifications.size());
					System.out.println("failed  count \t: " + failedNotifications.size());
				}
				pushManager.stopConnection();
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getLocalizedMessage(), e);
			}
		}
		
	}
}
