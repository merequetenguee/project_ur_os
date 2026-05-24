/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ur_os.system;

import java.util.ArrayList;
import java.util.Random;
import ur_os.memory.Memory;
import ur_os.memory.MemoryInstruction;
import ur_os.memory.MemoryLoadType;
import ur_os.memory.MemoryManagerType;
import ur_os.memory.MemoryOperationType;
import ur_os.memory.freememorymagament.FreeMemorySlotManager;
import ur_os.process.CPUInstruction;
import ur_os.process.CPUInstructionType;
import ur_os.process.EndInstruction;
import ur_os.process.IOInstruction;
import ur_os.process.Instruction;
import ur_os.process.Process;
import ur_os.virtualmemory.SwapMemory;

/**
 *
 * @author super
 */
public class SystemOS implements Runnable{
    
    SimulationType simType;
    private static int clock = 0;
    private static final int MAX_SIM_CYCLES = 1000;
    private static final int MAX_SIM_PROC_CREATION_TIME = 50;
    private static final double PROB_PROC_CREATION = 0.1;
    public static final int MAX_PROC_SIZE = 1000;
    private static Random r = new Random(1235);
    private OS os;
    private CPU cpu;
    private IOQueue ioq;
    
    private Memory memory;
    private SwapMemory swap;
    
    
    public static final int SEED_SEGMENTS = 7401;
    public static final int SEED_PROCESS_SIZE = 9630;
    
    public static final int MEMORY_SIZE = 1_048_576; //1MB
    public static final int SWAP_MEMORY_SIZE = 1_073_741_824; //1 GB
    
    protected ArrayList<Process> processes;
    ArrayList<Integer> execution;

    public SystemOS(SimulationType simType) {
        memory = new Memory(MEMORY_SIZE);
        swap = new SwapMemory(MEMORY_SIZE);
        cpu = new CPU(memory,swap);
        ioq = new IOQueue();
        os = new OS(this, cpu, ioq);
        cpu.setOS(os);
        ioq.setOS(os);
        execution = new ArrayList();
        processes = new ArrayList();
        //initSimulationQueue();
        //initSimulationQueueSimple();
        initSimulationQueueSimpler();
       
         testMemoryInstruction();

        showProcesses();
        this.simType = simType;
    }
    
    public int getTime(){
        return clock;
    }
    
    public ArrayList<Process> getProcessAtI(int i){
        ArrayList<Process> ps = new ArrayList();
        
        for (Process process : processes) {
            if(process.getTime_init() == i){
                ps.add(process);
            }
        }
        
        return ps;
    }

    public void initSimulationQueue(){
        double tp;
        Process p;
        for (int i = 0; i < MAX_SIM_PROC_CREATION_TIME; i++) {
            tp = r.nextDouble();
            if(PROB_PROC_CREATION >= tp){
                p = new Process();
                p.setTime_init(clock);
                processes.add(p);
            }
            clock++;
        }
        clock = 0;
    }
    
    public void initSimulationQueueSimple(){
        Process p;
        int cont = 0;
        for (int i = 0; i < MAX_SIM_PROC_CREATION_TIME; i++) {
            if(i % 4 == 0){
                p = new Process(cont++,-1,true);
                p.setTime_init(clock);
                processes.add(p);
            }
            clock++;
        }
        clock = 0;
    }
    
