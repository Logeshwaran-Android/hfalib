/*    */ package io.socket.utf8;
/*    */ 
/*    */ import java.io.IOException;

/*    */
/*    */ public class UTF8Exception
/*    */   extends IOException
/*    */ {
/*    */   public UTF8Exception() {}
/*    */   
/*    */   public UTF8Exception(String message)
/*    */   {
/* 12 */     super(message);
/*    */   }
/*    */   
/*    */   public UTF8Exception(String message, Throwable cause) {
/* 16 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public UTF8Exception(Throwable cause) {
/* 20 */     super(cause);
/*    */   }
/*    */ }

