package com.audibleportfolio.config;
import com.audibleportfolio.domain.*;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
@Configuration public class AppConfig {
 @Bean CatalogIndex catalog(@Value("${catalog.records:10000}") int records){return new CatalogIndex(SampleCatalog.generate(records));}
 @Bean ProgressTracker progress(){return new ProgressTracker();}
}