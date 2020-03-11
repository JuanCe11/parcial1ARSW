package edu.eci.arsw.exams.moneylaunderingapi.service;

import edu.eci.arsw.exams.moneylaunderingapi.MoneyLauderingException;
import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class MoneyLaunderingServiceStub implements MoneyLaunderingService {
    HashMap<String, SuspectAccount> suspectAccounts;

    public MoneyLaunderingServiceStub(){
        suspectAccounts = new HashMap<String,SuspectAccount>();
        SuspectAccount a1  = new SuspectAccount();
        a1.setAccountId("1");
        a1.setAmountOfSmallTransactions(10);
        suspectAccounts.put(a1.getAccountId(),a1);

    }
    @Override
    public void addSuspectAccount(SuspectAccount suspectAccount) throws MoneyLauderingException {
        if (suspectAccounts.containsKey(suspectAccount.getAccountId())){
            throw new MoneyLauderingException("La cuenta ya fue creada"+ suspectAccount.getAccountId());
        }
        else{
            suspectAccounts.put(suspectAccount.getAccountId(),suspectAccount);
        }
    }

    @Override
    public void updateAccountStatus(SuspectAccount suspectAccount) throws MoneyLauderingException {
        ArrayList<SuspectAccount> sp = new ArrayList<>();
        suspectAccounts.entrySet().forEach((entry) -> {
            if (entry.getKey().equals(suspectAccount.getAccountId())){
                sp.add(entry.getValue());
            }
        });
        if(sp.isEmpty()){
            throw  new MoneyLauderingException("No encontrado");
        }
        SuspectAccount vieja = suspectAccounts.get(suspectAccount.getAccountId());
        vieja.setAmountOfSmallTransactions(suspectAccount.getAmountOfSmallTransactions() + vieja.getAmountOfSmallTransactions());
    }

    @Override
    public SuspectAccount getAccountStatus(String accountId) {
        return suspectAccounts.get(accountId);
    }

    @Override
    public List<SuspectAccount> getSuspectAccounts() {
        ArrayList<SuspectAccount> s = new  ArrayList<SuspectAccount>();
        suspectAccounts.entrySet().forEach((entry) -> {
            s.add(entry.getValue());
        });
        return s;
    }


}
