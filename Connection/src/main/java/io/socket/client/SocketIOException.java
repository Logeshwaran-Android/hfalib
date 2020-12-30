/*    */ package io.socket.client;
/*    */ 
/*    */ public class SocketIOException
/*    */   extends Exception
/*    */ {
/*    */   public SocketIOException() {}
/*    */   
/*    */   public SocketIOException(String message)
/*    */   {
/* 10 */     super(message);
/*    */   }
/*    */   
/*    */   public SocketIOException(String message, Throwable cause) {
/* 14 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public SocketIOException(Throwable cause) {
/* 18 */     super(cause);
/*    */   }
/*    */ }

