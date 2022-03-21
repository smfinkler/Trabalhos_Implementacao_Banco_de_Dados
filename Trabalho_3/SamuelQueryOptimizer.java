
package ibd.query;

import ibd.query.binaryop.BinaryOperation;
import ibd.query.sourceop.SourceOperation;
import ibd.query.unaryop.PKFilter;
import ibd.query.unaryop.UnaryOperation;
import java.util.LinkedList;
/**
 *
 * @author Samuel
 */
public class SamuelQueryOptimizer {
    
    static LinkedList<UnaryOperation> list = new LinkedList<>();
    static SourceOperation sourceOp = null;
    static Operation op2 = null;
    static int leftRight;
    static int contUnary = 0;
    static int contBinary = 0;
    
    public Operation pushDownFilters(Operation op) throws Exception{
        contaOps(op);
        if(contBinary>0){
            otimizaConsulta(op);
            op = op2;
        }
        return op;
    }
    
    public static void contaOps(Operation op){      
        if (op instanceof UnaryOperation){
            UnaryOperation uop = (UnaryOperation) op;
            contaOps(uop.getOperation());
            contUnary++;
        }else if (op instanceof BinaryOperation){
            BinaryOperation bop = (BinaryOperation) op;
            contaOps(bop.getLeftOperation());
            contaOps(bop.getRigthOperation());
            contBinary++;
        }  
    }
    
    public static void encontraSource(Operation op, PKFilter pkfilter, int tab, int lado){        
        if (op instanceof UnaryOperation){
            UnaryOperation uop = (UnaryOperation) op;
            encontraSource(uop.getOperation(), pkfilter, tab+4, lado);
            
        }else if (op instanceof BinaryOperation){
            BinaryOperation bop = (BinaryOperation) op;
            encontraSource(bop.getLeftOperation(), pkfilter, tab+4, 1);
            encontraSource(bop.getRigthOperation(), pkfilter, tab+4, 2);
            
        }else if (op instanceof SourceOperation){
            SourceOperation sop = (SourceOperation) op;
            if(sop.getSource().equals(pkfilter.getSourceName())){
                leftRight=lado;
                sourceOp = sop;  
            }            
        }
    }
    
    public static void otimizaConsulta(Operation op) throws Exception{
        
        boolean testada = false;
        
            if (op instanceof UnaryOperation){
                UnaryOperation uop = (UnaryOperation) op;
                
                if(uop.getOperation() instanceof SourceOperation){
                    testada=true;
                    op2 = op.getParentOperation();
                    while(op2.getParentOperation() instanceof BinaryOperation){
                        op2 = op2.getParentOperation();
                    }
                }
                
                if(list.size()>=contUnary){
                    testada=true;
                }

                for (UnaryOperation it : list) {
                    if(uop==it){
                        testada = true;
                    } 
                }

                if(testada==false){
                    boolean aux = false;
                    list.add(uop);
                    if(uop.getParentOperation() instanceof BinaryOperation){
                        aux = true;
                        op2 = uop.getParentOperation();
                    }else{
                        op2 = uop.getOperation();
                    }
                    
                    PKFilter pkfilter = (PKFilter) uop;
                    encontraSource(op2, pkfilter, 0, 0);

                    if(sourceOp.getSource().equals(pkfilter.getSourceName())){
                        UnaryOperation tempPaiUop = null;
                        BinaryOperation tempPaiBop = null;

                        if(sourceOp.getParentOperation() instanceof UnaryOperation){
                            if(aux){
                                Operation auxOp = uop.getOperation();
                                BinaryOperation auxBop = (BinaryOperation) op2;
                                if(leftRight==1){
                                    auxBop.setLeftOperation(auxOp);
                                }else if(leftRight==2){
                                    auxBop.setLeftOperation(auxOp);
                                }
                                
                            }
                            tempPaiUop = (UnaryOperation) sourceOp.getParentOperation();
                            tempPaiUop.setOperation(uop);
                            uop.setOperation(sourceOp);                           

                        }else if(sourceOp.getParentOperation() instanceof BinaryOperation){
                            
                            if(aux){
                                Operation auxOp = uop.getOperation();
                                BinaryOperation auxBop = (BinaryOperation) op2;
                                if(leftRight==1){
                                    auxBop.setLeftOperation(auxOp);
                                }else if(leftRight==2){
                                    auxBop.setLeftOperation(auxOp);
                                }
                                
                            }
                            
                            tempPaiBop = (BinaryOperation) sourceOp.getParentOperation();
                            if(leftRight==1){
                                tempPaiBop.setLeftOperation(uop);
                            }else if(leftRight==2){
                                tempPaiBop.setRightOperation(uop);
                            }
                            uop.setOperation(sourceOp);
                        }
                    } 
                }           

                if(!testada){
                    otimizaConsulta(op2); 
                }else{
                    otimizaConsulta(uop.getOperation());
                }
                
            }else if (op instanceof BinaryOperation){
                BinaryOperation bop = (BinaryOperation) op;
                otimizaConsulta(bop.getLeftOperation());
                otimizaConsulta(bop.getRigthOperation());
            }         
    }
}