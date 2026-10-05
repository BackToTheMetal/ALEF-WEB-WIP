package java.io;

public class NotSerializableException extends ObjectStreamException {
  public NotSerializableException(String className) {
    super(className);
  }

  public NotSerializableException() {}
}
