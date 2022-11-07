//
// Arquitetura von Neumann Básica
// Prof. Luiz A. de P. Lima Jr.
// PSCF - PUCPR
//
// CPU
//

public class CPU {
    // Registradores da CPU
    private int PC = 0;     // program counter
    private int regA = 0;   // registrador A
    private int regB = 0;   // registrador B
    private int regC = 0;   // registrador C
    private final Memoria mem;
    private final IO es;

    public CPU(IO es, Memoria mem) {
        this.es = es;
        this.mem = mem;
    }
    public void Run(int ender) throws EnderecoInvalido {
        PC = ender;

        // lê "programa" da memória
        regA = mem.Read(PC++);
        regB = mem.Read(PC++);

        // roda o programa
        regC = 1;   // contador
        while (regA <= regB) {
            mem.Write(regA, regC);
            es.Output("> " + regA + " -> " + regC + "\n");
            ++regC;
            ++regA;
        }
    }
}
