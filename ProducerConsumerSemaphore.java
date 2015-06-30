package ConcurrentProgramming;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerSemaphore {
    public static void main(String[] args) {
        System.out.println("OUTPUT:");
        System.out.println("RUN:");

        MeetingRoom m = new MeetingRoom(2);

        Supplier s1 = new Supplier(m, 1);
        Supplier s2 = new Supplier(m, 2);
        Supplier s3 = new Supplier(m, 3);
        Supplier s4 = new Supplier(m, 4);
        Supplier s5 = new Supplier(m, 5);
        Supplier s6 = new Supplier(m, 6);
        Supplier s7 = new Supplier(m, 7);
        Supplier s8 = new Supplier(m, 8);
        Supplier s9 = new Supplier(m, 9);
        Supplier s10 = new Supplier(m, 10);

        Consumer c1 = new Consumer(m, 1);
        Consumer c2 = new Consumer(m, 2);
        Consumer c3 = new Consumer(m, 3);
        Consumer c4 = new Consumer(m, 4);
        Consumer c5 = new Consumer(m, 5);
        Consumer c6 = new Consumer(m, 6);
        Consumer c7 = new Consumer(m, 7);
        Consumer c8 = new Consumer(m, 8);
        Consumer c9 = new Consumer(m, 9);
        Consumer c10 = new Consumer(m, 10);

        s1.start();
        c1.start();
        s2.start();
        c2.start();
        s3.start();
        c3.start();
        s4.start();
        c4.start();
        s5.start();
        c5.start();
        s6.start();
        c6.start();
        s7.start();
        c7.start();
        s8.start();
        c8.start();
        s9.start();
        c9.start();
        s10.start();
        c10.start();


    }
}

class Container<V> {
    V data;
    int id;
    private final Lock lock = new ReentrantLock();

    public Container() {
    }

    public synchronized V getData() {
        return this.data;
    }

    public synchronized void setData(V data) {
        lock.lock();
        this.data = data;
        lock.unlock();
    }

    public synchronized int getId() {
        return this.id;
    }

    public void setId(int id) {
        lock.lock();
        this.id = id;
        lock.unlock();
    }

    public boolean isEmpty() {
        return (data == null);
    }
}

class MeetingRoom {
    int data;
    int N = 0;
    int occupants = 0;
    Semaphore sem = new Semaphore(N);

    Container<Integer> c1 = new Container<Integer>();
    Container<Integer> c2 = new Container<Integer>();

    boolean isEmpty1 = true;
    boolean isEmpty2 = true;

    public MeetingRoom(int N) {
        sem = new Semaphore(N);
    }


    public synchronized int get (int id) {
        while (id != c1.getId() && id != c2.getId()) {
            try {
                System.out.println("Consumer " + id + " enters waiting room.");
                wait();
            } catch (InterruptedException e) { }
        }

        if ((id == c1.getId() || id == c2.getId()) && (occupants <= N)) {
            if (id == c1.getId()) {
                occupants++;
                System.out.println("Consumer " + id + " enters meeting room / removes random data from container one: " + c1.getData() + ".");

                try {
                    data = c1.getData();
                } catch (NullPointerException n) { }

                c1.setData(null);
                notifyAll();
                occupants--;
            }
            else if (id == c2.getId()) {
                occupants++;
                System.out.println("Consumer " + id + " enters meeting room / removes random data from container two: " + c1.getData() + ".");

                try {
                    data = c2.getData();
                } catch (NullPointerException n) { }

                c2.setData(null);
                notifyAll();
                occupants--;
            }
        }
        return data;
    }

    public synchronized void put(int value, int id) {
        while (!c1.isEmpty() && !c2.isEmpty()) {
            try {
                System.out.println("Supplier " + id + " enters waiting room.");
                wait();
            }
            catch (InterruptedException e) {
            }
            finally {
            }
        }

        isEmpty1 = c1.isEmpty();
        isEmpty2 = c2.isEmpty();

        if((isEmpty1 || isEmpty2) && (occupants <= N)) {
            if (isEmpty1) {
                try {
                    occupants++;
                    sem.acquire();
                    System.out.println("Supplier " + id + " enters meeting room/leaves data for consumer #" + id + "in CONTAINER ONE.");
                    c1.setData(value);
                    c1.setId(id);
                    isEmpty1 = false;
                    notifyAll();
                    occupants--;
                } catch (InterruptedException e) {}
                finally {
                    sem.release();
                }
            }
            else {
                try {
                    occupants++;
                    sem.acquire();
                    System.out.println("Supplier " + id + " enters meeting room/leaves data for consumer #" + id + " in CONTAINER TWO.");
                    c2.setData(value);
                    c2.setId(id);
                    isEmpty2 = false;
                    notifyAll();
                    occupants--;
                } catch (InterruptedException e) {}
                finally {
                    sem.release();
                }
            }
        }
    }

}

class Consumer extends Thread {
    private MeetingRoom meetingRoom;
    private int id;
    public Consumer(MeetingRoom m, int id) {
        meetingRoom = m;
        this.id = id;
    }
    public void run() {
        int value = 0;
        for(int i=0; i < 2; i++){
            try{
                int randomTime = (int)(Math.random() *1000);
                sleep(randomTime);
                System.out.println("Supplier " + id + " sleeps for " + randomTime + " milliseconds.");
            } catch (InterruptedException e) {}
            value = meetingRoom.get(id);
            System.out.println("Consumer #" + this.id + " got: " + value);
        }
    }
}

class Supplier extends Thread {
    private MeetingRoom meetingRoom;
    private int id;

    public Supplier(MeetingRoom m, int id) {
        meetingRoom = m;
        this.id = id;
    }

    public void run() {
        Random randomGenerator = new Random();

        for(int i=0; i < 2; i++) {
            try{
                int randomTime = (int)(Math.random() *1000);
                sleep(randomTime);
             //   System.out.println("Supplier " + id + " sleeps for " + randomTime + " milliseconds.");
            } catch (InterruptedException e) {

            }

            int randomInt = randomGenerator.nextInt(500);
            System.out.println("Supplier #" + this.id + " produces random data: " + randomInt +".");
            meetingRoom.put(randomInt, id);
           // System.out.println("Producer #" + this.id + " got: " + i);
        }
    }
}