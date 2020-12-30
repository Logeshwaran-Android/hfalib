/*    */ package io.socket.global;
/*    */ 
/*    */

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/*    */
/*    */

/*    */
/*    */ 
/*    */ public class Global
/*    */ {
/*    */   public static String encodeURIComponent(String str)
/*    */   {
/*    */     try
/*    */     {
/* 14 */       return 
/*    */       
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 20 */         URLEncoder.encode(str, "UTF-8").replace("+", "%20").replace("%21", "!").replace("%27", "'").replace("%28", "(").replace("%29", ")").replace("%7E", "~");
/*    */     } catch (UnsupportedEncodingException e) {
/* 22 */       throw new RuntimeException(e);
/*    */     }
/*    */   }
/*    */   
/*    */   public static String decodeURIComponent(String str) {
/*    */     try {
/* 28 */       return URLDecoder.decode(str, "UTF-8");
/*    */     } catch (UnsupportedEncodingException e) {
/* 30 */       throw new RuntimeException(e);
/*    */     }
/*    */   }
/*    */ }

