package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.util.List;

public class AnalizingThread extends Thread{
    List<File> transactionFiles;

    public AnalizingThread(List<File> transactionFiles){
        this.transactionFiles = transactionFiles;
    }


    @Override
    public void run() {
        for(File transactionFile : transactionFiles)
        {
            List<Transaction> transactions = MoneyLaundering.transactionReader.readTransactionsFromFile(transactionFile);
            for(Transaction transaction : transactions)
            {
                synchronized (MoneyLaundering.monitor){
                    if(MoneyLaundering.pausado) {
                        try {
                            MoneyLaundering.monitor.wait();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                MoneyLaundering.transactionAnalyzer.addTransaction(transaction);
            }
            MoneyLaundering.amountOfFilesProcessed.incrementAndGet();
        }
        MoneyLaundering.finishedThreads.incrementAndGet();
    }
}
