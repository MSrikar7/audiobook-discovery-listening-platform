import com.audibleportfolio.domain.*;
import java.util.*;
public class DomainTests {
  static int checks=0;
  static void ok(boolean result){checks++;if(!result)throw new AssertionError("assertion "+checks);}
  public static void main(String[] args){
    CatalogIndex idx=new CatalogIndex(SampleCatalog.generate(10000));
    ok(idx.size()==10000);ok(idx.get("bk-9").isPresent());ok(idx.get("missing").isEmpty());
    ok(idx.search("mystery",10).equals(idx.linearSearch("mystery",10)));
    ok(idx.search("CHRONICLES valley",20).equals(idx.linearSearch("CHRONICLES valley",20)));
    ok(idx.search("unheard",10).isEmpty());ok(idx.search("",10).isEmpty());
    try{idx.search("mystery",101);throw new AssertionError();}catch(IllegalArgumentException expected){checks++;}
    ProgressTracker tracker=new ProgressTracker();
    ok(tracker.update("u","bk-1",35,100,1).second()==35);
    ok(tracker.update("u","bk-1",2,100,0).second()==35);
    ok(tracker.update("u","bk-1",14,100,2).second()==14);
    try{tracker.update("u","bk-1",101,100,3);throw new AssertionError();}catch(IllegalArgumentException expected){checks++;}
    System.out.println("PASS: "+checks+" Java domain assertions");
  }
}