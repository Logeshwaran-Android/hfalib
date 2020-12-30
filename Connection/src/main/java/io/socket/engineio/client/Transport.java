/*     */ package io.socket.engineio.client;
/*     */ 
/*     */ import io.socket.engineio.parser.Packet;

/*     */
/*     */ public abstract class Transport extends io.socket.emitter.Emitter {
/*     */   public static final String EVENT_OPEN = "open";
/*     */   public static final String EVENT_CLOSE = "close";
/*     */   public static final String EVENT_PACKET = "packet";
/*     */   public static final String EVENT_DRAIN = "drain";
/*     */   public static final String EVENT_ERROR = "error";
/*     */   public static final String EVENT_REQUEST_HEADERS = "requestHeaders";
/*     */   public static final String EVENT_RESPONSE_HEADERS = "responseHeaders";
/*     */   public boolean writable;
/*     */   public String name;
/*     */   public java.util.Map<String, String> query;
/*     */   
/*  17 */   protected static enum ReadyState { OPENING,  OPEN,  CLOSED,  PAUSED;
/*     */     
/*     */     private ReadyState() {}
/*     */     
/*  21 */     public String toString() { return super.toString().toLowerCase(); }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean secure;
/*     */   
/*     */ 
/*     */   protected boolean timestampRequests;
/*     */   
/*     */   protected int port;
/*     */   
/*     */   protected String path;
/*     */   
/*     */   protected String hostname;
/*     */   
/*     */   protected String timestampParam;
/*     */   
/*     */   protected Socket socket;
/*     */   
/*     */   protected ReadyState readyState;
/*     */   
/*     */   protected okhttp3.WebSocket.Factory webSocketFactory;
/*     */   
/*     */   protected okhttp3.Call.Factory callFactory;
/*     */   
/*     */   public Transport(Options opts)
/*     */   {
/*  49 */     this.path = opts.path;
/*  50 */     this.hostname = opts.hostname;
/*  51 */     this.port = opts.port;
/*  52 */     this.secure = opts.secure;
/*  53 */     this.query = opts.query;
/*  54 */     this.timestampParam = opts.timestampParam;
/*  55 */     this.timestampRequests = opts.timestampRequests;
/*  56 */     this.socket = opts.socket;
/*  57 */     this.webSocketFactory = opts.webSocketFactory;
/*  58 */     this.callFactory = opts.callFactory;
/*     */   }
/*     */   
/*     */   protected Transport onError(String msg, Exception desc)
/*     */   {
/*  63 */     Exception err = new EngineIOException(msg, desc);
/*  64 */     emit("error", new Object[] { err });
/*  65 */     return this;
/*     */   }
/*     */   
/*     */   public Transport open() {
/*  69 */     io.socket.thread.EventThread.exec(new Runnable()
/*     */     {
/*     */       public void run() {
/*  72 */         if ((Transport.this.readyState == ReadyState.CLOSED) || (Transport.this.readyState == null)) {
/*  73 */           Transport.this.readyState = ReadyState.OPENING;
/*  74 */           Transport.this.doOpen();
/*     */         }
/*     */       }
/*  77 */     });
/*  78 */     return this;
/*     */   }
/*     */   
/*     */   public Transport close() {
/*  82 */     io.socket.thread.EventThread.exec(new Runnable()
/*     */     {
/*     */       public void run() {
/*  85 */         if ((Transport.this.readyState == ReadyState.OPENING) || (Transport.this.readyState == ReadyState.OPEN)) {
/*  86 */           Transport.this.doClose();
/*  87 */           Transport.this.onClose();
/*     */         }
/*     */       }
/*  90 */     });
/*  91 */     return this;
/*     */   }
/*     */   
/*     */   public void send(final Packet[] packets) {
/*  95 */     io.socket.thread.EventThread.exec(new Runnable()
/*     */     {
/*     */       public void run() {
/*  98 */         if (Transport.this.readyState == ReadyState.OPEN) {
/*     */           try {
/* 100 */             Transport.this.write(packets);
/*     */           } catch (io.socket.utf8.UTF8Exception err) {
/* 102 */             throw new RuntimeException(err);
/*     */           }
/*     */         } else {
/* 105 */           throw new RuntimeException("Transport not open");
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   protected void onOpen() {
/* 112 */     this.readyState = ReadyState.OPEN;
/* 113 */     this.writable = true;
/* 114 */     emit("open", new Object[0]);
/*     */   }
/*     */   
/*     */   protected void onData(String data) {
/* 118 */     onPacket(io.socket.engineio.parser.Parser.decodePacket(data));
/*     */   }
/*     */   
/*     */   protected void onData(byte[] data) {
/* 122 */     onPacket(io.socket.engineio.parser.Parser.decodePacket(data));
/*     */   }
/*     */   
/*     */   protected void onPacket(Packet packet) {
/* 126 */     emit("packet", new Object[] { packet });
/*     */   }
/*     */   
/*     */   protected void onClose() {
/* 130 */     this.readyState = ReadyState.CLOSED;
/* 131 */     emit("close", new Object[0]);
/*     */   }
/*     */   
/*     */   protected abstract void write(Packet[] paramArrayOfPacket)
/*     */     throws io.socket.utf8.UTF8Exception;
/*     */   
/*     */   protected abstract void doOpen();
/*     */   
/*     */   protected abstract void doClose();
/*     */   
/*     */   public static class Options
/*     */   {
/*     */     public String hostname;
/*     */     public String path;
/*     */     public String timestampParam;
/*     */     public boolean secure;
/*     */     public boolean timestampRequests;
/* 148 */     public int port = -1;
/* 149 */     public int policyPort = -1;
/*     */     public java.util.Map<String, String> query;
/*     */     protected Socket socket;
/*     */     public okhttp3.WebSocket.Factory webSocketFactory;
/*     */     public okhttp3.Call.Factory callFactory;
/*     */   }
/*     */ }