    public void initSimulationQueueSimpler(){
        
        int tempSize;
        Process p = new Process(0,0);
        tempSize = r.nextInt(MAX_PROC_SIZE-1)+1;
        p.setSize(tempSize);
        Instruction temp;  
        p.addCPUInstructions(3);
        temp = new MemoryInstruction(MemoryOperationType.LOAD, MemoryLoadType.CONST, 0, (byte)42, 0, 4);
        p.addInstruction(temp);
        p.addCPUInstructions(3);
        temp = new EndInstruction();
        p.addInstruction(temp);
        processes.add(p);
        
        
        //Process 1
        p = new Process(1,2);
        tempSize = r.nextInt(MAX_PROC_SIZE-1)+1;
        p.setSize(tempSize);
        p.addCPUInstructions(3);
        //temp = new IOInstruction(5); 
        temp = new MemoryInstruction(MemoryOperationType.STORE, MemoryLoadType.MEM, r.nextInt(tempSize), (byte)0, 0, 3);
        p.addInstruction(temp);
        p.addCPUInstructions(3);
        temp = new EndInstruction();
        p.addInstruction(temp);
        processes.add(p);
        
        
        //Process 2
        p = new Process(2,6);
        tempSize = r.nextInt(MAX_PROC_SIZE-1)+1;
        p.setSize(tempSize);
        p.addCPUInstructions(7);
        //temp = new IOInstruction(3);    
        temp = new MemoryInstruction(MemoryOperationType.LOAD, MemoryLoadType.MEM, r.nextInt(tempSize), (byte)0, 1, 4);
        p.addInstruction(temp);
        p.addCPUInstructions(5);
        temp = new EndInstruction();
        p.addInstruction(temp);
        processes.add(p);
        
        //Process 3
        p = new Process(3,8);
        tempSize = r.nextInt(MAX_PROC_SIZE-1)+1;
        p.setSize(tempSize);
        p.addCPUInstructions(4);
        //temp = new IOInstruction(3);    
        temp = new MemoryInstruction(MemoryOperationType.STORE, MemoryLoadType.MEM, r.nextInt(tempSize), (byte)0, 1, 4);
        p.addCPUInstructions(7);
        temp = new EndInstruction();
        p.addInstruction(temp);
        processes.add(p);
        
        clock = 0;
    }
    
    public void initSimulationQueueSimpler3(){
        
        
        Process p = new Process(0,0);
        p.setSize(200);
        Instruction temp;
        p.addCPUInstructions(5);
        temp = new IOInstruction(4);    
        p.addInstruction(temp);
        p.addCPUInstructions(3);
        processes.add(p);
        
        
        
        //Process 1
        p = new Process(1,5);
        p.setSize(500);
        p.addCPUInstructions(13);
        temp = new IOInstruction(5);    
        p.addInstruction(temp);
        p.addCPUInstructions(16);
        processes.add(p);
        
        
        //Process 2
        p = new Process(2,6);
        p.setSize(250);
        p.addCPUInstructions(7);
        temp = new IOInstruction(3);    
        p.addInstruction(temp);
        p.addCPUInstructions(5);
        processes.add(p);
        
        
        //Process 3
        p = new Process(3,24);
        p.setSize(800);
        p.addCPUInstructions(4);
        temp = new IOInstruction(3);    
        p.addInstruction(temp);
        p.addCPUInstructions(7);
        processes.add(p);
        
        
        
        //Process 4
        p = new Process(4,31);
        p.setSize(600);
        p.addCPUInstructions(7);
        temp = new IOInstruction(3);    
        p.addInstruction(temp);
        p.addCPUInstructions(7);
        processes.add(p);
        
        
        
        clock = 0;
    }
    
    
    
    public void initSimulationQueueSimpler2(){
        
        Process p = new Process(false);
        Instruction temp;
        p.addCPUInstructions(15);
        temp = new IOInstruction(12);    
        p.addInstruction(temp);
        p.addCPUInstructions(21);
        p.setTime_init(0);
        p.setPid(0);
        processes.add(p);
        
        
        p = new Process(false);
        p.addCPUInstructions(8);
        temp = new IOInstruction(4);    
        p.addInstruction(temp);
        p.addCPUInstructions(16);
        p.setTime_init(2);
        p.setPid(1);
        processes.add(p);
        
        p = new Process(false);
        p.addCPUInstructions(10);
        temp = new IOInstruction(15);    
        p.addInstruction(temp);
        p.addCPUInstructions(12);
        p.setTime_init(6);
        p.setPid(2);
        processes.add(p);
        
        p = new Process(false);
        p.addCPUInstructions(9);
        temp = new IOInstruction(6);    
        p.addInstruction(temp);
        p.addCPUInstructions(17);
        p.setTime_init(8);
        p.setPid(3);
        processes.add(p);
        
        clock = 0;
    }
    
