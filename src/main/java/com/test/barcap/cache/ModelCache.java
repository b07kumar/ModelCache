package com.test.barcap.cache;

import com.test.barcap.cache.pojo.Model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModelCache {

    private static volatile Map<String, Double> cache = Collections.synchronizedMap(new HashMap<String, Double>());

    protected static Model get(String stockCode)
    {
        return  new Model( stockCode, cache.get(stockCode));
    }

    protected static boolean update(Model model)
    {
        cache.put(model.getKey(), model.getTarget());
        return true;
    }
    protected static void clear()
    {
        cache.clear();
    }
}
