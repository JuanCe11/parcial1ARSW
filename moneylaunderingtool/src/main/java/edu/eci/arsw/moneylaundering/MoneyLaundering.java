package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoneyLaundering
{
    public static TransactionAnalyzer transactionAnalyzer;
    public static TransactionReader transactionReader;
    public static AtomicInteger amountOfFilesProcessed;
    public static AtomicInteger finishedThreads;
    public static Object monitor = new Object();
    public static boolean pausado = false;
    private int amountOfFilesTotal;
    private AnalizingThread[] hilos;

    public MoneyLaundering()
    {
        transactionAnalyzer = new TransactionAnalyzer();
        transactionReader = new TransactionReader();
        amountOfFilesProcessed = new AtomicInteger();
        finishedThreads = new AtomicInteger();
    }

    public void processTransactionData(int cantidadHilos ,MoneyLaundering moneyLaundering)
    {
        hilos = new AnalizingThread[cantidadHilos];
        amountOfFilesProcessed.set(0);
        List<File> transactionFiles = getTransactionFileList();
        amountOfFilesTotal = transactionFiles.size();
        List<ArrayList> archivos =  asignarArchivos(transactionFiles,cantidadHilos);
        finishedThreads.set(0);
        for (int i = 0; i < cantidadHilos; i++) {
            hilos[i] = new AnalizingThread(archivos.get(i));
            hilos[i].start();
        }

        for (int i = 0; i < hilos.length; i++) {
            try {
                hilos[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private List<ArrayList> asignarArchivos(List<File> files, int cantidadHilos){
        ArrayList<ArrayList> res = new ArrayList<>();
        int cont = 0;
        for (int i = 0; i < cantidadHilos ; i++) {
            res.add(new ArrayList<File>());
        }
        for (int i = 0; i < files.size() ; i++) {
            if(cont == res.size())cont = 0;
            res.get(cont).add(files.get(i));
            cont ++;
        }
        return res;
    }



    public List<String> getOffendingAccounts()
    {
        return transactionAnalyzer.listOffendingAccounts();
    }

    private List<File> getTransactionFileList()
    {
        List<File> csvFiles = new ArrayList<>();
        try (Stream<Path> csvFilePaths = Files.walk(Paths.get("src/main/resources/")).filter(path -> path.getFileName().toString().endsWith(".csv"))) {
            csvFiles = csvFilePaths.map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvFiles;
    }

    public void pausar(){

        if(pausado){
            pausado = false;
            synchronized (monitor){
                monitor.notifyAll();
            }
            for (int i = 0; i < hilos.length; i++) {
                hilos[i].despausar();
            }
            System.out.println("running");
        }else{
            pausado = true;
           /* int suma  = 0;
            while (suma != hilos.length){
                System.out.println("Pausados "+suma);
                for (int i = 0; i < hilos.length; i++) {
                    System.out.println(hilos[i].isPausado());
                    suma = (hilos[i].isPausado())?suma+1 : suma;
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
            System.out.println("paused");
        }

    };


    public static void main(String[] args)
    {
        MoneyLaundering moneyLaundering = new MoneyLaundering();
        int cantidadHilos = 5;
        Thread processingThread = new Thread(() -> moneyLaundering.processTransactionData(cantidadHilos,moneyLaundering));
        processingThread.start();
        while(finishedThreads.get() != cantidadHilos){
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (finishedThreads.get() != cantidadHilos)
                moneyLaundering.pausar();
            if(line.contains("exit"))
                break;
            String message = "Processed %d out of %d files.\nFound %d suspect accounts.";
            List<String> offendingAccounts = moneyLaundering.getOffendingAccounts();
            message = String.format(message, moneyLaundering.amountOfFilesProcessed.get(), moneyLaundering.amountOfFilesTotal, offendingAccounts.size());
            System.out.println(message);

        }
        String message = "FINISH! \nProcessed %d out of %d files.\nFound %d suspect accounts:\n%s";
        List<String> offendingAccounts = moneyLaundering.getOffendingAccounts();
        String suspectAccounts = offendingAccounts.stream().reduce("", (s1, s2)-> s1 + "\n"+s2);
        message = String.format(message, moneyLaundering.amountOfFilesProcessed.get(), moneyLaundering.amountOfFilesTotal, offendingAccounts.size(), suspectAccounts);
        System.out.println(message);

    }
}