    public void testCPUInstruction() {
        System.out.println("=== TEST CPU INSTRUCTION ===");

        System.out.println("-- Prueba 1: ADD R0, R1 --");
        CPUInstruction add = new CPUInstruction(CPUInstructionType.ADD, 0, 1);
        System.out.println(add);
        System.out.println("OpType: " + add.getOpType());     
        System.out.println("R1: " + add.getR1());              
        System.out.println("R2: " + add.getR2());            
        System.out.println("Ciclos: " + add.getCycleNumber()); 

        
        System.out.println("-- Prueba 2: MUL R2, R3 --");
        CPUInstruction mul = new CPUInstruction(CPUInstructionType.MUL, 2, 3);
        System.out.println(mul);
        System.out.println("OpType: " + mul.getOpType());     
        System.out.println("Ciclos: " + mul.getCycleNumber()); 
        boolean mulCiclo1 = mul.advanceInstruction();
        System.out.println("Termino en ciclo 1: " + mulCiclo1); 
        boolean mulCiclo2 = mul.advanceInstruction();
        System.out.println("Termino en ciclo 2: " + mulCiclo2); 
        boolean mulCiclo3 = mul.advanceInstruction();
        System.out.println("Termino en ciclo 3: " + mulCiclo3); 
        System.out.println("-- Prueba 3: DIV R0, R1 --");
        CPUInstruction div = new CPUInstruction(CPUInstructionType.DIV, 0, 1);
        System.out.println(div);
        System.out.println("Ciclos: " + div.getCycleNumber());
        int ciclosDiv = 0;
        while (!div.advanceInstruction()) {
            ciclosDiv++;
        }
        ciclosDiv++;
        System.out.println("Ciclos ejecutados: " + ciclosDiv); 

        System.out.println("-- Prueba 4: MOV R0, 42 --");
        CPUInstruction mov = new CPUInstruction(CPUInstructionType.MOV, 0, (byte) 42);
        System.out.println(mov);
        System.out.println("OpType: " + mov.getOpType());      
        System.out.println("R1: " + mov.getR1());               
        System.out.println("R2: " + mov.getR2());               
        System.out.println("Constante: " + mov.getConstant());  

        System.out.println("-- Prueba 5: NOT R1 --");
        CPUInstruction not = new CPUInstruction(CPUInstructionType.NOT, 1, -1);
        System.out.println(not);
        System.out.println("OpType: " + not.getOpType()); 
        System.out.println("R1: " + not.getR1());          
        System.out.println("R2: " + not.getR2());         

        System.out.println("-- Prueba 6: AND R0, R1 --");
        CPUInstruction and = new CPUInstruction(CPUInstructionType.AND, 0, 1);
        System.out.println(and);
        System.out.println("OpType: " + and.getOpType()); 
        System.out.println("Ciclos: " + and.getCycleNumber()); 

        System.out.println("-- Prueba 7: Copia de MUL R2, R3 --");
        CPUInstruction mulCopia = new CPUInstruction(mul);
        System.out.println(mulCopia);
        System.out.println("OpType copia: " + mulCopia.getOpType());      
        System.out.println("R1 copia: " + mulCopia.getR1());              
        System.out.println("R2 copia: " + mulCopia.getR2());               
        System.out.println("Ciclos copia: " + mulCopia.getCycleNumber()); 

        System.out.println("-- Prueba 8: Constructor por defecto --");
        CPUInstruction def = new CPUInstruction();
        System.out.println(def);
        System.out.println("OpType: " + def.getOpType());
        System.out.println("R1: " + def.getR1());         
        System.out.println("R2: " + def.getR2());         
        System.out.println("Ciclos: " + def.getCycleNumber()); 

        System.out.println("=== FIN TEST ===");
    } 
    
