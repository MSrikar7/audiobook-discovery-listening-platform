import com.audibleportfolio.domain.*;
import java.util.*;
import java.nio.file.*;
/** Execute after compiling domain/*.java. Prints verifiable deterministic correctness + timing. */
public class Benchmark {
  static volatile int sink;
  static long measure(Runnable work,int times){long before=System.nanoTime();for(int i=0;i<times;i++)work.run();return System.nanoTime()-before;}
  public static void main(String[] args)throws Exception {
    int count=args.length>0?Integer.parseInt(args[0]):100_000;
    var books=SampleCatalog.generate(count);var ix=new CatalogIndex(books);
    var queries=List.of("mystery","science","history","fantasy","fiction","biography","chronicles valley","narrator 31","volume 99999","absentword");
    for(String q:queries)if(!ix.search(q,20).equals(ix.linearSearch(q,20)))throw new AssertionError("wrong results: "+q);
    for(int i=0;i<10;i++){String q=queries.get(i%queries.size());sink=ix.search(q,20).size();sink=ix.linearSearch(q,20).size();}
    int iterations=30;long[] indexed=new long[iterations],linear=new long[iterations];
    for(int i=0;i<iterations;i++){String q=queries.get(i%queries.size());
       if(i%2==0){indexed[i]=measure(()->{sink=ix.search(q,20).size();},1);linear[i]=measure(()->{sink=ix.linearSearch(q,20).size();},1);}
       else{linear[i]=measure(()->{sink=ix.linearSearch(q,20).size();},1);indexed[i]=measure(()->{sink=ix.search(q,20).size();},1);}}
    Arrays.sort(indexed);Arrays.sort(linear);
    double im=indexed[iterations/2]/1e6,lm=linear[iterations/2]/1e6;
    String output="{\"dataset\":\"synthetic\",\"catalog_records\":"+count+",\"queries\":"+iterations+",\"correctness_checks\":10,\"indexed_median_ms\":"+im+",\"linear_median_ms\":"+lm+",\"median_latency_reduction_percent\":"+(100*(1-im/lm))+",\"note\":\"in-process Java search only; not Redis, HTTP, Kafka or production load\"}";
    System.out.println(output);if(args.length>1)Files.writeString(Path.of(args[1]),output+"\n");
  }
}