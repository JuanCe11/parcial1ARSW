package edu.eci.arsw.exams.moneylaunderingapi;


import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;
import edu.eci.arsw.exams.moneylaunderingapi.service.MoneyLaunderingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/fraud-bank-accounts")
public class MoneyLaunderingController
{
    @Autowired
    MoneyLaunderingService moneyLaunderingService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> offendingAccounts() {
        List<SuspectAccount> data = null;
        try{
            data = moneyLaunderingService.getSuspectAccounts();
            return new ResponseEntity<>(data, HttpStatus.ACCEPTED);
        }catch (Exception e){
            return new ResponseEntity<>("ERROR 500",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addSuspect(@RequestBody SuspectAccount o) {
        try{
            moneyLaunderingService.addSuspectAccount(o);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>("ERROR 500",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping( value = "/{accountId}", method = RequestMethod.GET)
    public ResponseEntity<?> getAccountStatus(@PathVariable String accountId)
    {
        try{
            return new ResponseEntity<>(moneyLaunderingService.getAccountStatus(accountId), HttpStatus.ACCEPTED);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{accountId}",method = RequestMethod.PUT)
    public ResponseEntity<?> putSuspectAccount(@PathVariable String accountId,@RequestBody SuspectAccount sp){
        try {
            moneyLaunderingService.updateAccountStatus(sp);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}