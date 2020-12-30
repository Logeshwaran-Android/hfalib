/*    */ package io.socket.parser;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract interface Parser
/*    */ {
/*    */   public static final int CONNECT = 0;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int DISCONNECT = 1;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int EVENT = 2;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int ACK = 3;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int ERROR = 4;
/*    */   
/*    */ 
/*    */ 
/*    */   public static final int BINARY_EVENT = 5;
/*    */   
/*    */ 
/*    */ 
/*    */   public static final int BINARY_ACK = 6;
/*    */   
/*    */ 
/*    */ 
/*    */   public static final int protocol = 4;
/*    */   
/*    */ 
/*    */ 
/* 45 */   public static final String[] types = { "CONNECT", "DISCONNECT", "EVENT", "ACK", "ERROR", "BINARY_EVENT", "BINARY_ACK" };
/*    */   
/*    */   public static abstract interface Decoder
/*    */   {
/*    */     public abstract void add(String paramString);
/*    */     
/*    */     public abstract void add(byte[] paramArrayOfByte);
/*    */     
/*    */     public abstract void destroy();
/*    */     
/*    */     public abstract void onDecoded(Callback paramCallback);
/*    */     
/*    */     public static abstract interface Callback
/*    */     {
/*    */       public abstract void call(Packet paramPacket);
/*    */     }
/*    */   }
/*    */   
/*    */   public static abstract interface Encoder
/*    */   {
/*    */     public abstract void encode(Packet paramPacket, Callback paramCallback);
/*    */     
/*    */     public static abstract interface Callback
/*    */     {
/*    */       public abstract void call(Object[] paramArrayOfObject);
/*    */     }
/*    */   }
/*    */ }

