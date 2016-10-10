package org.jivesoftware.openfire.plugin.helloworld;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.jivesoftware.admin.AuthCheckFilter;
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
public class HelloWorldPlugin implements Plugin{



    private SessionManager sessionManager;

    public HelloWorldPlugin() {
        sessionManager = SessionManager.getInstance();
    }

    @Override
    public void initializePlugin(PluginManager manager, File pluginDirectory) {
        System.out.println("=========init HelloWorldPlugin=========");
    
    }

    @Override
    public void destroyPlugin() {
    	System.out.println("HelloWorldPlugin:destroyPlugin!");

    }
}
