package com.authorwjf;
 
import android.hardware.Sensor;

import com.authorwjf.NanoHTTPD;
import com.authorwjf.ServerRunner;
 
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.conn.util.InetAddressUtils;




public class DebugServer extends NanoHTTPD {
	
	//static keyword means all instances of the same class share the same variable
	private static float my_x = 0;
	private static float my_y = 0;
	private static float my_z = 0;
	private static float my_x1 = 0;
	private static float my_y1 = 0;
	private static float my_z1 = 0;
	private static float my_x2 = 0;
	private static float my_y2 = 0;
	private static float my_z2 = 0;
	private static float my_x3 = 0;
	private static float my_y3 = 0;
	private static float my_z3 = 0;
	private static String StrType="Waiting for Type value ";
	private static String my_status = "Waiting for status value  ";
	
	static SimpleDateFormat sdf = new SimpleDateFormat("MMMMMMMM dd, yyyy  'at' HH:mm:ss z");
	private static String startDateandTime = sdf.format(new Date());
	private static String currentDateandTime = sdf.format(new Date());
	
	private static String my_ipv4 = getIPAddress(true);
	private static String my_ipv6 = getIPAddress(false);
	
	
    public DebugServer() {
        super(8080);
    }
    
    public static void changeValues(float x, float y, float z, String status, int Type)
    {

    	if(Type==Sensor.TYPE_ACCELEROMETER){
        	my_x = x;
        	my_y = y;
        	my_z = z;
        	currentDateandTime = sdf.format(new Date());
    		}
    	if(Type==Sensor.TYPE_GYROSCOPE){
        	my_x1 = x;
        	my_y1 = y;
        	my_z1 = z;
        	currentDateandTime = sdf.format(new Date());
    		}
    	if(Type==Sensor.TYPE_GRAVITY){
        	my_x2 = x;
        	my_y2 = y;
        	my_z2 = z;
            my_status = status; 
        	currentDateandTime = sdf.format(new Date());
    		}
    	if(Type==Sensor.TYPE_ROTATION_VECTOR){
        	my_x3 = x;
        	my_y3 = y;
        	my_z3 = z;
        	currentDateandTime = sdf.format(new Date());
    		}
    }
 
    public static void main(String[] args) {
        ServerRunner.run(DebugServer.class);
    }
 
    @Override public Response serve(IHTTPSession session) {
        Map<String, List<String>> decodedQueryParameters =
            decodeParameters(session.getQueryParameterString());
 
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head><title>Accelerometer Data Server</title></head>");
        sb.append("<body>");
        sb.append("<h1>Accelerometer Data Server</h1>");
 
        sb.append("<p><blockquote><b>URI</b> = ").append(
            String.valueOf(session.getUri())).append("<br />");
 
        sb.append("<b>Method</b> = ").append(
            String.valueOf(session.getMethod())).append("</blockquote></p>")
            .append("<b>______________________________________________  </b>").append("</blockquote></p>");
        
        sb.append("<h3>Latest  Information</h3>   ")
                                                         .append("<b>   Accelerometer: </blockquote></p>")
                                                         .append("<b>   X-axis</b> = ").append(my_x).append("</blockquote></p>")
                                                         .append("<b>   Y-axis</b> = ").append(my_y).append("</blockquote></p>")
                                                         .append("<b>   Z-axis</b> = ").append(my_z).append("</blockquote></p>")
                                                         .append("<b>   Gyroscope: </blockquote></p>")
                                                         .append("<b>   X-axis</b> = ").append(my_x1).append("</blockquote></p>")
                                                         .append("<b>   Y-axis</b> = ").append(my_y1).append("</blockquote></p>")
                                                         .append("<b>   Z-axis</b> = ").append(my_z1).append("</blockquote></p>")
                                                         .append("<b>   Gravity: </blockquote></p>")
                                                         .append("<b>   X-axis</b> = ").append(my_x2).append("</blockquote></p>")
                                                         .append("<b>   Y-axis</b> = ").append(my_y2).append("</blockquote></p>")
                                                         .append("<b>   Z-axis</b> = ").append(my_z2).append("</blockquote></p>")
                                                         .append("<b>   Rotation: </blockquote></p>")
                                                         .append("<b>   X-axis</b> = ").append(my_x3).append("</blockquote></p>")
                                                         .append("<b>   Y-axis</b> = ").append(my_y3).append("</blockquote></p>")
                                                         .append("<b>   Z-axis</b> = ").append(my_z3).append("</blockquote></p>")

                                                         .append("<b>   Significant Direction</b> = ").append(my_status).append("</blockquote></p>")
                                                         .append("<b>   Timestamp</b> = ").append(currentDateandTime).append("</blockquote></p>")
                                                         .append(" <b>______________________________________________ </b> ").append("</blockquote></p>")
                                                         .append("Server started ").append(startDateandTime).append("</blockquote></p>")                                             
                                                         .append("<b>______________________________________________  </b>").append("</blockquote></p>").append("   ").append("</blockquote></p>")
                                                         ;
 
        sb.append("<h3>Server IP Addresses</h3>   ").append("<b>   IPv4</b> = ").append(my_ipv4).append("</blockquote></p>")
        .append("<b>   IPv6</b> = ").append(my_ipv6).append("</blockquote></p>")
        .append("<b>______________________________________________  </b>").append("</blockquote></p>").append("   ").append("</blockquote></p>")
                                                         ;

        
        sb.append("<h3>Headers</h3><p><blockquote>").
            append(toString(session.getHeaders())).append("</blockquote></p>");
 
        sb.append("<h3>Parms</h3><p><blockquote>").
            append(toString(session.getParms())).append("</blockquote></p>");
 
        sb.append("<h3>Parms (multi values?)</h3><p><blockquote>").
            append(toString(decodedQueryParameters)).append("</blockquote></p>");
 
        try {
            Map<String, String> files = new HashMap<String, String>();
            session.parseBody(files);
            sb.append("<h3>Files</h3><p><blockquote>").
                append(toString(files)).append("</blockquote></p>");
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        sb.append("</body>");
        sb.append("</html>");
        return new Response(sb.toString());
    }
 
    private String toString(Map<String, ? extends Object> map) {
        if (map.size() == 0) {
            return "";
        }
        return unsortedList(map);
    }
 
    private String unsortedList(Map<String, ? extends Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul>");
        for (Map.Entry entry : map.entrySet()) {
            listItem(sb, entry);
        }
        sb.append("</ul>");
        return sb.toString();
    }
 
    private void listItem(StringBuilder sb, Map.Entry entry) {
        sb.append("<li><code><b>").append(entry.getKey()).
            append("</b> = ").append(entry.getValue()).append("</code></li>");
    }
    
    
    /**
     * Get IP address from first non-localhost interface
     * @param ipv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
                        if (useIPv4) {
                            if (isIPv4) 
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim<0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
    
    
}