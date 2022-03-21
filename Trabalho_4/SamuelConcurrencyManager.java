
package ibd.transaction;

import ibd.Record;
import java.util.Hashtable;

/**
 * 
 * @author Samuel
 */
public class SamuelConcurrencyManager extends ConcurrencyManager{
    
    int max_ticks;   
    Hashtable<Transaction, Integer> transactionsTick;  
    
    public SamuelConcurrencyManager(int max_ticks) throws Exception {
        super();
        this.max_ticks = max_ticks;
        this.transactionsTick = new Hashtable<>();
    }   
    
    @Override
    protected boolean shouldAbort(Transaction t){
        boolean abort = super.shouldAbort(t);
           
        if (transactionsTick.containsKey(t)) {
            transactionsTick.put(t, transactionsTick.get(t)+1);
        }else{            
            transactionsTick.put(t, 1);
        }
        
        for (Transaction key : transactionsTick.keySet()) {
            int tick = transactionsTick.get(key);
            if(max_ticks==tick){
                abort=true;
                transactionsTick.put(t, 0);
            }            
        }
        
        return abort;
    }
  
    @Override
    protected Record processCurrentInstruction(Transaction t) throws Exception{        
        Record r = super.processCurrentInstruction(t);
        transactionsTick.put(t, 0);
        return r;
    }        
}
