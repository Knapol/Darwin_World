package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimulationEngine {
    private final List<Simulation> listOfSimulations;
    private final List<Thread> simulationsThreads = new ArrayList<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public SimulationEngine(List<Simulation> listOfSimulations){
        this.listOfSimulations = listOfSimulations;
    }

    public void runSync() {
        for (Simulation sim : listOfSimulations){
            sim.run();
        }
    }

    public void runAsync(){
        for (Simulation sim : listOfSimulations){
            simulationsThreads.add(new Thread(sim));
        }

        for (Thread simThread : simulationsThreads){
            simThread.start();
        }
    }

    public void awaitSimulationsEnd() throws InterruptedException{
        for (Thread simThread : simulationsThreads){
            simThread.join();
        }

        if (!executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                    if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                        System.out.println("Nie udało się zakończyć wszystkich wątków");
                    }
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
    }

    public void runAsyncInThreadPool(){
        for (Simulation sim : listOfSimulations){
            executorService.submit(sim);
        }
    }
}
