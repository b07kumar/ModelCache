package com.test.barcap.cache;

import com.test.barcap.cache.pojo.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ModelDataService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public List<Model> request(List<String> stockCodeList) throws InterruptedException, ExecutionException {
        List<Model>  result = new ArrayList<Model>();

        CompletionService completionService = new ExecutorCompletionService(executorService);
        List<Callable<Model>> taskList = stockCodeList.stream().<Callable<Model>>map(s -> () -> {return ModelCache.get(s);} ).collect(Collectors.toList());
        List<Future<Model>> futureList = new ArrayList<Future<Model>>();

        for( Callable<Model> task : taskList) {
            Future<Model> future = completionService.submit(task);
            futureList.add(future);
        }

        for( int taskCount = 0; taskCount < taskList.size(); taskCount++)
        {
            Future<Model> future = completionService.take();
            result.add(future.get());
        }

        return result;
    }

    public void updateModel(List<Model> modelValues) throws InterruptedException {
        List<Callable<Boolean>> updateTaskList = modelValues.stream().<Callable<Boolean>>map(m -> () -> { return ModelCache.update(m);} ).collect(Collectors.toList());
        executorService.invokeAll(updateTaskList);
    }
}
