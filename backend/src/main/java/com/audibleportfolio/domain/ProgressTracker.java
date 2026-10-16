package com.audibleportfolio.domain;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
/** Atomic per-user/book positions with monotonic event sequence for stale event rejection. */
public final class ProgressTracker {
  public record Progress(String userId,String bookId,int second,long sequence){}
  private final Map<String,Progress> state=new ConcurrentHashMap<>();
  public Progress update(String user,String book,int seconds,int duration,long seq){
    if(user==null||user.isBlank()||book==null||book.isBlank())throw new IllegalArgumentException("missing identity");
    if(seconds<0||seconds>duration||duration<1||seq<0)throw new IllegalArgumentException("invalid playback position");
    String key=user+"\u0000"+book;
    return state.compute(key,(k,prior)->prior!=null&&seq<=prior.sequence()?prior:new Progress(user,book,seconds,seq));
  }
  public Progress get(String user,String book){return state.get(user+"\u0000"+book);}
}