    public void testMemoryInstruction() {
    System.out.println("=== TEST MEMORY INSTRUCTION ===");

    System.out.println("-- Test 1: LOAD CONST 42 -> R0 --");
    MemoryInstruction lc = new MemoryInstruction(MemoryOperationType.LOAD, MemoryLoadType.CONST, 0, (byte)42, 0, 3);
    System.out.println(lc);
    System.out.println("MType: " + lc.getMType() + " | LoadType: " + lc.getLoadType());
    System.out.println("Constante: " + lc.getContent() + " | Ri: R" + lc.getRi() + " | Ciclos: " + lc.getCycleNumber());

    System.out.println("-- Test 2: LOAD Mem[100] -> R1 --");
    MemoryInstruction lm = new MemoryInstruction(MemoryOperationType.LOAD, MemoryLoadType.MEM, 100, (byte)0, 1, 4);
    System.out.println(lm);
    System.out.println("MType: " + lm.getMType() + " | LoadType: " + lm.getLoadType());
    System.out.println("Direccion: " + lm.getLogicalAddress() + " | Ri: R" + lm.getRi() + " | Ciclos: " + lm.getCycleNumber());

    System.out.println("-- Test 3: STORE R2 -> Mem[200] --");
    MemoryInstruction st = new MemoryInstruction(MemoryOperationType.STORE, MemoryLoadType.MEM, 200, (byte)0, 2, 3);
    System.out.println(st);
    System.out.println("MType: " + st.getMType() + " | Ri: R" + st.getRi() + " | Direccion: " + st.getLogicalAddress());

    System.out.println("-- Test 4: Avanzar ciclos LOAD CONST --");
    MemoryInstruction mi = new MemoryInstruction(MemoryOperationType.LOAD, MemoryLoadType.CONST, 0, (byte)7, 0, 3);
    int ciclos = 0;
    while (!mi.advanceInstruction()) { ciclos++; }
    ciclos++;
    System.out.println("Ciclos ejecutados: " + ciclos); 

    System.out.println("-- Test 5: Copia de LOAD MEM --");
    MemoryInstruction copia = new MemoryInstruction(lm);
    System.out.println(copia);
    System.out.println("MType copia: " + copia.getMType() + " | LoadType copia: " + copia.getLoadType());

    System.out.println("=== FIN TEST MEMORY INSTRUCTION ===");
}
    
    public boolean isSimulationFinished(){
        
        boolean finished = true;
        
        for (Process p : processes) {
            finished = finished && p.isFinished();
        }
        
        return finished;
    
    }

    public SimulationType getSimulationType() {
        return simType;
    }
    
    public int getClock(){
        return clock;
    }
    
