/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.process;

public class CPUInstruction extends Instruction {

    private CPUInstructionType opType;  
    private int r1;                    
    private int r2;                     
    private byte constant;             

    // Default one
    public CPUInstruction() {
        super(ProcessInstructionType.CPU, 1);
        this.opType = CPUInstructionType.ADD;
        this.r1 = 0;
        this.r2 = 1;
        this.constant = 0;
    }

    // Operations between two registers
    public CPUInstruction(CPUInstructionType opType, int r1, int r2) {
        super(ProcessInstructionType.CPU, opType.getCycles()); 
        this.opType = opType;
        this.r1 = r1;
        this.r2 = r2;
        this.constant = 0;
    }

    // Constant 
    public CPUInstruction(CPUInstructionType opType, int r1, byte constant) {
        super(ProcessInstructionType.CPU, opType.getCycles());
        this.opType = opType;
        this.r1 = r1;
        this.r2 = -1;      
        this.constant = constant;
    }

    // Update constructor
    public CPUInstruction(Instruction i) {
        this();
        if (i instanceof CPUInstruction) {
            CPUInstruction m = (CPUInstruction) i;
            this.type = m.type;
            this.cycleNumber = m.cycleNumber;
            this.remainingCycles = m.remainingCycles;
            this.opType = m.opType;
            this.r1 = m.r1;
            this.r2 = m.r2;
            this.constant = m.constant;
        }
    }
    // Getters
    public CPUInstructionType getOpType() { 
        return opType; 
    }
    
    public int getR1() { 
        return r1; 
    }
    
    public int getR2() { 
        return r2; 
    }
    
    public byte getConstant() { 
        return constant; 
    }

    @Override
    public String toString() {

        switch(opType){

            case MOV:
                return super.toString() + " " + opType +
                       " R" + r1 + ", " + constant;

            case NOT:
                return super.toString() + " " + opType +
                       " R" + r1;

            default:
                return super.toString() + " " + opType +
                       " R" + r1 + ", R" + r2;
        }
    }
}

