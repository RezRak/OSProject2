import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ProducerConsumerSim {

    // Buffer size and shared queue for producer-consumer
    static final int BUFFER_SIZE = 5;
    static BlockingQueue<Integer> buffer = new ArrayBlockingQueue<>(BUFFER_SIZE);

    public static void main(String[] args) throws Exception {
        // Read processes from input file
        List<ProcessThread> processList = readProcessesFromFile("processes.txt");

        System.out.println("=== Starting Process Threads ===");

        // Start each process thread
        List<Thread> threads = new ArrayList<>();
        for (ProcessThread pt : processList) {
            Thread t = new Thread(pt);
            threads.add(t);
            t.start();
        }

        // Wait for all process threads to complete
        for (Thread t : threads) {
            t.join();
        }

        System.out.println("\n=== Starting Producer-Consumer Simulation ===");

        // Create and start producer and consumer threads
        Thread producer = new Thread(new Producer());
        Thread consumer = new Thread(new Consumer());

        producer.start();
        consumer.start();

        // Wait for both to finish
        producer.join();
        consumer.join();

        System.out.println("=== Simulation Complete ===");
    }

    // Reads process data from the file and returns a list of ProcessThreads
    public static List<ProcessThread> readProcessesFromFile(String filename) {
        List<ProcessThread> processes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("PID")) {
                    continue; // Skip header or blank lines
                }
                String[] parts = line.trim().split("\\s+");
                int pid = Integer.parseInt(parts[0]);
                int burstTime = Integer.parseInt(parts[1]);
                processes.add(new ProcessThread(pid, burstTime));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return processes;
    }

    // Represents a single process (thread) with PID and burst time
    static class ProcessThread implements Runnable {
        int pid;
        int burstTime;

        public ProcessThread(int pid, int burstTime) {
            this.pid = pid;
            this.burstTime = burstTime;
        }

        public void run() {
            System.out.println("Process " + pid + " started.");
            try {
                Thread.sleep(burstTime * 100); // Simulate CPU burst
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Process " + pid + " finished.");
        }
    }

    // Producer thread adds items to the buffer
    static class Producer implements Runnable {
        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    System.out.println("[Producer 1] Waiting for lock to produce " + i);
                    buffer.put(i); // Block if buffer full
                    System.out.println("[Producer 1] Acquired lock and produced: " + i);
                    Thread.sleep((int)(Math.random() * 300)); // Random delay
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Consumer thread removes items from the buffer
    static class Consumer implements Runnable {
        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    System.out.println("[Consumer 1] Waiting for lock to consume...");
                    int item = buffer.take(); // Block if buffer empty
                    System.out.println("[Consumer 1] Acquired lock and consumed: " + item);
                    Thread.sleep((int)(Math.random() * 300)); // Random delay
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}