package info.mayankag.UserProfileService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheInspectionService {

    @Autowired
    public CacheManager cacheManager;

    public void printCacheContents(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if(cache != null) {
            System.out.println("Cache Contents");
            System.out.println(cache.getNativeCache());
        } else {
            System.out.println("No cache with name [" + cacheName + "] present");
        }
    }

}
