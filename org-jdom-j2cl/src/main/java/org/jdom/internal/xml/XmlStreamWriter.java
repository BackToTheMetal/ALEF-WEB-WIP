package org.jdom.internal.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/** Streaming encoders used by XML output, independent of optional JRE charsets. */
public final class XmlStreamWriter extends Writer {
  private final OutputStream output;
  private final String encoding;
  private char high;
  private boolean closed, bom;

  public XmlStreamWriter(OutputStream output,String encoding)throws UnsupportedEncodingException {
    if(output==null)throw new NullPointerException("output");
    this.output=output;
    try{this.encoding=XmlEncoding.canonical(encoding);}catch(IOException e){throw new UnsupportedEncodingException(e.getMessage());}
  }
  @Override public void write(char[] chars,int offset,int length)throws IOException {
    if(closed)throw new IOException("Writer is closed");
    if(offset<0||length<0||offset>chars.length-length)throw new IndexOutOfBoundsException();
    if(length==0)return;
    if(!bom){bom=true;if(encoding.equals("UTF-16")){output.write(0xfe);output.write(0xff);}}
    for(int i=offset;i<offset+length;i++) {
      char c=chars[i];
      if(high!=0) {
        if(!Character.isLowSurrogate(c))throw new IOException("Unpaired surrogate in XML output");
        int code=Character.toCodePoint(high,c);high=0;codePoint(code);
      }else if(Character.isHighSurrogate(c))high=c;
      else if(Character.isLowSurrogate(c))throw new IOException("Unpaired surrogate in XML output");
      else codePoint(c);
    }
  }
  private void codePoint(int c)throws IOException {
    if(encoding.startsWith("UTF-16")) {
      for(char unit:Character.toChars(c)) {
        if(encoding.equals("UTF-16LE")){output.write(unit&255);output.write(unit>>>8);}
        else{output.write(unit>>>8);output.write(unit&255);}
      }
    }else if(encoding.equals("ISO-8859-1")||encoding.equals("US-ASCII")) {
      if(c>(encoding.equals("US-ASCII")?127:255))throw new IOException("Unrepresentable XML output character");
      output.write(c);
    }else if(c<0x80)output.write(c);
    else if(c<0x800){output.write(0xc0|c>>>6);output.write(0x80|c&63);}
    else if(c<0x10000){output.write(0xe0|c>>>12);output.write(0x80|c>>>6&63);output.write(0x80|c&63);}
    else{output.write(0xf0|c>>>18);output.write(0x80|c>>>12&63);output.write(0x80|c>>>6&63);output.write(0x80|c&63);}
  }
  @Override public void flush()throws IOException{if(closed)throw new IOException("Writer is closed");output.flush();}
  @Override public void close()throws IOException {
    if(closed)return;
    closed=true;
    try{if(high!=0)throw new IOException("Unpaired surrogate in XML output");}finally{output.close();}
  }
}
