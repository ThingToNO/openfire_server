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

		<div class="jive-table">
			<form action="subm" method="post">
				<table>
					<tr>
						<td style="width: 125px" align="right">
							From
						</td>
						<td>
							<input style="width:260px" type="text" name="fromJID" />
							<input type=hidden name="p2p" value="p2p" />
						</td>
					</tr>
					<tr>
						<td style="width: 125px" align="right">
							To
						</td>
						<td>
							<input style="width:260px" type="text" name="toJID" />
						</td>
					</tr>
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
