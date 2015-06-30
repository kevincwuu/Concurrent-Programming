package ConcurrentProgramming;

import java.util.Random;
import java.util.concurrent.Exchanger;

public class ProducerConsumerExchanger {

        Exchanger exchanger = new Exchanger();

        private class Producer implements Runnable
        {
            private Object value;
            int id;
            Random randomGenerator = new Random();


            public Producer(int id) {
                this.id = id;
            }

            @Override
            public void run()
            {
                try
                {
                    //exchange the randomly generated number produced for the consumer's id
                    value = exchanger.exchange(randomGenerator.nextInt(500));
                    System.out.println(Thread.currentThread().getName()+" now has id #"+ value);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

        private class Consumer implements Runnable
        {
            private Object id;
            Object tempID = id;


            public Consumer(int id) {
                this.id = id;
                tempID = id;
            }

            @Override
            public void run()
            {
                try
                {
                    //exchange the id for a randomly generated number produced by producer
                    id = exchanger.exchange(id);
                    System.out.println(Thread.currentThread().getName() + " #" + tempID + " now has value "+id);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

        private void start()
        {
            new Thread(new Producer(1),"Producer").start();
            new Thread(new Consumer(1),"Consumer").start();
            new Thread(new Producer(2),"Producer").start();
            new Thread(new Consumer(2),"Consumer").start();
            new Thread(new Producer(3),"Producer").start();
            new Thread(new Consumer(3),"Consumer").start();
            new Thread(new Producer(4),"Producer").start();
            new Thread(new Consumer(4),"Consumer").start();
            new Thread(new Producer(5),"Producer").start();
            new Thread(new Consumer(5),"Consumer").start();
            new Thread(new Producer(6),"Producer").start();
            new Thread(new Consumer(6),"Consumer").start();
        }

        public static void main(String[] args)
        {
            new ProducerConsumerExchanger().start();
        }

    }