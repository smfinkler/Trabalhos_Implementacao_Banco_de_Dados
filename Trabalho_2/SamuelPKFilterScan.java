
package ibd.query.sourceop;

import ibd.Table;
import static ibd.index.ComparisonTypes.DIFF;
import static ibd.index.ComparisonTypes.EQUAL;
import static ibd.index.ComparisonTypes.GREATER_EQUAL_THAN;
import static ibd.index.ComparisonTypes.GREATER_THAN;
import static ibd.index.ComparisonTypes.LOWER_EQUAL_THAN;
import static ibd.index.ComparisonTypes.LOWER_THAN;
/**
 *
 * @author Samuel
 */
public class SamuelPKFilterScan extends PKFilterScan{
    
    
    public SamuelPKFilterScan(String sourceName, Table table, int comparisonType, long value) {
        super(sourceName, table, comparisonType, value);
    }
    
    @Override
    public void open() throws Exception {
        super.open();

        switch(comparisonType){ 
            case DIFF:
                currentBlock = 0;
                lastBlock = Table.BLOCKS_AMOUNT-1;             
                break;
                
            case EQUAL:
                buscaBinariaEqual(table, value);
                break;
                
            case LOWER_THAN:
                buscaBinariaLower(table, value);
                break;
                
            case LOWER_EQUAL_THAN:
                buscaBinariaLowerEqual(table, value);
                break;
                
            case GREATER_THAN:
                buscaBinariaGreater(table, value);
                break;
                
            case GREATER_EQUAL_THAN:
                buscaBinariaGreaterEqual(table, value);
                break;
        }
    }
    
    public void buscaBinariaEqual(Table t, long x) throws Exception{
        
        long blocoInicio = 0;
        long blocoMeio;
        long blocoFim = Table.BLOCKS_AMOUNT-1;       

	while(blocoInicio <= blocoFim){
            blocoMeio = (blocoInicio+blocoFim)/2;
            
            if(t.getBlock(blocoMeio).getRecord(0).getPrimaryKey() < x  && t.getBlock(blocoMeio).maxPrimaryKey().getPrimaryKey() < x){                
                blocoInicio = blocoMeio + 1;
            }else if(t.getBlock(blocoMeio).getRecord(0).getPrimaryKey() > x  && t.getBlock(blocoMeio).maxPrimaryKey().getPrimaryKey() > x){
                blocoFim = blocoMeio - 1;
            }else{
                currentBlock = blocoMeio;
                lastBlock = blocoMeio;
                return;
            }
	}
    }
    
    public void buscaBinariaGreater(Table t, long x) throws Exception{
        
        long blocoInicio = 0;
        long blocoMeio = 0;
        long blocoFim = Table.BLOCKS_AMOUNT-1; 

	while(blocoInicio <= blocoFim){
            blocoMeio = (blocoInicio+blocoFim)/2;

            if(t.getBlock(blocoMeio).getRecord(0).getPrimaryKey() > x){
                blocoFim = blocoMeio - 1;
            }else if(t.getBlock(blocoMeio).maxPrimaryKey().getPrimaryKey() <= x){            
                blocoInicio = blocoMeio + 1;
            }else{
                break;
            }
	}
        currentBlock = blocoMeio;
        lastBlock = Table.BLOCKS_AMOUNT-1;
    }
    
    public void buscaBinariaGreaterEqual(Table t, long x) throws Exception{
        
        long blocoInicio = 0;
        long blocoMeio = 0;
        long blocoFim = Table.BLOCKS_AMOUNT-1;   

	while(blocoInicio <= blocoFim){
            blocoMeio = (blocoInicio+blocoFim)/2;

            if(t.getBlock(blocoMeio).maxPrimaryKey().getPrimaryKey() >= x && t.getBlock(blocoMeio).getRecord(0).getPrimaryKey() > x){ 
                blocoFim = blocoMeio - 1; 
            }else if(t.getBlock(blocoMeio).getRecord(0).getPrimaryKey() <= x && t.getBlock(blocoMeio).maxPrimaryKey().getPrimaryKey() < x){
                blocoInicio = blocoMeio + 1;
            }else{
                break;
            }
	}
        currentBlock = blocoMeio;
        lastBlock = Table.BLOCKS_AMOUNT-1;
    } 
    
    public void buscaBinariaLower(Table t, long x) throws Exception{
        
        long blocoInicio = 0;
        long blocoMeio = 0;
        long blocoFim = Table.BLOCKS_AMOUNT-1;       

	while(blocoInicio <= blocoFim){
            blocoMeio = (blocoInicio+blocoFim)/2;

            if(t.getBlock(blocoMeio).maxPrimaryKey().getPrimaryKey() < x){                
                blocoInicio = blocoMeio + 1;
            }else if(t.getBlock(blocoMeio).getRecord(0).getPrimaryKey() >= x){
                blocoFim = blocoMeio - 1;
            }else{
                break;
            }
	}
        currentBlock = 0;
        lastBlock = blocoMeio;
    }
    
    public void buscaBinariaLowerEqual(Table t, long x) throws Exception{
        
        long blocoInicio = 0;
        long blocoMeio = 0;
        long blocoFim = Table.BLOCKS_AMOUNT-1;       

	while(blocoInicio <= blocoFim){
            blocoMeio = (blocoInicio+blocoFim)/2;

            if(t.getBlock(blocoMeio).maxPrimaryKey().getPrimaryKey() < x && t.getBlock(blocoMeio).getRecord(0).getPrimaryKey() <= x){                
                blocoInicio = blocoMeio + 1;
            }else if(t.getBlock(blocoMeio).getRecord(0).getPrimaryKey() > x && t.getBlock(blocoMeio).maxPrimaryKey().getPrimaryKey() >= x){
                blocoFim = blocoMeio - 1;
            }else{
                break;
            }
	}
        currentBlock = 0;
        lastBlock = blocoMeio;
    }
    
}


