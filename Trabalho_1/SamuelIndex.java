
package ibd.index;

import static ibd.index.ComparisonTypes.DIFF;
import static ibd.index.ComparisonTypes.EQUAL;
import static ibd.index.ComparisonTypes.GREATER_EQUAL_THAN;
import static ibd.index.ComparisonTypes.GREATER_THAN;
import static ibd.index.ComparisonTypes.LOWER_EQUAL_THAN;
import static ibd.index.ComparisonTypes.LOWER_THAN;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

/**
 * 
 * @author Samuel
 */
public class SamuelIndex implements Index, Iterable{
    
    public ArrayList<IndexRecordMap> index = new ArrayList<>();

    @Override
    public void clear() {
        index.clear();
    }

    @Override
    public void addEntry(Long blockId, Long recordId, Long primaryKey) {
        IndexRecord ir = new IndexRecord(blockId, recordId, primaryKey);
        index.add(new IndexRecordMap(primaryKey, ir));
        Collections.sort(index, new SortByPrimaryKey());
    }

    @Override
    public void removeEntry(Long primaryKey) {
        IndexRecordMap irmToRemove = null;
        for (Iterator<IndexRecordMap> it = index.iterator(); it.hasNext(); ) {
            IndexRecordMap irm = it.next();
            if(Objects.equals(irm.getPrimaryKey(), primaryKey)){
                irmToRemove = irm;
            }
        }
        if(irmToRemove!=null) index.remove(irmToRemove);      
    }

    @Override
    public IndexRecord getEntry(Long primaryKey) {
        for (Iterator<IndexRecordMap> it = index.iterator(); it.hasNext(); ) {
            IndexRecordMap irm = it.next();
            if(Objects.equals(irm.getPrimaryKey(), primaryKey)){
                return irm.getIndexRecord();
            }
        }
        return null;
    }

    @Override
    public List<IndexRecord> getEntries(Long primaryKey, int comparisonType) {
        ArrayList<IndexRecord> lista = new ArrayList<>();
  
        switch(comparisonType){
            case EQUAL:
                for(Iterator<IndexRecordMap> it = index.iterator(); it.hasNext(); ) {
                    IndexRecordMap irm = it.next();
                    if(Objects.equals(irm.getPrimaryKey(), primaryKey)){
                        lista.add(irm.getIndexRecord());
                    }
                }
                break;
            case DIFF:
                for(Iterator<IndexRecordMap> it = index.iterator(); it.hasNext(); ) {
                    IndexRecordMap irm = it.next();
                    if(!Objects.equals(irm.getPrimaryKey(), primaryKey)){
                        lista.add(irm.getIndexRecord());
                    } 
                }
                break;
            case GREATER_THAN:
                for(Iterator<IndexRecordMap> it = index.iterator(); it.hasNext(); ) {
                    IndexRecordMap irm = it.next();
                    if(irm.getPrimaryKey()>primaryKey){
                        lista.add(irm.getIndexRecord());
                    }  
                }
                break;
            case GREATER_EQUAL_THAN:
                for(Iterator<IndexRecordMap> it = index.iterator(); it.hasNext(); ) {
                    IndexRecordMap irm = it.next();
                    if(irm.getPrimaryKey()>=primaryKey){
                        lista.add(irm.getIndexRecord());
                    }
                }
                break;
            case LOWER_THAN:
                for(Iterator<IndexRecordMap> it = index.iterator(); it.hasNext(); ) {
                    IndexRecordMap irm = it.next();
                    if(irm.getPrimaryKey()<primaryKey){
                        lista.add(irm.getIndexRecord());
                    }else{
                        break;
                    }
                }
                break;
            case LOWER_EQUAL_THAN:
                for(Iterator<IndexRecordMap> it = index.iterator(); it.hasNext(); ) {
                    IndexRecordMap irm = it.next();
                    if(irm.getPrimaryKey()<=primaryKey){
                        lista.add(irm.getIndexRecord());
                    }else{
                        break;
                    }
                }
                break;
        }  
        return lista;
    }

    @Override
    public int getRecordsAmount() {
        return index.size();
    }

    @Override
    public Iterator iterator() {
        return new SamuelIndexIterator(this);
    }
    
    //Classe do iterator
    private class SamuelIndexIterator implements Iterator{
        Iterator it;
        
        public SamuelIndexIterator(SamuelIndex index){
            this.it = index.index.iterator();
        }
        
        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Object next() { 
            IndexRecordMap irm = (IndexRecordMap) it.next();
            return irm.getIndexRecord();
        }
    
    }
    
    //classe para simular um Map com chave e valor
    private class IndexRecordMap{
        Long primaryKey;
        IndexRecord indexRecord;

        public IndexRecordMap(Long primaryKey, IndexRecord indexRecord) {
            this.primaryKey = primaryKey;
            this.indexRecord = indexRecord;
        }

        public Long getPrimaryKey() {
            return primaryKey;
        }

        public IndexRecord getIndexRecord() {
            return indexRecord;
        }
        
    }
    
    //classe usada pelo Collections.sort() para ordenar o ArrayList pela PrimaryKey
    private class SortByPrimaryKey implements Comparator<IndexRecordMap>{
        @Override
        public int compare(IndexRecordMap a, IndexRecordMap b){
            return (int) (a.getPrimaryKey() - b.getPrimaryKey());
        }
    }
    
}
