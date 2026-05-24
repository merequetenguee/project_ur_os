
package ur_os.memory;

import java.util.LinkedList;
import java.util.Random;

public class MemoryOperationList {
    LinkedList<MemoryInstruction> mol;
    Random r;
    public static final int MAX_SIZE_SIMPLE_LIST = 10;

    public MemoryOperationList() {
        mol = new LinkedList();
        r = new Random();
    }

    
    public void generateSimpleMemoryOperations(int processSize) {
        MemoryOperationType mtype;
        MemoryLoadType loadType;  
        byte b;
        int ri;                   

        for (int i = 0; i < MAX_SIZE_SIMPLE_LIST; i++) {
            int pick = Math.abs(r.nextInt() % 3);
            if (pick == 0) {
                mtype = MemoryOperationType.LOAD;
                loadType = MemoryLoadType.CONST;
                b = (byte)(r.nextInt() % 128);  
            } else if (pick == 1) {
                mtype = MemoryOperationType.LOAD;
                loadType = MemoryLoadType.MEM;
                b = 0;
            } else {
                mtype = MemoryOperationType.STORE;
                loadType = MemoryLoadType.MEM;  
                b = 0;
            }
            ri = Math.abs(r.nextInt() % 8);  
            mol.add(new MemoryInstruction(mtype, loadType, r.nextInt(processSize), b, ri));
        }
    }

    
    public void add(MemoryInstruction m) { mol.add(m); }

    public MemoryInstruction getNext() {
        if (mol.size() > 0) return mol.remove();
        else return null;
    }

    public int getSize() { return mol.size(); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (MemoryInstruction mi : mol) {
            sb.append(mi.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
