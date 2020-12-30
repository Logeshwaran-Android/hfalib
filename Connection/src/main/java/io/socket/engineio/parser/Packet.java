/*    */ package io.socket.engineio.parser;
/*    */ 
/*    */ 
/*    */ public class Packet<T>
/*    */ {
/*    */   public static final String OPEN = "open";
/*    */   
/*    */   public static final String CLOSE = "close";
/*    */   public static final String PING = "ping";
/*    */   public static final String PONG = "pong";
/*    */   public static final String UPGRADE = "upgrade";
/*    */   public static final String MESSAGE = "message";
/*    */   public static final String NOOP = "noop";
/*    */   public static final String ERROR = "error";
/*    */   public String type;
/*    */   public T data;
/*    */   
/*    */   public Packet(String type)
/*    */   {
/* 20 */     this(type, null);
/*    */   }
/*    */   
/*    */   public Packet(String type, T data) {
/* 24 */     this.type = type;
/* 25 */     this.data = data;
/*    */   }
/*    */ }


