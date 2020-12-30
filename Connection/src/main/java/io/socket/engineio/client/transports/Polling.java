/*     */ package io.socket.engineio.client.transports;
/*     */ 
/*     */

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;
import io.socket.engineio.parser.Packet;
import io.socket.engineio.parser.Parser;
import io.socket.thread.EventThread;
import io.socket.utf8.UTF8Exception;
import io.socket.yeast.Yeast;

/*     */
/*     */
/*
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */

/*     */
/*     */ public abstract class Polling extends Transport
/*     */ {
/*  20 */   private static final Logger logger = Logger.getLogger(Polling.class.getName());
/*     */   
/*     */   public static final String NAME = "polling";
/*     */   
/*     */   public static final String EVENT_POLL = "poll";
/*     */   
/*     */   public static final String EVENT_POLL_COMPLETE = "pollComplete";
/*     */   private boolean polling;
/*     */   
/*     */   public Polling(Transport.Options opts)
/*     */   {
/*  31 */     super(opts);
/*  32 */     this.name = "polling";
/*     */   }
/*     */   
/*     */   protected void doOpen() {
/*  36 */     poll();
/*     */   }
/*     */
public void pause(final Runnable onPause) {
    EventThread.exec(new Runnable() {
        @Override
        public void run() {
            final Polling self = Polling.this;

            Polling.this.readyState = ReadyState.PAUSED;

            final Runnable pause = new Runnable() {
                @Override
                public void run() {
                    logger.fine("paused");
                    self.readyState = ReadyState.PAUSED;
                    onPause.run();
                }
            };

            if (Polling.this.polling || !Polling.this.writable) {
                final int[] total = new int[]{0};

                if (Polling.this.polling) {
                    logger.fine("we are currently polling - waiting to pause");
                    total[0]++;
                    Polling.this.once(EVENT_POLL_COMPLETE, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            logger.fine("pre-pause polling complete");
                            if (--total[0] == 0) {
                                pause.run();
                            }
                        }
                    });
                }

                if (!Polling.this.writable) {
                    logger.fine("we are currently writing - waiting to pause");
                    total[0]++;
                    Polling.this.once(EVENT_DRAIN, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            logger.fine("pre-pause writing complete");
                            if (--total[0] == 0) {
                                pause.run();
                            }
                        }
                    });
                }
            } else {
                pause.run();
            }
        }
    });
}
/*     */   
/*     */   private void poll() {
/*  94 */     logger.fine("polling");
/*  95 */     this.polling = true;
/*  96 */     doPoll();
/*  97 */     emit("poll", new Object[0]);
/*     */   }
/*     */   
/*     */   protected void onData(String data)
/*     */   {
/* 102 */     _onData(data);
/*     */   }
/*     */   
/*     */   protected void onData(byte[] data)
/*     */   {
/* 107 */     _onData(data);
/*     */   }
/*     */   
/*     */   private void _onData(Object data) {
/* 111 */     final Polling self = this;
/* 112 */     if (logger.isLoggable(Level.FINE)) {
/* 113 */       logger.fine(String.format("polling got data %s", new Object[] { data }));
/*     */     }
/* 115 */     Parser.DecodePayloadCallback callback = new Parser.DecodePayloadCallback()
/*     */     {
/*     */       public boolean call(Packet packet, int index, int total) {
/* 118 */         if (self.readyState == Transport.ReadyState.OPENING) {
/* 119 */           self.onOpen();
/*     */         }
/*     */         
/* 122 */         if ("close".equals(packet.type)) {
/* 123 */           self.onClose();
/* 124 */           return false;
/*     */         }
/*     */         
/* 127 */         self.onPacket(packet);
/* 128 */         return true;
/*     */       }
/*     */     };
/*     */     
/* 132 */     if ((data instanceof String))
/*     */     {
/* 134 */       Parser.DecodePayloadCallback<String> tempCallback = callback;
/* 135 */       Parser.decodePayload((String)data, tempCallback);
/* 136 */     } else if ((data instanceof byte[])) {
/* 137 */       Parser.decodePayload((byte[])data, callback);
/*     */     }
/*     */     
/* 140 */     if (this.readyState != Transport.ReadyState.CLOSED) {
/* 141 */       this.polling = false;
/* 142 */       emit("pollComplete", new Object[0]);
/*     */       
/* 144 */       if (this.readyState == Transport.ReadyState.OPEN) {
/* 145 */         poll();
/*     */       }
/* 147 */       else if (logger.isLoggable(Level.FINE)) {
/* 148 */         logger.fine(String.format("ignoring poll - transport state '%s'", new Object[] { this.readyState }));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected void doClose()
/*     */   {
/* 155 */     final Polling self = this;
/*     */     
/* 157 */     Emitter.Listener close = new Emitter.Listener()
/*     */     {
/*     */       public void call(Object... args) {
/* 160 */         Polling.logger.fine("writing close packet");
/*     */         try {
/* 162 */           self.write(new Packet[] { new Packet("close") });
/*     */         } catch (UTF8Exception err) {
/* 164 */           throw new RuntimeException(err);
/*     */         }
/*     */       }
/*     */     };
/*     */     
/* 169 */     if (this.readyState == Transport.ReadyState.OPEN) {
/* 170 */       logger.fine("transport open - closing");
/* 171 */       close.call(new Object[0]);
/*     */     }
/*     */     else
/*     */     {
/* 175 */       logger.fine("transport not open - deferring close");
/* 176 */       once("open", close);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void write(Packet[] packets) throws UTF8Exception {
/* 181 */     final Polling self = this;
/* 182 */     this.writable = false;
/* 183 */     final Runnable callbackfn = new Runnable()
/*     */     {
/*     */       public void run() {
/* 186 */         self.writable = true;
/* 187 */         self.emit("drain", new Object[0]);
/*     */       }
/*     */       
/* 190 */     };
/* 191 */     Parser.encodePayload(packets, new io.socket.engineio.parser.Parser.EncodeCallback()
/*     */     {
/*     */       public void call(Object data) {
/* 194 */         if ((data instanceof byte[])) {
/* 195 */           self.doWrite((byte[])data, callbackfn);
/* 196 */         } else if ((data instanceof String)) {
/* 197 */           self.doWrite((String)data, callbackfn);
/*     */         } else {
/* 199 */           Polling.logger.warning("Unexpected data: " + data);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   protected String uri() {
/* 206 */     Map<String, String> query = this.query;
/* 207 */     if (query == null) {
/* 208 */       query = new HashMap();
/*     */     }
/* 210 */     String schema = this.secure ? "https" : "http";
/* 211 */     String port = "";
/*     */     
/* 213 */     if (this.timestampRequests) {
/* 214 */       query.put(this.timestampParam, Yeast.yeast());
/*     */     }
/*     */     
/* 217 */     String derivedQuery = io.socket.parseqs.ParseQS.encode(query);
/*     */     
/* 219 */     if ((this.port > 0) && ((("https".equals(schema)) && (this.port != 443)) || (
/* 220 */       ("http".equals(schema)) && (this.port != 80)))) {
/* 221 */       port = ":" + this.port;
/*     */     }
/*     */     
/* 224 */     if (derivedQuery.length() > 0) {
/* 225 */       derivedQuery = "?" + derivedQuery;
/*     */     }
/*     */     
/* 228 */     boolean ipv6 = this.hostname.contains(":");
/* 229 */     return schema + "://" + (ipv6 ? "[" + this.hostname + "]" : this.hostname) + port + this.path + derivedQuery;
/*     */   }
/*     */   
/*     */   protected abstract void doWrite(byte[] paramArrayOfByte, Runnable paramRunnable);
/*     */   
/*     */   protected abstract void doWrite(String paramString, Runnable paramRunnable);
/*     */   
/*     */   protected abstract void doPoll();
/*     */ }

