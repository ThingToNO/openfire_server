<%@ page import="org.jivesoftware.util.ParamUtils,
                 org.jivesoftware.openfire.XMPPServer,
                 org.jivesoftware.openfire.user.User,
                 org.xmpp.packet.Message,
                 org.xmpp.packet.JID,
                 java.net.URLEncoder,
                 java.lang.String"
    errorPage="error.jsp"
%>
<%@page import="java.sql.Connection"%>
<%@page import="org.jivesoftware.database.DbConnectionManager"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title><fmt:message key="user.message.title"/></title>
<meta name="pageID" content="push-notification"/>
</head>
<body>
		<%
		List<String> helpers = new ArrayList<String>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement("select username from ofuser");
			rs = pstmt.executeQuery();
			while (rs.next()) {
				helpers.add(rs.getString("username"));
			}
		} catch (SQLException e) {
			out.print("" + e.getLocalizedMessage());
		} finally {
			DbConnectionManager.closeConnection(rs, pstmt, con);
		}

/* 		Connection con1 = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs1 = null;
		List<Map<String, String>> companys = new ArrayList<Map<String, String>>();
		try {
			con1 = DbConnectionManager.getConnection();
			pstmt1 = con1
					.prepareStatement("select uuid,name from buddyCompany");
			rs1 = pstmt1.executeQuery();
			while (rs1.next()) {
				Map<String, String> _map = new HashMap<String, String>();
				_map.put("name", rs1.getString("name"));
				_map.put("uuid", rs1.getString("uuid"));
				companys.add(_map);
			}
		} catch (SQLException e) {
			out.print("" + e.getLocalizedMessage());
		} finally {
			DbConnectionManager.closeConnection(rs1, pstmt1, con1);
		} */
	%>



     <h1>PushNotification</h1>

		<div class="jive-table">
			<form action="pushnotification" method="post">
				<table>
					<tr>
						<td style="width: 125px" align="right">
							To
						</td>
						<td>
							<select name="toJID" style="padding:2px 4px">
								<option value="-1">
									All Users
								</option>
								<%
									for (String _helper : helpers) {
								%>
								<option value="<%=_helper%>"><%=_helper%></option>
								<%
									}
								%>
							</select>
						</td>
					</tr>
<%-- 					<tr>
						<td style="width: 125px" align="right">
							To
						</td>
						<td>
							<select name="toJID" style="padding:2px 4px">
								<option value="-1">
									All Users
								</option>
								<%
									for (Map<String, String> _company : companys) {
								%>
								<option value="<%=_company.get("uuid")%>"><%=_company.get("name")%></option>
								<%
									}
								%>
							</select>
						</td>
					</tr> --%>
					<tr>
						<td style="width: 125px" align="right">
							Subject
						</td>
						<td>
							<input style="width:260px" type="text" name="subject" />
						</td>
					</tr>
					<tr>
						<td valign="top" style="width: 125px" align="right">
							Content
						</td>
						<td>
							<textarea style="width:560px;height:80px" name="content"></textarea>
						</td>
					</tr>

					<tr>
						<td colspan="2" style="width: 125px" align="left">
							<input type="submit" value="  Push  " />
						</td>
					</tr>
				</table>
			</form>
		</div>
</body>
</html>