package ur_os.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import ur_os.memory.Memory;
import ur_os.memory.MemoryInstruction;
import ur_os.memory.MemoryLoadType;
import ur_os.memory.SystemMemoryManager;
import ur_os.memory.freememorymagament.MemorySlot;
import ur_os.process.Process;
import ur_os.process.ProcessState;
import ur_os.virtualmemory.SwapMemory;

public class MemoryUnit {

    Memory memory;
    SwapMemory swap;
    protected ArrayList<Process> processes;
    SystemMemoryManager smm;
    CPU cpu;
    ArrayList<String> registerfamilies = new ArrayList<>(Arrays.asList("General Purpose", "Specific Purpose"));
    Map<String, ArrayList<Register>> registers;

    public MemoryUnit() {
        this(new Memory(SystemOS.MEMORY_SIZE), new SwapMemory(SystemOS.MEMORY_SIZE), null, null);
    }

    public MemoryUnit(Memory m, SwapMemory s, CPU cpu, SystemMemoryManager smm,
                      ArrayList<ArrayList<Register>> CoreRGroup) {
        memory = m;
        swap = s;
        processes = new ArrayList();
        this.cpu = cpu;
        this.smm = smm;
        registers = new HashMap<>();
        for (int i = 0; i < CoreRGroup.size(); i++) {
            registers.put(registerfamilies.get(i), CoreRGroup.get(i));
        }
        initGPRegisters();
        initSpecificRegisters();
    }

    public MemoryUnit(Memory m, SwapMemory s, CPU cpu, SystemMemoryManager smm) {
        memory = m;
        swap = s;
        processes = new ArrayList();
        this.cpu = cpu;
        this.smm = smm;
        registers = new HashMap<>();
        initGPRegisters();
        initSpecificRegisters();
    }

    // R0..R7 como registros RK: banco compartido entre CPUInstruction (r1/r2) y MemoryInstruction (ri)
    private void initGPRegisters() {
        ArrayList<Register> gp = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            gp.add(new Register((byte) 0, RegisterType.RK));
        }
        registers.put("General Purpose", gp);
    }

    // CR3: registro de control que guarda la base de la tabla de páginas del proceso activo
    private void initSpecificRegisters() {
        ArrayList<Register> sp = new ArrayList<>();
        sp.add(new Register((byte) 0, RegisterType.CR3));
        registers.put("Specific Purpose", sp);
    }

    public Register getGPRegister(int ri) {
        ArrayList<Register> gp = registers.get("General Purpose");
        if (gp != null && ri >= 0 && ri < gp.size()) {
            return gp.get(ri);
        }
        return null;
    }

    public Register getCR3Register() {
        ArrayList<Register> sp = registers.get("Specific Purpose");
        if (sp != null && !sp.isEmpty()) return sp.get(0);
        return null;
    }

    public void addProcess(Process p) {
        p.setState(ProcessState.NEW_MEMORY);
        processes.add(p);
    }

    public void update() {
        Process temp;
        MemoryInstruction mop;
        for (int i = 0; i < processes.size(); i++) {
            if (processes.get(i).getState() != ProcessState.NEW_MEMORY) {
                mop = (MemoryInstruction) processes.get(i).getCurrentInstruction();
                executeMemoryOperation(processes.get(i), mop);
                if (processes.get(i).advanceInstruction()) {
                    System.out.println("MEMORY OPERATION EXECUTION DONE");
                    temp = processes.get(i);
                    processes.remove(processes.get(i));
                    cpu.os.interrupt(InterruptType.MEMORY_DONE, temp);
                    i--;
                } else {
                    System.out.println("MEMORY OPERATION EXECUTION - " + mop.getRemainingCycles() + " ciclos restantes");
                }
            } else {
                processes.get(i).setState(ProcessState.MEMORY);
            }
        }
    }

    public void executeMemoryOperation(Process p, MemoryInstruction mop) {
        int logAdd, phyAdd;
        Register reg;
        if (mop != null) {
            System.out.println("Proceso " + p.getPid() + " ejecutando: " + mop);
            switch (mop.getMType()) {

                case LOAD:
                    if (mop.getLoadType() == MemoryLoadType.CONST) {
                       
                        reg = getGPRegister(mop.getRi());
                        if (reg != null) {
                            reg.setData(mop.getContent());
                            System.out.println("R" + mop.getRi() + " <- constante " + mop.getContent());
                        }
                    } else {
                     
                        logAdd = mop.getLogicalAddress();
                        phyAdd = smm.getPhysicalAddress(logAdd, p.getPMM());
                        byte loaded = load(phyAdd);
                        reg = getGPRegister(mop.getRi());
                        if (reg != null) {
                            reg.setData(loaded);
                            System.out.println("R" + mop.getRi() + " <- Mem[" + logAdd + "] (fis=" + phyAdd + ") = " + loaded);
                        }
                    }
                    break;

                case STORE:
                    
                    logAdd = mop.getLogicalAddress();
                    phyAdd = smm.getPhysicalAddress(logAdd, p.getPMM(), true);
                    reg = getGPRegister(mop.getRi());
                    byte toStore = (reg != null) ? reg.getData() : 0;
                    store(phyAdd, toStore);
                    System.out.println("Mem[" + logAdd + "] (fis=" + phyAdd + ") <- R" + mop.getRi() + " = " + toStore);
                    break;
            }
        }
    }

    public void removeProcess(Process p) {
        processes.remove(p);
    }

    @Override
    public String toString() {
        if (!processes.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("MU ");
            for (Process p : processes) {
                sb.append(p);
                sb.append("\n");
            }
            return sb.toString();
        } else {
            return "MU: Empty";
        }
    }

    public byte load(int physicalAddress) {
        byte b = memory.get(physicalAddress);
        System.out.println("Dato obtenido: " + b);
        return b;
    }

    public void store(int physicalAddress, byte content) {
        memory.set(physicalAddress, content);
        System.out.println("Dato " + memory.get(physicalAddress) + " guardado en: " + physicalAddress);
    }

    void loadSlot(MemorySlot m, MemorySlot vm) {
        int base = m.getBase();
        int vbase = vm.getBase();
        for (int i = 0; i < vm.getSize(); i++) {
            memory.set(base + i, swap.get(vbase + i));
        }
    }

    void storeSlot(MemorySlot m, MemorySlot vm) {
        int base = m.getBase();
        int vbase = vm.getBase();
        for (int i = 0; i < vm.getSize(); i++) {
            swap.set(vbase + i, memory.get(base + i));
        }
    }

    void loadPage(int frameMem, int frameSwap) {
        int base = frameMem * OS.PAGE_SIZE;
        int vbase = frameSwap * OS.PAGE_SIZE;
        for (int i = 0; i < OS.PAGE_SIZE; i++) {
            memory.set(base + i, swap.get(vbase + i));
        }
    }

    void storePage(int frameMem, int frameSwap) {
        int base = frameMem * OS.PAGE_SIZE;
        int vbase = frameSwap * OS.PAGE_SIZE;
        for (int i = 0; i < OS.PAGE_SIZE; i++) {
            swap.set(vbase + i, memory.get(base + i));
        }
    }

    void setSMM(SystemMemoryManager smm) {
        this.smm = smm;
    }
}
