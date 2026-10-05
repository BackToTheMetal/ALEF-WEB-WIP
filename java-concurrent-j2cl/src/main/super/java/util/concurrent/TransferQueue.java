package java.util.concurrent;

/** Transfer queue API; rendezvous requires a consumer that can run independently. */
public interface TransferQueue<E> extends BlockingQueue<E> {
  boolean tryTransfer(E element);
  void transfer(E element) throws InterruptedException;
  boolean tryTransfer(E element, long timeout, TimeUnit unit) throws InterruptedException;
  boolean hasWaitingConsumer();
  int getWaitingConsumerCount();
}
