// [REEMPLAZAR TODO] Reemplazar el contenido completo de este archivo
package ur_os.memory;

import java.util.Random;
import ur_os.process.Instruction;
import ur_os.process.ProcessInstructionType;

public class MemoryInstruction extends Instruction {

    MemoryOperationType mtype;  
   
    MemoryLoadType loadType;
    int logicalAddress;          
    byte content;                
                                

    int ri;

    
    public MemoryInstruction() {
        this(MemoryOperationType.LOAD, MemoryLoadType.CONST, 0, (byte) 0, 0, 3);
    }

    
    public MemoryInstruction(MemoryOperationType mtype, MemoryLoadType loadType,
                             int logicalAddress, byte content, int ri) {
        this(mtype, loadType, logicalAddress, content, ri, 3);
    }

   
    public MemoryInstruction(MemoryOperationType mtype, MemoryLoadType loadType,
                             int logicalAddress, byte content, int ri, boolean rand) {
        this(mtype, loadType, logicalAddress, content, ri, 0);
        if (rand) {
            Random r = new Random();
            this.cycleNumber = r.nextInt(10);
            this.remainingCycles = this.cycleNumber;
        }
    }


    public MemoryInstruction(MemoryOperationType mtype, MemoryLoadType loadType,
                             int logicalAddress, byte content, int ri, int cycleNumber) {
        super(ProcessInstructionType.MEMORY, cycleNumber);
        this.mtype = mtype;
        this.loadType = loadType;      
        this.logicalAddress = logicalAddress;
        this.content = content;
        this.ri = ri;                  
    }

   
    public MemoryInstruction(Instruction i) {
        this();
        if (i instanceof MemoryInstruction) {
            MemoryInstruction m = (MemoryInstruction) i;
            this.type = m.type;
            this.cycleNumber = m.cycleNumber;
            this.remainingCycles = m.remainingCycles;
            this.mtype = m.mtype;
            this.loadType = m.loadType;        
            this.logicalAddress = m.logicalAddress;
            this.content = m.content;
            this.ri = m.ri;                    
        }
    }

    
    public MemoryOperationType getMType() { return mtype; }
    public void setMType(MemoryOperationType mtype) { this.mtype = mtype; }

  
    public MemoryLoadType getLoadType() { return loadType; }
    public void setLoadType(MemoryLoadType loadType) { this.loadType = loadType; }

    public int getLogicalAddress() { return logicalAddress; }
    public void setLogicalAddress(int logicalAddress) { this.logicalAddress = logicalAddress; }

    public byte getContent() { return content; }
    public void setContent(byte content) { this.content = content; }

    
    public int getRi() { return ri; }
    public void setRi(int ri) { this.ri = ri; }

  
    @Override
    public String toString() {
        switch (mtype) {
            case LOAD:
                if (loadType == MemoryLoadType.CONST)
                    return super.toString() + " LOAD CONST " + content + " -> R" + ri;
                else
                    return super.toString() + " LOAD Mem[" + logicalAddress + "] -> R" + ri;
            case STORE:
                return super.toString() + " STORE R" + ri + " -> Mem[" + logicalAddress + "]";
            default:
                return super.toString() + " " + mtype;
        }
    }
}