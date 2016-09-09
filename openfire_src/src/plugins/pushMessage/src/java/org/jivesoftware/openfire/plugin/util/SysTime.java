package org.jivesoftware.openfire.plugin.util;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SysTime {

	public static void PrintCurrentTime(String TAG,String info){
		System.out.println(TAG+"----"+getCurrentTime()+"----"+info);
	}
    private static String getCurrentTime() {  
        String returnStr = null;  
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        Date date = new Date();  
        returnStr = f.format(date);  
        return returnStr;  
    }  
	
}
