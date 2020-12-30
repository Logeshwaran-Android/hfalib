/*    */ package io.socket.parseqs;
/*    */ 
/*    */

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.socket.global.Global;

/*    */
/*    */
/*    */

/*    */
/*    */ 
/*    */ 
/*    */ public class ParseQS
/*    */ {
/*    */   public static String encode(Map<String, String> obj)
/*    */   {
/* 14 */     StringBuilder str = new StringBuilder();
/* 15 */     for (Entry<String, String> entry : obj.entrySet()) {
/* 16 */       if (str.length() > 0) { str.append("&");
/*    */       }
/* 18 */       str.append(Global.encodeURIComponent((String)entry.getKey())).append("=").append(Global.encodeURIComponent((String)entry.getValue()));
/*    */     }
/* 20 */     return str.toString();
/*    */   }
/*    */   
/*    */   public static Map<String, String> decode(String qs) {
/* 24 */     Map<String, String> qry = new HashMap();
/* 25 */     String[] pairs = qs.split("&");
/* 26 */     for (String _pair : pairs) {
/* 27 */       String[] pair = _pair.split("=");
/* 28 */       qry.put(Global.decodeURIComponent(pair[0]), pair.length > 1 ?
/* 29 */         Global.decodeURIComponent(pair[1]) : "");
/*    */     }
/* 31 */     return qry;
/*    */   }
/*    */ }

