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
import org.jivesoftware.openfire.plugin.util.SysTime;
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
        System.out.println("1--PushNotificationServlet init()");
        plugin = (NotificationPlugin) XMPPServer.getInstance().getPluginManager().getPlugin("notification");
        AuthCheckFilter.addExclude(SERVICE_NAME);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String toJID = req.getParameter("toJID");
		String subject = req.getParameter("subject");
		String content = req.getParameter("content");
		new PushThread(toJID, subject, content).start();
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
        doGet(req,resp);
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
        System.out.println("1--PushNotificationServlet destroy()");
        AuthCheckFilter.removeExclude(SERVICE_NAME);
    }
	public int getUserCount(String where) {
		SysTime.PrintCurrentTime(TAG, "getUserCount() begin");
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
		SysTime.PrintCurrentTime(TAG, "getUserCount() end");
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
			SysTime.PrintCurrentTime(TAG, "run() begin");
			if (!toJid.equals("-1")) {
                 SysTime.PrintCurrentTime(TAG,"===toJid!=-1====");
				XMPPServer.getInstance().getSessionManager()
		           .sendServerMessage(new JID(toJid+ "@" + domain), subject, content);
			} else {
				int limit = 50;
				int count = getUserCount("");
				int cycleCount = count / limit + (count % limit == 0 ? 0 : 1);
				if(count/limit == 0){
					SysTime.PrintCurrentTime(TAG," count/limit == 0 ");
					push2User(toJid, subject, content, 0, count);
				}else{
					for (int i = 0; i < cycleCount; i++) {
						push2User(toJid, subject, content, i * limit, limit);
					}
				}
			}
			SysTime.PrintCurrentTime(TAG, "run() end ");
		}
	}
/*	private void routePacket(String toJid, String subject,
			String content) {
		Message message = new Message();

		message.setTo(toJid + "@" +"172.16.230.6");
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
	}*/

	private Collection<String> push2User(String toJid,
			String subject, String content, int startIndex, int numResults) {
		SysTime.PrintCurrentTime(TAG, "push2User() begin");
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
			SysTime.PrintCurrentTime(TAG, "push2User() begin 1");
			if ((startIndex == 0) && (numResults == Integer.MAX_VALUE)) {

				pstmt = con.prepareStatement(sql);
				// Set the fetch size. This will prevent some JDBC drivers from
				// trying
				// to load the entire result set into memory.
				DbConnectionManager.setFetchSize(pstmt, 500);
				SysTime.PrintCurrentTime(TAG, "push2User() begin 2");
				rs = pstmt.executeQuery();
				while (rs.next()) {

					//SysTime.PrintCurrentTime(TAG, "push2User() begin 2-1");
					XMPPServer.getInstance().getSessionManager()
					           .sendServerMessage(new JID(rs.getString(1)+ "@" + domain), subject, content);
				}
			} else {
				SysTime.PrintCurrentTime(TAG, "push2User() begin 4");
				pstmt = DbConnectionManager.createScrollablePreparedStatement(
						con, sql);
				DbConnectionManager.limitRowsAndFetchSize(pstmt, startIndex,
						numResults);
				rs = pstmt.executeQuery();
				DbConnectionManager.scrollResultSet(rs, startIndex);
				int count = 0;
				while (rs.next() && count < numResults) {
					//SysTime.PrintCurrentTime(TAG, "push2User() begin 4-1");
					SysTime.PrintCurrentTime(TAG, "push2User() :"+rs.getString(1));
					XMPPServer.getInstance().getSessionManager()
			           .sendServerMessage(new JID(rs.getString(1)+ "@" + domain), subject, content);
					//SysTime.PrintCurrentTime(TAG, "push2User() begin 4-2");
				}
			}
		} catch (SQLException e) {
			Log.error(e.getMessage(), e);
		} finally {
			DbConnectionManager.closeConnection(rs, pstmt, con);
		}
		SysTime.PrintCurrentTime(TAG, "push2User() end");
		return usernames;
	}
}
