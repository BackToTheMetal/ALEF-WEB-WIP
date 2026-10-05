package com.javaioj2cl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

/** MPS-shaped compile and runtime probes for the added java.io profile. */
public final class JavaIoConsumer {
  private JavaIoConsumer() {}

  public static String lexicalPath(String parent, String child) throws IOException {
    File file = new File(new File(parent), child);
    File canonical = file.getCanonicalFile();
    FileFilter fileFilter = candidate -> candidate.isFile();
    FilenameFilter nameFilter = (directory, name) -> name.endsWith(".mps");
    if (fileFilter.accept(file) || nameFilter.accept(file.getParentFile(), file.getName())) {
      return canonical.getAbsolutePath();
    }
    return file.getPath();
  }

  public static byte[] writeData() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    DataOutput output = new DataOutputStream(bytes);
    output.writeBoolean(true);
    output.writeInt(0x12345678);
    output.writeLong(0x0102030405060708L);
    output.writeUTF("MPS-λ");
    return bytes.toByteArray();
  }

  public static String readData(byte[] bytes) throws IOException {
    DataInput input = new DataInputStream(new ByteArrayInputStream(bytes));
    return input.readBoolean()
        + ":"
        + Integer.toHexString(input.readInt())
        + ":"
        + Long.toHexString(input.readLong())
        + ":"
        + input.readUTF();
  }

  public static String readText(byte[] bytes) throws IOException {
    BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8));
    String first = reader.readLine();
    String second = reader.readLine();
    return first + ":" + second;
  }

  public static String copyCharacters(String value) throws IOException {
    CharArrayReader reader = new CharArrayReader(value.toCharArray());
    CharArrayWriter writer = new CharArrayWriter();
    char[] buffer = new char[3];
    int count;
    while ((count = reader.read(buffer, 0, buffer.length)) != -1) {
      writer.write(buffer, 0, count);
    }
    return writer.toString();
  }

  public static String openFileFailure(String path) {
    try {
      new FileInputStream(path);
      return "unexpected success";
    } catch (FileNotFoundException expected) {
      return expected.getMessage();
    }
  }

  public static boolean objectStreamsFailExplicitly() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try {
      new ObjectOutputStream(bytes).writeObject("unsupported");
      return false;
    } catch (NotSerializableException expected) {
      try {
        new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())).readObject();
        return false;
      } catch (NotSerializableException alsoExpected) {
        return true;
      } catch (ClassNotFoundException impossible) {
        throw new IOException(impossible);
      }
    }
  }
}
