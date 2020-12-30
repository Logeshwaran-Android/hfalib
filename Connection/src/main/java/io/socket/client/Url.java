/*    */ package io.socket.client;
/*    */ 
/*    */

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.regex.Pattern;

/*    */
/*    */
/*    */
/*    */

/*    */
/*    */ public class Url
/*    */ {
/* 11 */   private static Pattern PATTERN_HTTP = Pattern.compile("^http|ws$");
/* 12 */   private static Pattern PATTERN_HTTPS = Pattern.compile("^(http|ws)s$");
/*    */   
/*    */   public static URL parse(String uri)
/*    */     throws java.net.URISyntaxException
/*    */   {
/* 17 */     return parse(new URI(uri));
/*    */   }
/*    */   
/*    */   public static URL parse(URI uri) {
/* 21 */     String protocol = uri.getScheme();
/* 22 */     if ((protocol == null) || (!protocol.matches("^https?|wss?$"))) {
/* 23 */       protocol = "https";
/*    */     }
/*    */     
/* 26 */     int port = uri.getPort();
/* 27 */     if (port == -1) {
/* 28 */       if (PATTERN_HTTP.matcher(protocol).matches()) {
/* 29 */         port = 80;
/* 30 */       } else if (PATTERN_HTTPS.matcher(protocol).matches()) {
/* 31 */         port = 443;
/*    */       }
/*    */     }
/*    */     
/* 35 */     String path = uri.getRawPath();
/* 36 */     if ((path == null) || (path.length() == 0)) {
/* 37 */       path = "/";
/*    */     }
/*    */     
/* 40 */     String userInfo = uri.getRawUserInfo();
/* 41 */     String query = uri.getRawQuery();
/* 42 */     String fragment = uri.getRawFragment();
/*    */     try {
/* 44 */       return new URL(protocol + "://" + (userInfo != null ? userInfo + "@" : "") + uri
/*    */       
/* 46 */         .getHost() + (port != -1 ? ":" + port : "") + path + (query != null ? "?" + query : "") + (fragment != null ? "#" + fragment : ""));
/*    */ 
/*    */     }
/*    */     catch (MalformedURLException e)
/*    */     {
/*    */ 
/* 52 */       throw new RuntimeException(e);
/*    */     }
/*    */   }
/*    */   
/*    */   public static String extractId(String url) throws MalformedURLException {
/* 57 */     return extractId(new URL(url));
/*    */   }
/*    */   
/*    */   public static String extractId(URL url) {
/* 61 */     String protocol = url.getProtocol();
/* 62 */     int port = url.getPort();
/* 63 */     if (port == -1) {
/* 64 */       if (PATTERN_HTTP.matcher(protocol).matches()) {
/* 65 */         port = 80;
/* 66 */       } else if (PATTERN_HTTPS.matcher(protocol).matches()) {
/* 67 */         port = 443;
/*    */       }
/*    */     }
/* 70 */     return protocol + "://" + url.getHost() + ":" + port;
/*    */   }
/*    */ }


