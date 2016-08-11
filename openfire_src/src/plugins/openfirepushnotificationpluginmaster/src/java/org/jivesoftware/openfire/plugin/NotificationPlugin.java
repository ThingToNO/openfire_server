package org.jivesoftware.openfire.plugin;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.session.LocalClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.component.Component;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.component.ComponentManagerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

import java.io.File;
import java.util.Iterator;
import java.util.Random;
/**
 * Notification plugin
 *
 * Created by zhoulq on 5/6/14.
 *
 * @author zhouliqiang
 *
 * doctly2010@gmail.com
 *
 */
public class NotificationPlugin implements Plugin{

    private static final Logger Log = LoggerFactory.getLogger(NotificationPlugin.class);
    private static final String NOTIFICATION_NAMESPACE = "androidpn:iq:notification";

    private SessionManager sessionManager;

    public NotificationPlugin() {
        sessionManager = SessionManager.getInstance();
    }

    @Override
    public void initializePlugin(PluginManager manager, File pluginDirectory) {
        Log.info("initializePlugin()");
    }

    /**
     * send notification to all users
     *
     * */
    public void sendNotificationToAllUser(String title, String message, String uri){
        Log.info("sendNotificationToAllUser.......title = " + title);
        IQ notificationIQ = createNotificationIQ("1234567890", title, message, uri);
        notificationIQ.setFrom("admin@zhoulq-fedora");
        Log.info("sendNotificationToAllUser...getIQRouter[].......route[]......");
        int sessionNumber = sessionManager.getSessions().size();
        Log.info("sendNotificationToAllUser.....get session number = " + sessionNumber);
        Iterator iterator = sessionManager.getSessions().iterator();
        while (iterator.hasNext()){
            Log.info("sendNotificationToAllUser......iterator.hasNext()");
            LocalClientSession localClientSession = (LocalClientSession) iterator.next();
            if(localClientSession.getPresence().isAvailable()){
                notificationIQ.setTo(localClientSession.getAddress());
                Log.info("sendNotificationToAllUser......get local session address");
                try {
                    localClientSession.deliver(notificationIQ);
                    Log.info("sendNotificationToAllUser......deliver");
                } catch (UnauthorizedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * send notification to a signal
     * */
    public void sendNotificationToSignal(String userName, String title, String message, String uri){
        Log.info("sendNotificationToSignal.......title = " + title);
        IQ notificationIQ = createNotificationIQ("1234567890", title, message, uri);
        notificationIQ.setFrom("admin@zhoulq-fedora");
        JID userJID = new JID(userName);
        LocalClientSession localClientSession = (LocalClientSession) sessionManager.getSession(userJID);
        if(localClientSession.getPresence().isAvailable()){
            notificationIQ.setTo(localClientSession.getAddress());
            Log.info("sendNotificationToSignal......get local session address");
            try {
                localClientSession.deliver(notificationIQ);
                Log.info("sendNotificationToSignal......deliver");
            } catch (UnauthorizedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Creates a new notification IQ and returns it.
     */
    private IQ createNotificationIQ(String apiKey, String title,
                                    String message, String uri) {
        Log.info("createNotificationIQ()............ title = " + title);
        Random random = new Random();
        String id = Integer.toHexString(random.nextInt());

        Element notification = DocumentHelper.createElement(QName.get(
                "notification", NOTIFICATION_NAMESPACE));
        notification.addElement("id").setText(id);
        notification.addElement("apiKey").setText(apiKey);
        notification.addElement("title").setText(title);
        notification.addElement("message").setText(message);
        notification.addElement("uri").setText(uri);

        IQ iq = new IQ();
        iq.setType(IQ.Type.set);
        iq.setChildElement(notification);

        return iq;
    }

    @Override
    public void destroyPlugin() {
        Log.info("destroyPlugin()");

    }
}
