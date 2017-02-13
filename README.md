# openfire_server

--添加了pushNotifications 插件，用来发送推送消息
--Android client: https://github.com/whtchl/XMPPAndroidSmackSource.git

  Server :https://github.com/whtchl/openfire_server.git



<img src="https://raw.githubusercontent.com/whtchl/openfire_server/master/art/pushmessage.png"/>


调用接口：http://127.0.0.1:9090/plugins/pushMessage/pushnotification?toJID=709&subject=11&content=tchl


=================================

本文用的是mysql数据库，在运行程序之前先部署mysql，接着创建openfire数据库。sql路径在openfire_server\openfire_src\src\database\openfire_mysql.sql


===================================
编译：
openfire_server\openfire_src\build\build.xml

全源码编译 ant 双击：openfire(default)

编译所以的plugins： ant 双击plugins

编译单个plugin： 在 openfire_server\openfire_src\build\build.properties 添加plugin=pushMessage。 即添加plugin=名字插件名字

                  deploy.jar.dir=
		  
                #plugin=openfirepushnotificationpluginmaster
		
               plugin=pushMessage
	       


===============================

打包发布：详见blog
http://blog.csdn.net/michael1112/article/details/52534413



===================================

在下面这个java中修改服务器ip地址。
RoutingTableImpl.java   

@Override

    public void routePacket(JID jid, Packet packet, boolean fromServer) throws PacketException {
    
        boolean routed = false;
	
        try {
	
        	System.out.println("serverName:"+serverName+"  jid.getDomain:"+jid.getDomain());
		
	        if (serverName.equals(jid.getDomain()) ||
		
	        		jid.getDomain().equals("172.16.1.120")) {



改为服务器ip


==============================
添加admin的domain：将admin的message不写到ofoffline 表中。
MessageRouter.java 
   public void saveMessage(JID recipient, Packet packet){
   
        log.debug( "Message sent to unreachable address: " + packet.toXML() );
	
        final Message msg = (Message) packet;
	
        String from = msg.getFrom().toString();
	
        if(msg != null && msg.getTo().toString().equals("admin@win-6rloun8a6fq")){
	


=======================
