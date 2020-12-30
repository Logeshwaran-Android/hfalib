/*    */ package io.socket.engineio.client;
/*    */ 
/*    */ public class EngineIOException
/*    */   extends Exception
/*    */ {
/*    */   public String transport;
/*    */   public Object code;
/*    */   
/*    */   public EngineIOException() {}
/*    */   
/*    */   public EngineIOException(String message)
/*    */   {
/* 13 */     super(message);
/*    */   }
/*    */   
/*    */   public EngineIOException(String message, Throwable cause) {
/* 17 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public EngineIOException(Throwable cause) {
/* 21 */     super(cause);
/*    */   }
/*    */ }


