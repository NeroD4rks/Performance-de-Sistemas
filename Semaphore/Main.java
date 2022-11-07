import java.util.Random;
import java.util.concurrent.Semaphore;

class ControlAcess {
    private int acessos = 0;
    private final Semaphore mutex = new Semaphore(1);

    public void lock(Semaphore semaphore) {
        mutex.acquireUninterruptibly();
        // Zona de Risco tbm
        ++acessos;
        /* Se for uma outra empresa acessando pela primeira vez, vai entrar no if
         e vai aguardar até a liberação da outra */
        if (acessos == 1)semaphore.acquireUninterruptibly();
        // Fim da Zona de Risco
        mutex.release();
    }

    public void unlock(Semaphore semaphore) {
        mutex.acquireUninterruptibly();
        --acessos;
        /*
        * Como cada acesso é registrado, aqui diminui a cada liberação.
        *  Então quando todos de uma empresa sinalizarem que finalizou, ele libera para a outra empresa
        * */
        if (acessos == 0) semaphore.release();
        mutex.release();
    }
}

class Empresa implements Runnable {
    private String id;
    private float tempo;
    private String empresa;
    private Semaphore s;
    private Semaphore empty;
    private ControlAcess controlAcess;

    public Empresa(String id, String empresa, float tempo, Semaphore s, Semaphore empty, ControlAcess controlAcess) {
        this.id = id;
        this.empresa = empresa;
        this.tempo = tempo;
        this.empty = empty;
        this.s = s;
        this.controlAcess = controlAcess;
    }

    public void up(Semaphore s) {
        s.release();
    }

    public void down(Semaphore s) {
        try {
            s.acquire();
        } catch (InterruptedException ignored) {
        }
    }

    public void sleep(float segs) {
        try {
            Thread.sleep((long) (segs * 1_000));
        } catch (InterruptedException ignored) {
        }
    }

    private void realizaAcao() {
        try {
            Random rand = new Random();
            sleep(rand.nextFloat((10 - 5) + 1) + 5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void regiaoCritica() {
        System.out.println("+ F" + id + " [" + empresa + "] acessou");
        realizaAcao();
        System.out.println("- F" + id + " [" + empresa + "] terminou acesso");
    }

    @Override
    public void run() {
        sleep(tempo);
        System.out.println("F" + id + " [" + empresa + "] tentando acesso");
        controlAcess.lock(empty);
        down(this.s);
        regiaoCritica();
        up(this.s);
        controlAcess.unlock(empty);
    }

}

public class Main {


    public static void main(String[] args) throws InterruptedException {
        int total_por_vez = 3;
        int N = 10;
        Random rand = new Random();
        int max = 10;
        int min = 1;
        int i;
        Thread[] funcionarioA = new Thread[N];
        Thread[] funcionarioB = new Thread[N];
        Semaphore s_empty = new Semaphore(1);
        Semaphore s_a = new Semaphore(total_por_vez);
        Semaphore s_b = new Semaphore(total_por_vez);
        ControlAcess a = new ControlAcess();
        ControlAcess b = new ControlAcess();
        for (i = 0; i < N; i++) {
            funcionarioA[i] = new Thread(new Empresa(String.valueOf(i + 1), "A", rand.nextFloat((max - min) + 1) + min, s_a, s_empty, a));
            funcionarioB[i] = new Thread(new Empresa(String.valueOf(i + 1), "B", rand.nextFloat((max - min) + 1) + min, s_b, s_empty, b));
        }
        for (i = 0; i < N; ++i) {
            funcionarioA[i].start();
            funcionarioB[i].start();
        }
    }

}
