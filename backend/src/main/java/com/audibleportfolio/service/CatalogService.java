package com.audibleportfolio.service;
import com.audibleportfolio.domain.*;
import java.util.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
@Service public class CatalogService {
 private final CatalogIndex index;private final StringRedisTemplate redis;private final ObjectMapper json;
 public CatalogService(CatalogIndex i,StringRedisTemplate redis,ObjectMapper json){this.index=i;this.redis=redis;this.json=json;}
 public List<Book> search(String q,int limit){
  if(limit<1||limit>100)throw new IllegalArgumentException("limit out of range");
  String key="catalog:v1:"+limit+":"+String.join("_",CatalogIndex.tokens(q));
  try{String hit=redis.opsForValue().get(key);if(hit!=null)return json.readValue(hit,json.getTypeFactory().constructCollectionType(List.class,Book.class));}catch(Exception ignored){/* Redis is optional; local index fallback */}
  List<Book> found=index.search(q,limit);
  try{redis.opsForValue().set(key,json.writeValueAsString(found),Duration.ofMinutes(5));}catch(Exception ignored){/* degrade gracefully */}
  return found;
 }
 public Optional<Book> find(String id){return index.get(id);}
 public int size(){return index.size();}
}