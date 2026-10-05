package org.jdom.internal.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Locale;

/** Strict in-memory XML byte decoding; unsupported charsets never fall back silently. */
final class XmlEncoding {
  final StringReader reader;
  final String encoding;

  private XmlEncoding(String text, String encoding) { this.reader = new StringReader(text); this.encoding = encoding; }

  static String canonical(String encoding) throws IOException {
    if (encoding == null) return "UTF-8";
    switch (encoding.toUpperCase(Locale.ROOT)) {
      case "UTF8": case "UTF-8": return "UTF-8";
      case "UTF-16": case "UTF-16BE": case "UTF-16LE": return encoding.toUpperCase(Locale.ROOT);
      case "ASCII": case "US-ASCII": return "US-ASCII";
      case "LATIN1": case "ISO-8859-1": return "ISO-8859-1";
      default: throw new IOException("Unsupported XML encoding " + encoding);
    }
  }

  static XmlEncoding read(InputStream input, String explicit, int maxChars) throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    byte[] chunk = new byte[4096];
    for (int n; (n=input.read(chunk))!=-1;) {
      if (n==0) throw new IOException("InputStream made no progress");
      if ((long)bytes.size()+n > (long)maxChars*4+4) throw new IOException("XML byte limit exceeded");
      bytes.write(chunk,0,n);
    }
    byte[] data=bytes.toByteArray(); int start=0;
    String detected=null;
    if (data.length>=3 && u(data[0])==0xef && u(data[1])==0xbb && u(data[2])==0xbf) {detected="UTF-8";start=3;}
    else if(data.length>=2 && u(data[0])==0xfe && u(data[1])==0xff) {detected="UTF-16BE";start=2;}
    else if(data.length>=2 && u(data[0])==0xff && u(data[1])==0xfe) {detected="UTF-16LE";start=2;}
    else if(data.length>=4 && data[0]==0 && data[1]=='<' && data[2]==0 && data[3]=='?') detected="UTF-16BE";
    else if(data.length>=4 && data[0]=='<' && data[1]==0 && data[2]=='?' && data[3]==0) detected="UTF-16LE";
    String encoding=explicit==null ? detected : canonical(explicit);
    if(detected!=null && encoding!=null && !compatible(encoding,detected)) throw new IOException("Conflicting XML byte encoding and BOM");
    if(encoding==null) {
      // An ASCII-compatible declaration can choose the decoder before non-ASCII bytes occur.
      StringBuilder prefix=new StringBuilder();
      boolean xmlDeclaration=data.length>=6 && data[0]=='<' && data[1]=='?' && data[2]=='x' && data[3]=='m' && data[4]=='l'
          && (data[5]==' ' || data[5]=='\t' || data[5]=='\r' || data[5]=='\n');
      if(xmlDeclaration)for(int i=0;i<data.length;i++) {
        if(i>=maxChars)throw new IOException("XML declaration exceeds character limit");
        int c=u(data[i]);if(c>127)break;prefix.append((char)c);if(c=='>')break;
      }
      String declaration=prefix.toString();
      if(declaration.startsWith("<?xml ") || declaration.startsWith("<?xml\t") || declaration.startsWith("<?xml\n") || declaration.startsWith("<?xml\r")) {
        int at=declaration.indexOf("encoding");
        if(at>=0) {
          at+=8;while(at<declaration.length() && Character.isWhitespace(declaration.charAt(at)))at++;
          if(at<declaration.length() && declaration.charAt(at++)=='=') {
            while(at<declaration.length() && Character.isWhitespace(declaration.charAt(at)))at++;
            if(at<declaration.length()) {char q=declaration.charAt(at++);int end=declaration.indexOf(q,at);if((q=='\''||q=='"')&&end>at)encoding=canonical(declaration.substring(at,end));}
          }
        }
      }
    }
    if(encoding==null)encoding="UTF-8";
    if(encoding.equals("UTF-16")) {
      if(detected==null)throw new IOException("UTF-16 XML requires a BOM");
      encoding=detected;
    }
    StringBuilder text=new StringBuilder();
    for(int i=start;i<data.length;) {
      int c=u(data[i++]);
      if(encoding.startsWith("UTF-16")) {
        if(i==data.length)throw new IOException("Truncated UTF-16 code unit");
        int second=u(data[i++]); text.append((char)(encoding.equals("UTF-16LE")? c | second<<8 : c<<8 | second));
      } else if(encoding.equals("ISO-8859-1"))text.append((char)c);
      else if(c<128)text.append((char)c);
      else {
        if(encoding.equals("US-ASCII"))throw new IOException("Non-ASCII byte");
        int n=c>=0xc2&&c<=0xdf?1:c>=0xe0&&c<=0xef?2:c>=0xf0&&c<=0xf4?3:-1;
        if(n<0||i+n>data.length)throw new IOException("Invalid UTF-8 sequence");
        int value=c & (n==1?31:n==2?15:7);
        for(int j=0;j<n;j++) {int b=u(data[i++]);if((b&0xc0)!=0x80)throw new IOException("Invalid UTF-8 continuation");value=value<<6 | b&63;}
        if(value<(n==1?0x80:n==2?0x800:0x10000)||value>0x10ffff||value>=0xd800&&value<=0xdfff)throw new IOException("Invalid UTF-8 code point");
        text.appendCodePoint(value);
      }
      if(text.length()>maxChars)throw new IOException("XML character limit exceeded");
    }
    return new XmlEncoding(text.toString(),encoding);
  }
  static boolean compatible(String a,String b) { return a.equals(b) || a.equals("UTF-16") && b.startsWith("UTF-16") || b.equals("UTF-16") && a.startsWith("UTF-16"); }
  private static int u(byte b){return b&255;}
}
