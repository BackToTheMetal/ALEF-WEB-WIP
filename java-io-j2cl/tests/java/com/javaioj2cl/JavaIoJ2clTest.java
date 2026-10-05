package com.javaioj2cl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

public final class JavaIoJ2clTest {
  @Test
  public void fileIsLexicalAndNeverClaimsStorage() throws IOException {
    File file = new File(new File("models"), "sample.mps");
    assertEquals("models/sample.mps", file.getPath());
    assertEquals("sample.mps", file.getName());
    assertEquals("models", file.getParent());
    assertEquals("/models/sample.mps", file.getAbsolutePath());
    assertFalse(file.exists());
    assertFalse(file.isFile());
    assertFalse(file.isDirectory());
  }

  @Test
  public void dataStreamsRoundTripMpsPrimitives() throws IOException {
    assertEquals(
        "true:12345678:102030405060708:MPS-λ",
        JavaIoConsumer.readData(JavaIoConsumer.writeData()));
  }

  @Test
  public void readerDecodesUtf8AndBufferedLines() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    OutputStreamWriter writer = new OutputStreamWriter(bytes, StandardCharsets.UTF_8);
    writer.write("first\nMPS-λ");
    writer.close();
    assertEquals("first:MPS-λ", JavaIoConsumer.readText(bytes.toByteArray()));
  }

  @Test
  public void characterArrayStreamsAreFunctional() throws IOException {
    assertEquals("model-root", JavaIoConsumer.copyCharacters("model-root"));
  }

  @Test
  public void unsupportedBackendsFailExplicitly() throws IOException {
    assertTrue(JavaIoConsumer.openFileFailure("model.mps").contains("no browser filesystem"));
    assertTrue(JavaIoConsumer.objectStreamsFailExplicitly());
  }
}
