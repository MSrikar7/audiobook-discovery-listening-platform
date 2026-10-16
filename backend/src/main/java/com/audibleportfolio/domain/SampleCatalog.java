package com.audibleportfolio.domain;
import java.util.*;
public final class SampleCatalog {
  private SampleCatalog(){}
  public static List<Book> generate(int count){
    String[] genres={"mystery","science","history","fantasy","fiction","biography"};
    List<Book> all=new ArrayList<>(count);
    for(int i=0;i<count;i++)all.add(new Book("bk-"+i,"Chronicles of the " + genres[i%genres.length]+" valley volume "+i,
     "Narrator "+(i%257),genres[i%genres.length],1800+i%12000, i < 256 ? "/audio/book-"+i+".mp3" : null));
    return all;
  }
}