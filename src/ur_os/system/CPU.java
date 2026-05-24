/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ur_os.system;

import java.util.ArrayList;
import ur_os.memory.Memory;
import ur_os.memory.freememorymagament.MemorySlot;
import ur_os.process.CPUInstruction;
import ur_os.process.Instruction;
import ur_os.process.Process;
import ur_os.process.ProcessState;
import ur_os.virtualmemory.SwapMemory;


/**
 *
 * @author super
 */
public class CPU {
    
    Process p;
    OS os;
    MemoryUnit mu;

    
    public CPU(){
        this(null,null,null);
    }
    
    public CPU(OS os, Memory m, SwapMemory s, ArrayList<ArrayList<Register>> RegistersGroups){
        this.os = os;
        if(os != null)
            this.mu = new MemoryUnit(m,s,this,os.smm,RegistersGroups);
        else
            this.mu = new MemoryUnit(m,s,this,null,RegistersGroups);
    }
    public CPU(OS os, Memory m, SwapMemory s){
        this.os = os;
        if(os != null)
            this.mu = new MemoryUnit(m,s,this,os.smm);
        else
            this.mu = new MemoryUnit(m,s,this,null);
    }
    
    public CPU(Memory m, SwapMemory s){
        this(null,m,s);
    }
    
    public void setOS(OS os){
        this.os = os;
        mu.setSMM(os.smm);
    }
    
    public void addProcess(Process p){
        this.p = p;
        p.setState(ProcessState.CPU);
    }
    
    public Process getProcess(){
        return p;
    }
    
    public boolean isEmpty(){
        return p == null;
    }
    
    public void update(){
        if(!isEmpty())
            advanceInstruction();
        mu.update();
    }
    
    public MemoryUnit getMemoryUnit(){
        return mu;
    }
    
    public void addProcessToMemoryUnit(Process p){
        mu.addProcess(p);
    }
    
    public void advanceInstruction(){
        
        Instruction i = p.getCurrentInstruction();
        p.advanceInstruction();
        Process tempp;
        switch(i.getType()){
            case MEMORY:
                //executeMemoryOperation((MemoryInstruction) i);
                System.out.println("Executing Memory instruction");
                tempp = removeProcess();
                os.interrupt(InterruptType.CPU_TO_MEMORY, tempp);
                break;
                
            case IO:
                System.out.println("Executing IO instruction");
                tempp = removeProcess();
                os.interrupt(InterruptType.CPU_TO_IO, tempp);
                break;
                
            case CPU:
                executeCPUOperation((CPUInstruction) i);
                break;
            
            case END:
                tempp = removeProcess();
                os.interrupt(InterruptType.FINISH_PROCESS, tempp);
                break;
        }
        
        
    }
    
    public void interrupt(InterruptType t, Process p, Instruction i){
        os.interrupt(t, p, i);
    }
    
    public void executeCPUOperation(CPUInstruction i){
        Register reg1 = mu.getGPRegister(i.getR1());
        if (reg1 == null) return;

        byte val1 = reg1.getData();
        byte val2 = (i.getR2() == -1)
                ? i.getConstant()
                : (mu.getGPRegister(i.getR2()) != null ? mu.getGPRegister(i.getR2()).getData() : 0);

        byte result;
        switch (i.getOpType()) {
            case ADD: reg1.setData((byte)(val1 + val2)); break;
            case SUB: reg1.setData((byte)(val1 - val2)); break;
            case MUL: reg1.setData((byte)(val1 * val2)); break;
            case DIV: reg1.setData(val2 != 0 ? (byte)(val1 / val2) : (byte)0); break;
            case MOV: reg1.setData(val2); break;
            case AND: reg1.setData((byte)(val1 & val2)); break;
            case OR:  reg1.setData((byte)(val1 | val2)); break;
            case XOR: reg1.setData((byte)(val1 ^ val2)); break;
            case NOT: reg1.setData((byte)(~val1)); break;
        }
        System.out.println("Ejecutando CPU: " + i + " | R" + i.getR1() + " = " + reg1.getData());
    }
    
    public Process removeProcess(){
        Process t = p;
        p = null;
        return t;
    }
    
    public Process extractProcess(){
        Process temp = p;
        p = null;
        return temp;
    }
    
    
    public String toString(){
        if(!isEmpty())
            return "CPU: "+p.toString();
        else
            return "CPU: Empty";
    }

    void loadSlot(MemorySlot m, MemorySlot vm) {
        mu.loadSlot(m,vm);
    }
    
    void storeSlot(MemorySlot m, MemorySlot vm) {
        mu.storeSlot(m,vm);
    }
    
    void loadPage(int frameMem, int frameSwap) {
        mu.loadPage(frameMem,frameSwap); //Bring the page from frame frameSwap in swapt to frame frameMem in memory
    }
    
    void storePage(int frameMem, int frameSwap) {
        mu.storePage(frameMem,frameSwap); //Send the page from frame frameMem in memory to frameSwap in swapt
    }
    
   
    
}
