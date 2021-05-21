package com.test.barcap;

import com.test.barcap.cache.pojo.Model;
import com.test.barcap.cache.ModelCache;
import com.test.barcap.cache.ModelDataService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

public class MainClass {
    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ModelDataService modelDataService = new ModelDataService();
        Random random = new Random();
        Runnable updatetask = () -> {
            try {
                Model model = new Model("1 HK", random.nextDouble());
                modelDataService.updateModel( Arrays.asList( model ));
                System.out.println("Updating  :" + model);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        executorService.scheduleAtFixedRate( updatetask, 0, 2, TimeUnit.SECONDS);
        Runnable requestTask = () -> {
            try {
                List<Model> result = modelDataService.request(Arrays.asList("1 HK", "5 HK"));
                System.out.println("Result :" + result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        };

        executorService.scheduleAtFixedRate(requestTask, 0, 2, TimeUnit.SECONDS);
    }
}
