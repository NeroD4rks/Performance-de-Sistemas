public class IO {
    private final java.io.PrintStream saida;
    public IO(java.io.PrintStream out) {
        saida = out;
    }
    public void Output(String s) {
        saida.print(s);
    }
}
