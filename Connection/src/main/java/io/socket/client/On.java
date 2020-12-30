/*    */ package io.socket.client;
/*    */ 
/*    */

import io.socket.emitter.Emitter;

/*    */

/*    */
/*    */ public class On
/*    */ {
    public static Handle on(final Emitter obj, final String ev, final Emitter.Listener fn) {
        obj.on(ev, fn);
        return new Handle() {
            @Override
            public void destroy() {
                obj.off(ev, fn);
            }
        };
    }
/*    */   
/*    */   public static abstract interface Handle
/*    */   {
/*    */     public abstract void destroy();
/*    */   }
/*    */ }