    @Override
    public void run() {
        double tp;
        ArrayList<Process> ps;
        
        System.out.println("******SIMULATION START******");
        
        int i=0;
        Process temp_exec;
        int tempID;
        while(!isSimulationFinished() && i < MAX_SIM_CYCLES){//MAX_SIM_CYCLES is the maximum simulation time, to avoid infinite loops
            System.out.println("******Clock: "+i+"******");
            
            if(i == 8){
                i = i;
            }
            
            if(this.getSimulationType() == SimulationType.ALL || this.getSimulationType() == SimulationType.PROCESS_PLANNING){
                System.out.println(cpu);
                System.out.println(ioq);
            }
            
            //Crear procesos, si aplica en el ciclo actual
            ps = getProcessAtI(i);
            for (Process p : ps) {
                os.create_process(p);
                System.out.println("Process Created: "+p.getPid()+"\n"+p);
                
                showFreeMemory();
            } //If the scheduler is preemtive, this action will trigger the extraction from the CPU, is any process is there.
            
            //Actualizar el OS, quien va actualizar el Scheduler            

            os.update();
            //os.update() prepares the system for execution. It runs at the beginning of the cycle.
            
                        
            clock++;
            
            temp_exec = cpu.getProcess();
            if(temp_exec == null){
                tempID = -1;
            }else{
                tempID = temp_exec.getPid();
            }
            execution.add(tempID);
            
            //Actualizar la CPU
            cpu.update();
            
            
            ///Actualizar la IO
            ioq.update();
            
            //Las actualizaciones de CPU y IO pueden generar interrupciones que actualizan a cola de listos, cuando salen los procesos
            
            if(this.getSimulationType() == SimulationType.ALL || this.getSimulationType() == SimulationType.PROCESS_PLANNING){
                System.out.println("After the cycle: ");
                System.out.println(cpu);
                System.out.println(ioq);
            }
            i++;

        }
        System.out.println("******SIMULATION FINISHES******");
        //os.showProcesses();
        
        System.out.println("******Process Execution******");
        for (Integer num : execution) {
            System.out.print(num+" ");
        }
        System.out.println("");
        
        System.out.println("******Performance Indicators******");
        System.out.println("Total execution cycles: "+clock);
        System.out.println("CPU Utilization: "+this.calcCPUUtilization());
        System.out.println("Throughput: "+this.calcThroughput());
        System.out.println("Average Turnaround Time: "+this.calcTurnaroundTime());
        System.out.println("Average Waiting Time: "+this.calcAvgWaitingTime());
        System.out.println("Average Context Switches: "+this.calcAvgContextSwitches());
        System.out.println("Average Response Time: "+this.calcAvgResponseTime());
        
        showProcesses();
        memory.showNotNullBytes();
        
        showFreeMemory();
    }
    
    public void showFreeMemory(){
        if(OS.SMM == MemoryManagerType.PAGING){
            System.out.println("Free frame number: "+os.fmm.getSize());
        }else{
            System.out.println("Free Memory Slots ("+os.fmm.getSize()+"): ");
            FreeMemorySlotManager msm = (FreeMemorySlotManager)os.fmm;
            System.out.println(msm);
        }
    }
    
    public void showProcesses(){
        System.out.println("Process list:");
        StringBuilder sb = new StringBuilder();
        
        for (Process process : processes) {
            sb.append(process);
            sb.append("\n");
        }
        
        System.out.println(sb.toString());
    }
    
    
    public double calcCPUUtilization(){
        int cont=0;
        for (Integer num : execution) {
            if(num == -1)
                cont++;
        }
        
        return (execution.size()-cont)/(double)execution.size();
    }
    
    public double calcTurnaroundTime(){
        
        double tot = 0;
        
        for (Process p : processes) {
            tot = tot + (p.getTime_finished() - p.getTime_init());
        }
        
        
        return tot/processes.size();
    }
    
    public double calcThroughput(){
        return (double)processes.size()/execution.size();
    }
    
    public double calcAvgWaitingTime(){
        double tot = 0;
        
        for (Process p : processes) {
            tot = tot + ((p.getTime_finished() - p.getTime_init()) - p.getTotalExecutionTime());
        }
        
        return tot/processes.size();
    }
    
    public double calcAvgContextSwitches(){
        int cont = 1;
        int prev = execution.get(0);
        for (Integer i : execution) {
            if(prev != i){
                cont++;
                prev = i;
            }
        }
        
        return cont / (double)processes.size();
    }

    public double calcAvgResponseTime(){
        
        double tot = 0;
        int temp = 0;
        for (Process p : processes) {
            temp = execution.indexOf(p.getPid());//On which cycle did the process started execution
            tot = tot + (temp - p.getTime_init());//Difference between execution start and arrival
        }
        
        return tot/processes.size();
    }
    
    
}
