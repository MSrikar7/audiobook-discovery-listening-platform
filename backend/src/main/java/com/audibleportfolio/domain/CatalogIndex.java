package com.audibleportfolio.domain;
import java.util.*;
/** Immutable catalog index, no external dependencies. Thread-safe after construction. */
public final class CatalogIndex {
  private final List<Book> books;
  private final Map<String,Book> byId;
  private final Map<String,Set<Integer>> inverted;
  public CatalogIndex(List<Book> input) {
    books=List.copyOf(input); Map<String,Book> ids=new HashMap<>(); Map<String,Set<Integer>> terms=new HashMap<>();
    for (int i=0;i<books.size();i++) {
      Book b=books.get(i); if(ids.putIfAbsent(b.id(),b)!=null) throw new IllegalArgumentException("duplicate id: "+b.id());
      for(String token:tokens(b.title()+" "+b.author()+" "+b.genre())) terms.computeIfAbsent(token,k->new HashSet<>()).add(i);
    }
    byId=Map.copyOf(ids); Map<String,Set<Integer>> safe=new HashMap<>();terms.forEach((k,v)->safe.put(k,Set.copyOf(v))); inverted=Map.copyOf(safe);
  }
  public int size(){return books.size();}
  public Optional<Book> get(String id){return Optional.ofNullable(byId.get(id));}
  public List<Book> search(String query,int limit){
    if(limit<1||limit>100) throw new IllegalArgumentException("limit must be 1-100");
    List<String> words=tokens(query); if(words.isEmpty()) return List.of();
    Set<Integer> candidates=null;
    for(String word:words){ Set<Integer> ids=inverted.getOrDefault(word,Set.of()); if(ids.isEmpty())return List.of();
      if(candidates==null)candidates=new HashSet<>(ids);else candidates.retainAll(ids); if(candidates.isEmpty())return List.of(); }
    List<Integer> order=new ArrayList<>(candidates); Collections.sort(order); List<Book> result=new ArrayList<>();
    for(int id:order){result.add(books.get(id));if(result.size()==limit)break;}return result;
  }
  public List<Book> linearSearch(String query,int limit){
    List<String> words=tokens(query); if(words.isEmpty())return List.of(); List<Book> found=new ArrayList<>();
    for(Book b:books){Set<String> have=new HashSet<>(tokens(b.title()+" "+b.author()+" "+b.genre()));if(have.containsAll(words)){found.add(b);if(found.size()==limit)break;}}
    return found;
  }
  public static List<String> tokens(String s){ if(s==null)return List.of(); String cleaned=s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+"," ").trim(); return cleaned.isEmpty()?List.of():List.of(cleaned.split(" +")); }
}