/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package santaclaus;

/**
 *
 * @author PAUL
 */
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import static java.lang.System.out;

public class SantaClaus {

    /**
     * @param args the command line arguments
     */
   
    private volatile boolean niñoscreenenSanta = true;
    private final Semaphore disbelief = new Semaphore(0);
    private final static int END_OF_FAITH = 2020;
    private AtomicInteger ANIO = new AtomicInteger(2014);
    private static Random generaDOR = new Random();


    private final static int NUMERORENOS = 9;
    private final static int NUMERODUENDES = 10;
    private final static int GRUPODUENDES = 3;

  
    private final Semaphore queueDUENDES;
    private final CyclicBarrier tresDuendes;
    private final CyclicBarrier duendesInspirados;
    private final CyclicBarrier todoslosRenos;
    private final CyclicBarrier trineo;
    private final Semaphore ayudaSanta;
    private final static int ULTIMO_RENO = 0;    // compares to CyclicBarrier.await()
    private final static int Tercerduende = 0; 
    
    class Reno implements Runnable {
        int id;

        Reno(int id) { this.id = id; }

        public void run() {
            while (niñoscreenenSanta) {
                try {
                  
                    Thread.sleep(900 + generaDOR.nextInt(200));

                    
                    int reno = todoslosRenos.await();
                    
                    if (reno == ULTIMO_RENO) {
                        ayudaSanta.acquire();
                       
                        if (ANIO.incrementAndGet() == END_OF_FAITH)
                        {
                            niñoscreenenSanta = false;
                            disbelief.release();
                        }
                    }

                   
                    trineo.await();
                    Thread.sleep(generaDOR.nextInt(20)); 
                    reno = trineo.await();
                    if (reno == ULTIMO_RENO) {
                        ayudaSanta.release();
                        out.println("=== Los juguetes se entregan ===");
                    }
                } catch (InterruptedException e) {
    
                } catch (BrokenBarrierException e) {
                   
                }
            }
            ///out.println("RENO " + id + " se va de vacaciones");
        }
    }

    class Duende implements Runnable {
        int id;

        Duende(int id) { this.id = id; }

        public void run() {
            try {
                Thread.sleep(generaDOR.nextInt(2000));

                while (niñoscreenenSanta) {
                    queueDUENDES.acquire();
                    out.println("duende " + id + " tiene problemas");

                    int duende = tresDuendes.await();

                    if (duende == Tercerduende)
                        ayudaSanta.acquire();

                    Thread.sleep(generaDOR.nextInt(500));
                    out.println("duende " + id + " se retira");
                    duendesInspirados.await();

                    if (duende == Tercerduende)
                        ayudaSanta.release();

                    queueDUENDES.release();

                    Thread.sleep(generaDOR.nextInt(2000));
                }
            } catch (InterruptedException e) {

            } catch (BrokenBarrierException e) {

            }
            ///out.println("duende " + id + " se retira");
        }
    }

    class BarrierMessage implements Runnable {
        String msg;
        BarrierMessage(String msg) { this.msg = msg; }
        public void run() {
            out.println(msg);
        }
    }

    class Harnessing implements Runnable {
        boolean isSleighAttached;
        Harnessing() { isSleighAttached = false; }
        public void run() {
            isSleighAttached = !isSleighAttached;
            if (isSleighAttached)
                out.println("=== lllego el ultimo reno y despierta a santa ===");
            else
                out.println("=== santa prepara trineo  ===");
        }
    }

    public SantaClaus() {
        
        ayudaSanta = new Semaphore(1, true);
        queueDUENDES = new Semaphore(GRUPODUENDES, true);    
        tresDuendes = new CyclicBarrier(GRUPODUENDES,
                new BarrierMessage("--- " + GRUPODUENDES + " los duendes despiertan a santa y piden ayuda ---"));
        duendesInspirados = new CyclicBarrier(GRUPODUENDES,
                new BarrierMessage("--- santa vuelve a dormir ---"));
        todoslosRenos = new CyclicBarrier(NUMERORENOS, new Runnable() {
            public void run() {
                out.println("=== los renos empiezan a llegar ");
            }});
        trineo = new CyclicBarrier(NUMERORENOS, new Harnessing());

        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < NUMERODUENDES; ++i)
            threads.add(new Thread(new Duende(i)));
        for (int i = 0; i < NUMERORENOS; ++i)
            threads.add(new Thread(new Reno(i)));
        out.println("en el año " + ANIO + " :");
        for (Thread t : threads)
            t.start();

        try {
            disbelief.acquire();
   
            for (Thread t : threads)
                t.interrupt();
            for (Thread t : threads)
                t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        out.println("santa duerme");
    }

    
    public static void main(String[] args) {
  
        ventana v1 = new ventana();
        v1.setVisible(true);
        new SantaClaus();
    }
    
}
