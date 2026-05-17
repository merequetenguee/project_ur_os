/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ur_os.process;

public enum CPUInstructionType {
    //Arithmetic
    ADD(1),
    SUB(1),
    MUL(3),
    DIV(4),
    MOV(1),
    //Logical
    AND(1),
    OR(1),
    XOR(1),
    NOT(1);

    private final int cycles;

    CPUInstructionType(int cycles) {
        this.cycles = cycles;
    }

    public int getCycles() {
        return cycles;
    }
}
