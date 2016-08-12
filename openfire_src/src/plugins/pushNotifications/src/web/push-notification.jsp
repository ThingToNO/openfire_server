<%--
--push notification jsp.
--2014.5.5
--zhoulq
--%>

<%@ page import="org.jivesoftware.util.ParamUtils,
                 org.jivesoftware.openfire.XMPPServer,
                 org.jivesoftware.openfire.user.User,
                 org.xmpp.packet.Message,
                 org.xmpp.packet.JID,
                 java.net.URLEncoder,
                 java.lang.String,
                 org.jivesoftware.openfire.plugin.NotificationPlugin"
    errorPage="error.jsp"
%>

<%
    NotificationPlugin plugin = (NotificationPlugin)XMPPServer.getInstance().getPluginManager().getPlugin("notification");
    // Get parameters
    boolean send = ParamUtils.getBooleanParameter(request,"send");
    boolean tabs = ParamUtils.getBooleanParameter(request,"tabs",true);
    String broadcast = ParamUtils.getParameter(request,"broadcast");
    String username = ParamUtils.getParameter(request,"username");
    String title = ParamUtils.getParameter(request,"title");
    String message = ParamUtils.getParameter(request,"message");
%>

<jsp:useBean id="webManager" class="org.jivesoftware.util.WebManager"  />
<% webManager.init(pageContext); %>

<%
    // Handle a cancel
    if (request.getParameter("cancel") != null) {
        if (username == null) {
            response.sendRedirect("session-summary.jsp");
            return;
        }
        else {
            response.sendRedirect("user-properties.jsp?username=" + URLEncoder.encode(username, "UTF-8"));
            return;
        }
    }

    // Get the user - a user might not be passed in if this is a system-wide message
    User user = null;
    if (username != null) {
        user = webManager.getUserManager().getUser(username);
    }

    if (send) {
        // new a notification
        if(broadcast.equals("Y")){
            plugin.sendNotificationToAllUser(title, message, "");
                    System.out.println("JSP send a notification...to all...");
        }else{
            plugin.sendNotificationToSignal(username, title, message, "");
                    System.out.println("JSP send a notification...to " + username + "...");
        }
        System.out.println("JSP send a notification...");
    }
%>


<html>
<head>
<title><fmt:message key="user.message.title"/></title>
<meta name="pageID" content="push-notification"/>
<meta name="helpPage" content="send_a_notification_to_users.html"/>
</head>
<body>

    <h1>Push Notification</h1>


    <form action="push-notification.jsp" method="post" name="f">

    <% if(username != null){ %>
    <input type="hidden" name="username" value="<%= username %>">
    <% } %>
    <input type="hidden" name="tabs" value="<%= tabs %>">

    <input type="hidden" name="send" value="true">

        <!-- BEGIN send message block -->

        <div class="jive-contentBox" style="-moz-border-radius: 3px;">
		    <table cellpadding="3" cellspacing="1" border="0" width="600">

		        <tr><td colspan=3 class="text" style="padding-bottom: 10px;">

			        <p>Use the form below to send an push notification to all users.</p>

		        </td></tr>

		        <tr>
                <td class="jive-label" width="20%">To:</td>
                	<td width="80%">
                		<input type="radio" name="broadcast" value="Y" checked="checked" />  All
                        <input type="radio" name="broadcast" value="N" /> Single Device
                	</td>
                </tr>

                <tr class="jive-label" id="trUsername" style="display:none;">
                	<td>Username:</td>
                	<td><input type="text" id="username" name="username" value="" /></td>
                </tr>

		        <tr>
   			    <td class="jive-label">
   			    Title:
                </td>
                <td>
                <input type="text" id="title" name="title" value="Open-fire Notification"/>
                </td>
		        <tr valign="top">
			    <td class="jive-label">
				Message:
			    </td>
			    <td>
				<textarea name="message" cols="55" rows="5" wrap="virtual">This message is a test notification, sended by notification plugin on Open-fire, Thanks!</textarea>
			    </td>
		        </tr>
		    </table>
	    </div>
	    <!-- END send message block -->

        <input type="submit" value="PUSH">
        <input type="submit" name="CANCEL" value="CANCEL">
    </form>

    <script type="text/javascript">
    //<![CDATA[

    $(function() {
    	$('input[name=broadcast]').click(function() {
    		if ($('input[name=broadcast]')[0].checked) {
    			$('#trUsername').hide();
    		} else {
    			$('#trUsername').show();
    		}
    	});

    	if ($('input[name=broadcast]')[0].checked) {
    		$('#trUsername').hide();
    	} else {
    		$('#trUsername').show();
    	}
    });

    //]]>
    </script>

</body>
</html>