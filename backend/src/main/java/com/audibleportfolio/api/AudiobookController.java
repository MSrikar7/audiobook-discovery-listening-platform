package com.audibleportfolio.api;
import com.audibleportfolio.domain.*;
import com.audibleportfolio.service.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.util.*;
@CrossOrigin(origins="${frontend.origin:http://localhost:5173}")
@RestController@RequestMapping("/api")
public class AudiobookController {
 private final CatalogService catalog;private final ProgressService progress;
 public AudiobookController(CatalogService catalog,ProgressService progress){this.catalog=catalog;this.progress=progress;}
 @GetMapping("/books") public List<Book> search(@RequestParam(defaultValue="chronicles")String q,@RequestParam(defaultValue="12")int limit){return catalog.search(q,limit);}
 @GetMapping("/books/{id}")public ResponseEntity<Book> book(@PathVariable String id){return catalog.find(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());}
 @GetMapping("/health")public Map<String,Object> health(){return Map.of("catalogRecords",catalog.size(),"status","ok");}
 public record ProgressRequest(String userId,String bookId,int second,long sequence){}
 @PostMapping("/progress")public ProgressTracker.Progress progress(@RequestBody ProgressRequest request){
  int duration=catalog.find(request.bookId()).orElseThrow(()->new NoSuchElementException("book not found")).durationSeconds();
  return progress.update(request.userId(),request.bookId(),request.second(),duration,request.sequence());}
 @GetMapping("/progress/{user}/{book}")public ResponseEntity<ProgressTracker.Progress> getProgress(@PathVariable String user,@PathVariable String book){return Optional.ofNullable(progress.get(user,book)).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());}
 @ExceptionHandler({IllegalArgumentException.class,NoSuchElementException.class})public ResponseEntity<Map<String,String>> badRequest(RuntimeException ex){return ResponseEntity.badRequest().body(Map.of("error",ex.getMessage()));}
}