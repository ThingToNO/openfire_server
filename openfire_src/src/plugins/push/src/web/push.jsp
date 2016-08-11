<%--
  -	$Revision: $
  -	$Date: $
  -
  - Copyright (C) 2005-2008 Jive Software. All rights reserved.
  -
  - Licensed under the Apache License, Version 2.0 (the "License");
  - you may not use this file except in compliance with the License.
  - You may obtain a copy of the License at
  -
  -     http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  - See the License for the specific language governing permissions and
  - limitations under the License.

--%>
<%@ page language="java" pageEncoding="utf-8"%>
<%@page import="java.sql.Connection"%>
<%@page import="org.jivesoftware.database.DbConnectionManager"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<html>
	<head>
		<title><fmt:message key="jn.settings.title" /></title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta name="pageID" content="jingle-nodes" />
	</head>
	<body>
		<%
		List<String> users = new ArrayList<String>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement("select username from ofusers");
			rs = pstmt.executeQuery();
			while (rs.next()) {
				users.add(rs.getString("username"));
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

		<div class="jive-table">
			<form action="subm" method="post">
				<table>
					<tr>
						<td style="width: 125px" align="right">
							From
						</td>
						<td>
							<select name="fromJID" style="padding:2px 4px">
							    <option value="-1">
									All Users
								</option>
								<%
									for (String _users : users) {
								%>
								<option value="<%=_users%>"><%=users%></option>
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
						<td colspan="2" style="width: 125px" align="center">
							<input type="submit" value="  Push  " />
						</td>
					</tr>
				</table>
			</form>
		</div>
	</body>
</html>
