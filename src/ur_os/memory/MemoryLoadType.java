package ur_os.memory;

public enum MemoryLoadType {
    CONST,  // LOAD CONST valor -> Ri  (carga un valor inmediato al registro)
    MEM     // LOAD Mem[dir]   -> Ri  (carga desde dirección de memoria al registro)
}