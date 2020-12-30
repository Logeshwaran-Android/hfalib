/*    */ package io.socket.parser;
/*    */ 
/*    */ 
/*    */ public class Packet<T>
/*    */ {
/*  6 */   public int type = -1;
/*  7 */   public int id = -1;
/*    */   public String nsp;
/*    */   public T data;
/*    */   public int attachments;
/*    */   public String query;
/*    */   
/*    */   public Packet() {}
/*    */   
/*    */   public Packet(int type) {
/* 16 */     this.type = type;
/*    */   }
/*    */   
/*    */   public Packet(int type, T data) {
/* 20 */     this.type = type;
/* 21 */     this.data = data;
/*    */   }
/*    */ }
