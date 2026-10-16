package com.audibleportfolio.service;
import com.audibleportfolio.domain.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
@Service public class ProgressService {
 public record ProgressEvent(String userId,String bookId,int second,long sequence){}
 private final ProgressTracker state;private final KafkaTemplate<String,ProgressEvent> kafka;
 public ProgressService(ProgressTracker state,KafkaTemplate<String,ProgressEvent> kafka){this.state=state;this.kafka=kafka;}
 public ProgressTracker.Progress update(String user,String book,int sec,int duration,long seq){
  var progress=state.update(user,book,sec,duration,seq);
  // Non-blocking broker send; this is NOT a durable outbox and is best-effort.
  try{kafka.send("listening.progress",user+":"+book,new ProgressEvent(user,book,progress.second(),progress.sequence()));}catch(Exception e){System.err.println("Kafka unavailable: "+e.getMessage());}
  return progress;
 }
 public ProgressTracker.Progress get(String user,String book){return state.get(user,book);}
}