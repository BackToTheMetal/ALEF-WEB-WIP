# Build and adjacent dependency inventory

This appendix accounts for supporting work outside MPS Java replacements: source selection, J2CL compiler and JRE patches, AWT/Swing views, tests, generators, and manifests. These counts must not be added to the MPS replacement total.

## Shared browser source projects

| MPS project | Selected Java files | Selection mode | Reason |
| --- | --- | --- | --- |
| actions-runtime | 21 | complete | Complete production Java source roots |
| baseLanguage-runtime | 4 | complete | Complete production Java source roots |
| behavior-api | 23 | complete | Complete production Java source roots |
| behavior-runtime | 23 | complete | Complete production Java source roots |
| closures-runtime | 8 | complete | Complete production Java source roots |
| collections-runtime | 76 | complete | Complete production Java source roots |
| components | 6 | complete | Complete production Java source roots |
| constraints-runtime | 45 | complete | Complete production Java source roots |
| context | 4 | complete | Complete production Java source roots |
| editing-runtime | 1 | complete | Complete production Java source roots |
| editor-api | 116 | complete | Complete production Java source roots |
| editor-runtime | 456 | explicit | Preserve editor algorithms and omit only the unreferenced desktop image/folder chooser helper. JVM make/workbench startup dependencies are excluded. Declare JDOM directly for editor mementos and selection XML after removing desktop library exports. |
| editorlang-runtime | 50 | complete | Complete production Java source roots |
| findUsages-runtime | 13 | explicit | Real node-reference, concept-instance and model-usage searches, including traversal fallbacks and registered persistence participants, plus editor model scopes. Desktop FindersManager/plugin discovery remains excluded. |
| intentions-runtime | 19 | explicit | Preserve intention discovery/execution and omit the standalone desktop settings tree. |
| kernel | 477 | complete | Complete production Java source roots |
| logging | 2 | complete | Complete production Java source roots |
| messages-for-legacy-constraints | 7 | complete | Complete production Java source roots |
| mps-annotations | 5 | complete | Complete production Java source roots |
| mps-icons | 2 | explicit | Specific Java companion classes required by the browser editor; desktop module features are excluded. |
| mps-messaging | 6 | complete | Complete production Java source roots |
| mps-platform | 42 | explicit | Specific Java companion classes required by the browser editor; desktop module features are excluded. |
| mps-ui | 2 | explicit | Specific Java companion classes required by the browser editor; desktop module features are excluded. |
| openapi | 127 | complete | Complete production Java source roots |
| pattern-runtime | 12 | complete | Complete production Java source roots |
| platform | 1 | explicit | Platform contract only; BrowserPlatform explicitly installs core/typechecking without desktop startup, make, generation or importers. |
| problem | 5 | complete | Complete production Java source roots |
| project | 84 | complete | Complete production Java source roots |
| scripts-runtime | 3 | explicit | Specific Java companion classes required by the browser editor; desktop module features are excluded. |
| smodel | 128 | complete | Complete production Java source roots |
| tuples-runtime | 4 | complete | Complete production Java source roots |
| typechecking | 23 | complete | Complete production Java source roots |
| typesystemEngine | 153 | complete | Complete production Java source roots |
| typesystemIntegration | 4 | explicit | Real typesystem and non-typesystem editor checkers, diagnostic conversion and highlighting. Desktop checker startup and generated reference-scope checking depend on project-check/generator integrations and are excluded from this browser subset. |
| util-core | 50 | complete | Complete production Java source roots |
| vfs | 50 | complete | Complete production Java source roots |


## Patch targets

Every target file in the shared dependency’s patch files is listed. A target may appear in multiple ordered patches; these are patch records, not distinct Java classes. The composed JRE draws from sibling IO, NIO, concurrency and networking ports; its full source ownership is preserved in inventory.json.

| Patch | Target | Operation |
| --- | --- | --- |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/io/InputStream.java | modify |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/io/PrintStream.java | modify |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/io/Reader.java | modify |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/lang/Character.java | modify |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/lang/Deprecated.java | modify |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/lang/LinkageError.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/lang/Long.java | modify |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/lang/NoClassDefFoundError.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/lang/NoSuchMethodException.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/lang/OutOfMemoryError.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/lang/String.java | modify |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/lang/System.java | modify |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/lang/VirtualMachineError.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/lang/ref/PhantomReference.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/lang/ref/Reference.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/lang/ref/ReferenceQueue.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/lang/ref/SoftReference.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/lang/ref/WeakReference.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/lang/reflect/InvocationTargetException.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/lang/reflect/Modifier.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/security/SecureRandom.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/time/DateTimeException.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/time/Duration.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/time/Instant.java | modify |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/util/Collections.java | modify |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/util/List.java | modify |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/util/StringTokenizer.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/util/UUID.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/util/WeakHashMap.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/util/logging/LogRecord.java | modify |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/util/regex/JavaRegexUnicodeData.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/util/regex/Matcher.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/util/regex/Pattern.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/util/regex/PatternSyntaxException.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/java/util/stream/Stream.java | modify |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/javaemul/internal/CharacterData.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/javaemul/internal/MpsFormatter.java | add |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | jre/java/module-info.java | modify |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/BufferedInputStream.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/BufferedOutputStream.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/BufferedReader.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/CharArrayReader.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/CharArrayWriter.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/DataInput.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/DataInputStream.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/DataOutput.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/DataOutputStream.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/EOFException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/File.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/FileFilter.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/FileInputStream.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/FileNotFoundException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/FileOutputStream.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/FileReader.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/FileWriter.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/FilenameFilter.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/IOError.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/InputStreamReader.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/LineNumberReader.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/NotSerializableException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/ObjectInput.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/ObjectInputStream.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/ObjectOutput.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/ObjectOutputStream.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/ObjectStreamException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/PipedInputStream.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/PipedOutputStream.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/io/UTFDataFormatException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/lang/ClassLoader.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/lang/ClassNotFoundException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/lang/IllegalMonitorStateException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/lang/IllegalThreadStateException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/lang/Readable.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/lang/ReflectiveOperationException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/lang/Thread.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/lang/ThreadGroup.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/lang/ThreadLocal.java | modify |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/net/DatagramSocket.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/net/InetAddress.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/net/MalformedURLException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/net/NetworkInterface.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/net/ProtocolFamily.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/net/ServerSocket.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/net/Socket.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/net/SocketAddress.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/net/SocketOption.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/net/URI.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/net/URISyntaxException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/net/URL.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/Buffer.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/BufferOverflowException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/BufferUnderflowException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/ByteBuffer.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/ByteOrder.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/CharBuffer.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/DoubleBuffer.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/FloatBuffer.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/IntBuffer.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/InvalidMarkException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/LongBuffer.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/MappedByteBuffer.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/ReadOnlyBufferException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/ShortBuffer.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/AcceptPendingException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/AlreadyBoundException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/AlreadyConnectedException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/AsynchronousByteChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/AsynchronousChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/AsynchronousChannelGroup.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/AsynchronousCloseException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/AsynchronousFileChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/AsynchronousServerSocketChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/AsynchronousSocketChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/ByteChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/CancelledKeyException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/Channel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/Channels.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/ClosedByInterruptException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/ClosedChannelException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/ClosedSelectorException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/CompletionHandler.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/ConnectionPendingException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/DatagramChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/FileChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/FileLock.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/FileLockInterruptionException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/GatheringByteChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/IllegalBlockingModeException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/IllegalChannelGroupException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/IllegalSelectorException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/InterruptedByTimeoutException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/InterruptibleChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/MembershipKey.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/MulticastChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/NetworkChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/NoConnectionPendingException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/NonReadableChannelException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/NonWritableChannelException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/NotYetBoundException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/NotYetConnectedException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/OverlappingFileLockException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/Pipe.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/ReadPendingException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/ReadableByteChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/ScatteringByteChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/SeekableByteChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/SelectableChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/SelectionKey.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/Selector.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/ServerSocketChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/ShutdownChannelGroupException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/SocketChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/UnresolvedAddressException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/UnsupportedAddressTypeException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/WritableByteChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/WritePendingException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/spi/AbstractInterruptibleChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/spi/AbstractSelectableChannel.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/spi/AbstractSelectionKey.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/spi/AbstractSelector.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/spi/AsynchronousChannelProvider.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/channels/spi/SelectorProvider.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/charset/CharacterCodingException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/charset/Charset.java | modify |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/charset/CharsetDecoder.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/charset/CharsetEncoder.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/charset/CoderMalfunctionError.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/charset/CoderResult.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/charset/CodingErrorAction.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/charset/MalformedInputException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/charset/StandardCharsets.java | modify |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/charset/UnmappableCharacterException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/charset/spi/CharsetProvider.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/AccessDeniedException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/AccessMode.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/AtomicMoveNotSupportedException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/ClosedDirectoryStreamException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/ClosedFileSystemException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/ClosedWatchServiceException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/CopyOption.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/DirectoryIteratorException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/DirectoryNotEmptyException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/DirectoryStream.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/FileAlreadyExistsException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/FileStore.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/FileSystem.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/FileSystemAlreadyExistsException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/FileSystemException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/FileSystemLoopException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/FileSystemNotFoundException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/FileSystems.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/FileVisitOption.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/FileVisitResult.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/FileVisitor.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/Files.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/InvalidPathException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/LinkOption.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/LinkPermission.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/NoSuchFileException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/NotDirectoryException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/NotLinkException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/OpenOption.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/Path.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/PathMatcher.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/Paths.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/ProviderMismatchException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/ProviderNotFoundException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/ReadOnlyFileSystemException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/SecureDirectoryStream.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/SimpleFileVisitor.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/StandardCopyOption.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/StandardOpenOption.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/StandardWatchEventKinds.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/WatchEvent.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/WatchKey.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/WatchService.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/Watchable.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/AclEntry.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/AclEntryFlag.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/AclEntryPermission.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/AclEntryType.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/AclFileAttributeView.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/AttributeView.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/BasicFileAttributeView.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/BasicFileAttributes.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/DosFileAttributeView.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/DosFileAttributes.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/FileAttribute.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/FileAttributeView.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/FileOwnerAttributeView.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/FileStoreAttributeView.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/FileTime.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/GroupPrincipal.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/PosixFileAttributeView.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/PosixFileAttributes.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/PosixFilePermission.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/PosixFilePermissions.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/UserDefinedFileAttributeView.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/UserPrincipal.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/UserPrincipalLookupService.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/attribute/UserPrincipalNotFoundException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/spi/FileSystemProvider.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/nio/file/spi/FileTypeDetector.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/security/BasicPermission.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/security/Permission.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/security/Principal.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/time/Instant.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/AbstractExecutorService.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/ArrayBlockingQueue.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/BlockingQueue.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/BrokenBarrierException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/CompletableFuture.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/CompletionException.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/CompletionService.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/CompletionStage.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/ConcurrentHashMap.java | modify |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/ConcurrentLinkedDeque.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/ConcurrentLinkedQueue.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/CopyOnWriteArrayList.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/CopyOnWriteArraySet.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/CountDownLatch.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/CyclicBarrier.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/DelayQueue.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/ExecutorCompletionService.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/ExecutorService.java | modify |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/Executors.java | modify |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/ForkJoinPool.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/FutureTask.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/LinkedBlockingQueue.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/LinkedTransferQueue.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/MainThreadBlockingQueue.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/MinimalCompletionStage.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/RejectedExecutionHandler.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/RunnableScheduledFuture.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/ScheduledThreadPoolExecutor.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/Semaphore.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/ThreadFactory.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/ThreadLocalRandom.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/ThreadPoolExecutor.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/TransferQueue.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/atomic/AtomicInteger.java | modify |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/atomic/AtomicIntegerArray.java | modify |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/atomic/AtomicLong.java | modify |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/atomic/AtomicLongArray.java | modify |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/atomic/AtomicMarkableReference.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/atomic/AtomicReference.java | modify |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/atomic/AtomicReferenceArray.java | modify |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/atomic/AtomicStampedReference.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/atomic/DoubleAdder.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/atomic/LongAdder.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/locks/Condition.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/locks/Lock.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/locks/LockSupport.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/locks/ReadWriteLock.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/locks/ReentrantLock.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/java/util/concurrent/locks/ReentrantReadWriteLock.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/javaemul/internal/EmulatedCharset.java | modify |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/javaemul/internal/concurrent/MainThreadTimers.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/javaemul/internal/nio/UnsupportedNio.java | add |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | jre/java/module-info.java | modify |
| [third_party/j2cl/mps-support/classloader.patch](<C:/Codex/mps-j2cl/third_party/j2cl/mps-support/classloader.patch>) | jre/java/java/lang/ClassLoader.java | modify |
| [third_party/j2cl/type-support/class-type-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/type-support/class-type-support.patch>) | jre/java/java/lang/Class.java | modify |
| [third_party/j2cl/type-support/class-type-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/type-support/class-type-support.patch>) | jre/java/java/lang/reflect/Array.java | modify |
| [third_party/j2cl/type-support/class-type-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/type-support/class-type-support.patch>) | jre/java/javaemul/internal/Constructor.java | modify |
| [third_party/j2cl/type-support/class-type-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/type-support/class-type-support.patch>) | jre/java/javaemul/internal/nativebootstrap/Util.impl.java.js | modify |
| [third_party/j2cl/type-support/class-type-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/type-support/class-type-support.patch>) | jre/java/javaemul/internal/vmbootstrap/Arrays.impl.java.js | modify |
| [third_party/j2cl/type-support/class-type-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/type-support/class-type-support.patch>) | transpiler/java/com/google/j2cl/transpiler/ast/RuntimeMethods.java | modify |
| [third_party/j2cl/type-support/class-type-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/type-support/class-type-support.patch>) | transpiler/java/com/google/j2cl/transpiler/passes/ImplementClassMetadataViaConstructors.java | modify |
| [third_party/webawt/elemental2-1.3.2-w3c-css.patch](<C:/Codex/mps-j2cl/third_party/webawt/elemental2-1.3.2-w3c-css.patch>) | java/elemental2/dom/BUILD | modify |


## Adjacent WebAWT and WebSwing changes

These are the exact dependency-view mappings owned by mps-j2cl. An addition means the original path is absent in the current sibling source tree; an override means it exists. They are not MPS replacements. ALEF also has its own AWT/Swing integration work outside the scope of this MPS dependency inventory.

| Dependency | Original path | Mapped file | Kind |
| --- | --- | --- | --- |
| webawt | src/main/java/java/awt/AWTKeyStroke.java | [AWTKeyStroke.java](<C:/Codex/mps-j2cl/third_party/webawt/src/main/java/java/awt/AWTKeyStroke.java>) | override |
| webawt | src/main/java/java/net/BUILD.bazel | [webawt-net.BUILD.bazel](<C:/Codex/mps-j2cl/webawt-net.BUILD.bazel>) | override |
| webswing | src/main/java/javax/swing/SwingUtilities.java | [SwingUtilities.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/SwingUtilities.java>) | override |
| webswing | src/main/java/javax/swing/JMenuItem.java | [JMenuItem.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JMenuItem.java>) | override |
| webswing | src/main/java/javax/swing/JScrollBar.java | [JScrollBar.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JScrollBar.java>) | override |
| webswing | src/main/java/javax/swing/JViewport.java | [JViewport.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JViewport.java>) | override |
| webswing | src/main/java/javax/swing/JWindow.java | [JWindow.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JWindow.java>) | addition |
| webswing | src/main/java/javax/swing/LayoutFocusTraversalPolicy.java | [LayoutFocusTraversalPolicy.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/LayoutFocusTraversalPolicy.java>) | addition |
| webswing | src/main/java/javax/swing/Scrollable.java | [Scrollable.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/Scrollable.java>) | addition |
| webswing | src/main/java/javax/swing/event/CaretEvent.java | [CaretEvent.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/event/CaretEvent.java>) | addition |
| webswing | src/main/java/javax/swing/event/CaretListener.java | [CaretListener.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/event/CaretListener.java>) | addition |
| webswing | src/main/java/javax/swing/plaf/MenuItemUI.java | [MenuItemUI.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/plaf/MenuItemUI.java>) | addition |
| webswing | src/main/java/javax/swing/plaf/ScrollBarUI.java | [ScrollBarUI.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/plaf/ScrollBarUI.java>) | addition |
| webswing | src/main/java/javax/swing/text/JTextComponent.java | [JTextComponent.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/text/JTextComponent.java>) | override |
| webswing | src/main/java/javax/swing/plaf/basic/BasicMenuItemUI.java | [BasicMenuItemUI.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/plaf/basic/BasicMenuItemUI.java>) | addition |
| webswing | src/main/java/javax/swing/JComponent.java | [JComponent.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JComponent.java>) | override |
| webswing | src/main/java/javax/swing/JEditorPane.java | [JEditorPane.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JEditorPane.java>) | override |
| webswing | src/main/java/javax/swing/JMenu.java | [JMenu.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JMenu.java>) | override |
| webswing | src/main/java/javax/swing/JPopupMenu.java | [JPopupMenu.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JPopupMenu.java>) | override |
| webswing | src/main/java/javax/swing/JScrollPane.java | [JScrollPane.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JScrollPane.java>) | override |
| webswing | src/main/java/javax/swing/event/HyperlinkEvent.java | [HyperlinkEvent.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/event/HyperlinkEvent.java>) | addition |
| webswing | src/main/java/javax/swing/event/HyperlinkListener.java | [HyperlinkListener.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/event/HyperlinkListener.java>) | addition |
| webswing | src/main/java/javax/swing/text/DefaultCaret.java | [DefaultCaret.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/text/DefaultCaret.java>) | override |
| webswing | src/main/java/javax/swing/plaf/basic/BasicScrollBarUI.java | [BasicScrollBarUI.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/plaf/basic/BasicScrollBarUI.java>) | addition |
| webswing | src/main/java/javax/swing/event/MenuEvent.java | [MenuEvent.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/event/MenuEvent.java>) | addition |
| webswing | src/main/java/javax/swing/event/MenuListener.java | [MenuListener.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/event/MenuListener.java>) | addition |
| webswing | src/main/java/javax/swing/BrowserPaintSurface.java | [BrowserPaintSurface.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/BrowserPaintSurface.java>) | addition |
| webswing | src/main/java/javax/swing/event/EventListenerList.java | [EventListenerList.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/event/EventListenerList.java>) | override |
| webswing | src/main/java/javax/swing/BrowserRendererSnapshot.java | [BrowserRendererSnapshot.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/BrowserRendererSnapshot.java>) | addition |
| webswing | src/main/java/javax/swing/JList.java | [JList.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JList.java>) | override |
| webswing | src/main/java/javax/swing/event/PopupMenuEvent.java | [PopupMenuEvent.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/event/PopupMenuEvent.java>) | addition |
| webswing | src/main/java/javax/swing/event/PopupMenuListener.java | [PopupMenuListener.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/event/PopupMenuListener.java>) | addition |
| webswing | src/main/java/javax/swing/text/DefaultEditorKit.java | [DefaultEditorKit.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/text/DefaultEditorKit.java>) | override |
| webswing | src/main/java/javax/swing/AbstractButton.java | [AbstractButton.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/AbstractButton.java>) | override |
| webswing | src/main/java/javax/swing/BorderFactory.java | [BorderFactory.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/BorderFactory.java>) | override |
| webswing | src/main/java/javax/swing/DefaultListCellRenderer.java | [DefaultListCellRenderer.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/DefaultListCellRenderer.java>) | override |
| webswing | src/main/java/javax/swing/ImageIcon.java | [ImageIcon.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/ImageIcon.java>) | override |
| webswing | src/main/java/javax/swing/JLayeredPane.java | [JLayeredPane.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JLayeredPane.java>) | override |
| webswing | src/main/java/javax/swing/JRootPane.java | [JRootPane.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JRootPane.java>) | override |
| webswing | src/main/java/javax/swing/border/CompoundBorder.java | [CompoundBorder.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/border/CompoundBorder.java>) | addition |
| webswing | src/main/java/javax/swing/border/MatteBorder.java | [MatteBorder.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/border/MatteBorder.java>) | addition |
| webswing | src/main/java/javax/swing/plaf/basic/BasicButtonUI.java | [BasicButtonUI.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/plaf/basic/BasicButtonUI.java>) | override |
| webswing | src/main/java/javax/swing/JDialog.java | [JDialog.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JDialog.java>) | override |
| webswing | src/main/java/javax/swing/KeyStroke.java | [KeyStroke.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/KeyStroke.java>) | override |
| webswing | src/main/java/javax/swing/BrowserInputMethodSupport.java | [BrowserInputMethodSupport.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/BrowserInputMethodSupport.java>) | addition |
| webswing | src/main/java/javax/swing/RepaintManager.java | [RepaintManager.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/RepaintManager.java>) | override |


## Current tracked changes inside the upstream checkout

The pinned MPS commit is d2589ed55bbc9445b660769fcc17da97e8e395a6. Git reports the following tracked local changes. This list is separate from the external replacement mechanism. The shared source audit currently fails while parsing local module metadata; it reports unresolved IDEAPlugin dependencies intellij-stubs, webawt and webswing.

| Git status | Path |
| --- | --- |
|  M | [IdeaPlugin/mps-core/mps-core.iml](<C:/Codex/mps-j2cl/mps/IdeaPlugin/mps-core/mps-core.iml>) |
|  M | [IdeaPlugin/mps-java/mps-java.iml](<C:/Codex/mps-j2cl/mps/IdeaPlugin/mps-java/mps-java.iml>) |
|  M | [IdeaPlugin/mps-migration/mps-migration.iml](<C:/Codex/mps-j2cl/mps/IdeaPlugin/mps-migration/mps-migration.iml>) |
|  M | [IdeaPlugin/mps-testing/mps-testing.iml](<C:/Codex/mps-j2cl/mps/IdeaPlugin/mps-testing/mps-testing.iml>) |
|  M | [IdeaPlugin/mps-vcs/mps-vcs.iml](<C:/Codex/mps-j2cl/mps/IdeaPlugin/mps-vcs/mps-vcs.iml>) |
|  M | [IdeaPlugin/tests/psiProject/psiProject.iml](<C:/Codex/mps-j2cl/mps/IdeaPlugin/tests/psiProject/psiProject.iml>) |
|  M | [core/aspects/behavior/behavior-api/behavior-api.iml](<C:/Codex/mps-j2cl/mps/core/aspects/behavior/behavior-api/behavior-api.iml>) |
|  M | [core/aspects/behavior/behavior-runtime/behavior-runtime.iml](<C:/Codex/mps-j2cl/mps/core/aspects/behavior/behavior-runtime/behavior-runtime.iml>) |
|  M | [core/aspects/feedback/context/context.iml](<C:/Codex/mps-j2cl/mps/core/aspects/feedback/context/context.iml>) |
|  M | [core/aspects/feedback/feedback-api/feedback-api.iml](<C:/Codex/mps-j2cl/mps/core/aspects/feedback/feedback-api/feedback-api.iml>) |
|  M | [core/aspects/feedback/messages-api/messages-api.iml](<C:/Codex/mps-j2cl/mps/core/aspects/feedback/messages-api/messages-api.iml>) |
|  M | [core/aspects/feedback/messages-for-legacy-constraints/messages-for-legacy-constraints.iml](<C:/Codex/mps-j2cl/mps/core/aspects/feedback/messages-for-legacy-constraints/messages-for-legacy-constraints.iml>) |
|  M | [core/aspects/feedback/messages-for-rules/messages-for-rules.iml](<C:/Codex/mps-j2cl/mps/core/aspects/feedback/messages-for-rules/messages-for-rules.iml>) |
|  M | [core/aspects/feedback/messages-for-structure/messages-for-structure.iml](<C:/Codex/mps-j2cl/mps/core/aspects/feedback/messages-for-structure/messages-for-structure.iml>) |
|  M | [core/components/components.iml](<C:/Codex/mps-j2cl/mps/core/components/components.iml>) |
|  M | [core/findUsages-runtime/findUsages-runtime.iml](<C:/Codex/mps-j2cl/mps/core/findUsages-runtime/findUsages-runtime.iml>) |
|  M | [core/java-stub/java-stub.iml](<C:/Codex/mps-j2cl/mps/core/java-stub/java-stub.iml>) |
|  M | [core/kotlin-stub/kotlin-stub.iml](<C:/Codex/mps-j2cl/mps/core/kotlin-stub/kotlin-stub.iml>) |
|  M | [core/make-runtime/make-runtime.iml](<C:/Codex/mps-j2cl/mps/core/make-runtime/make-runtime.iml>) |
|  M | [core/model/model.iml](<C:/Codex/mps-j2cl/mps/core/model/model.iml>) |
|  M | [core/module/module.iml](<C:/Codex/mps-j2cl/mps/core/module/module.iml>) |
|  M | [core/mps-annotations/mps-annotations.iml](<C:/Codex/mps-j2cl/mps/core/mps-annotations/mps-annotations.iml>) |
|  M | [core/mps-messaging/mps-messaging.iml](<C:/Codex/mps-j2cl/mps/core/mps-messaging/mps-messaging.iml>) |
|  M | [core/openapi/openapi.iml](<C:/Codex/mps-j2cl/mps/core/openapi/openapi.iml>) |
|  M | [core/persistence/persistence.iml](<C:/Codex/mps-j2cl/mps/core/persistence/persistence.iml>) |
|  M | [core/project-check/project-check.iml](<C:/Codex/mps-j2cl/mps/core/project-check/project-check.iml>) |
|  M | [core/textgen/textgen.iml](<C:/Codex/mps-j2cl/mps/core/textgen/textgen.iml>) |
|  M | [core/tool/environment/environment.iml](<C:/Codex/mps-j2cl/mps/core/tool/environment/environment.iml>) |
|  M | [core/typechecking/typechecking.iml](<C:/Codex/mps-j2cl/mps/core/typechecking/typechecking.iml>) |
|  M | [core/typesystemEngine/typesystemEngine.iml](<C:/Codex/mps-j2cl/mps/core/typesystemEngine/typesystemEngine.iml>) |
|  M | [core/vfs/vfs.iml](<C:/Codex/mps-j2cl/mps/core/vfs/vfs.iml>) |
|  M | [editor/actions-runtime/actions-runtime.iml](<C:/Codex/mps-j2cl/mps/editor/actions-runtime/actions-runtime.iml>) |
|  M | [editor/editor-api/editor-api.iml](<C:/Codex/mps-j2cl/mps/editor/editor-api/editor-api.iml>) |
|  M | [editor/editorlang-runtime/editorlang-runtime.iml](<C:/Codex/mps-j2cl/mps/editor/editorlang-runtime/editorlang-runtime.iml>) |
|  M | [editor/instrumentation/instrumentation.iml](<C:/Codex/mps-j2cl/mps/editor/instrumentation/instrumentation.iml>) |
|  M | [editor/intentions-runtime/intentions-runtime.iml](<C:/Codex/mps-j2cl/mps/editor/intentions-runtime/intentions-runtime.iml>) |
|  M | [editor/typesystemIntegration/typesystemIntegration.iml](<C:/Codex/mps-j2cl/mps/editor/typesystemIntegration/typesystemIntegration.iml>) |
|  M | [platform/psi/psi.iml](<C:/Codex/mps-j2cl/mps/platform/psi/psi.iml>) |
|  M | [platform/vfs-tracking/vfs-tracking.iml](<C:/Codex/mps-j2cl/mps/platform/vfs-tracking/vfs-tracking.iml>) |
|  M | [plugins/execution-api/execution-api.iml](<C:/Codex/mps-j2cl/mps/plugins/execution-api/execution-api.iml>) |
|  M | [plugins/mcp-tools/mcp-tools.iml](<C:/Codex/mps-j2cl/mps/plugins/mcp-tools/mcp-tools.iml>) |
|  M | [plugins/mps-console/mps-console.iml](<C:/Codex/mps-j2cl/mps/plugins/mps-console/mps-console.iml>) |
|  M | [plugins/mps-devkit/mps-devkit.iml](<C:/Codex/mps-j2cl/mps/plugins/mps-devkit/mps-devkit.iml>) |
|  M | [plugins/mps-httpsupport/mps-httpsupport.iml](<C:/Codex/mps-j2cl/mps/plugins/mps-httpsupport/mps-httpsupport.iml>) |
|  M | [plugins/mps-idea-integration/IDEAPlugin/IDEAPlugin.iml](<C:/Codex/mps-j2cl/mps/plugins/mps-idea-integration/IDEAPlugin/IDEAPlugin.iml>) |
|  M | [plugins/mps-memtool/mps-memtool.iml](<C:/Codex/mps-j2cl/mps/plugins/mps-memtool/mps-memtool.iml>) |
|  M | [plugins/mps-migration/migration-platform/migration-platform.iml](<C:/Codex/mps-j2cl/mps/plugins/mps-migration/migration-platform/migration-platform.iml>) |
|  M | [plugins/mps-navbar/mps-navbar.iml](<C:/Codex/mps-j2cl/mps/plugins/mps-navbar/mps-navbar.iml>) |
|  M | [plugins/mps-repository/mps-repository.iml](<C:/Codex/mps-j2cl/mps/plugins/mps-repository/mps-repository.iml>) |
|  M | [plugins/mps-samples/mps-samples.iml](<C:/Codex/mps-j2cl/mps/plugins/mps-samples/mps-samples.iml>) |
|  M | [startup/startup.iml](<C:/Codex/mps-j2cl/mps/startup/startup.iml>) |
|  M | [testbench/testbench.iml](<C:/Codex/mps-j2cl/mps/testbench/testbench.iml>) |
|  M | [workbench/mps-editor/mps-editor.iml](<C:/Codex/mps-j2cl/mps/workbench/mps-editor/mps-editor.iml>) |
|  M | [workbench/mps-ui/mps-ui.iml](<C:/Codex/mps-j2cl/mps/workbench/mps-ui/mps-ui.iml>) |
|  M | [workbench/mps-workbench/mps-workbench.iml](<C:/Codex/mps-j2cl/mps/workbench/mps-workbench/mps-workbench.iml>) |
|  M | [workbench/scripts-runtime/scripts-runtime.iml](<C:/Codex/mps-j2cl/mps/workbench/scripts-runtime/scripts-runtime.iml>) |
|  M | [workbench/typesystemUi/typesystemUi.iml](<C:/Codex/mps-j2cl/mps/workbench/typesystemUi/typesystemUi.iml>) |


## Complete shared package support file list

Includes browser assets, compatibility sources, patch sources, tooling, tests, documentation, manifests and root build files. Excludes the upstream tree, replacements already listed separately, local output directories, prepared dependency views, IDE state, Python caches, node_modules, Bazel output symlinks and MODULE.bazel.lock. Presence here describes the current package, not an assertion about when or by whom it was added.

| File | Role |
| --- | --- |
| [.bazelignore](<C:/Codex/mps-j2cl/.bazelignore>) | root build, selection, or project metadata |
| [.bazelrc](<C:/Codex/mps-j2cl/.bazelrc>) | root build, selection, or project metadata |
| [.bazelversion](<C:/Codex/mps-j2cl/.bazelversion>) | root build, selection, or project metadata |
| [.gitignore](<C:/Codex/mps-j2cl/.gitignore>) | root build, selection, or project metadata |
| [BUILD.bazel](<C:/Codex/mps-j2cl/BUILD.bazel>) | root build, selection, or project metadata |
| [KOTLIN-AND-EDITOR-DEPENDENCIES.md](<C:/Codex/mps-j2cl/KOTLIN-AND-EDITOR-DEPENDENCIES.md>) | documentation or compatibility manifest |
| [MODULE.bazel](<C:/Codex/mps-j2cl/MODULE.bazel>) | root build, selection, or project metadata |
| [MPS-EDITOR-J2CL-PLAN.md](<C:/Codex/mps-j2cl/MPS-EDITOR-J2CL-PLAN.md>) | documentation or compatibility manifest |
| [MPS-J2CL-PLAN.md](<C:/Codex/mps-j2cl/MPS-J2CL-PLAN.md>) | documentation or compatibility manifest |
| [README.md](<C:/Codex/mps-j2cl/README.md>) | documentation or compatibility manifest |
| [completion-matching-tests.bzl](<C:/Codex/mps-j2cl/completion-matching-tests.bzl>) | root build, selection, or project metadata |
| [dependency-views.json](<C:/Codex/mps-j2cl/dependency-views.json>) | root build, selection, or project metadata |
| [editor-action-sources.bzl](<C:/Codex/mps-j2cl/editor-action-sources.bzl>) | root build, selection, or project metadata |
| [editor-foundation-tests.bzl](<C:/Codex/mps-j2cl/editor-foundation-tests.bzl>) | root build, selection, or project metadata |
| [editor-jre-tests.bzl](<C:/Codex/mps-j2cl/editor-jre-tests.bzl>) | root build, selection, or project metadata |
| [editor-live-tests.bzl](<C:/Codex/mps-j2cl/editor-live-tests.bzl>) | root build, selection, or project metadata |
| [editor-runtime-tests.bzl](<C:/Codex/mps-j2cl/editor-runtime-tests.bzl>) | root build, selection, or project metadata |
| [financial-calculations.bzl](<C:/Codex/mps-j2cl/financial-calculations.bzl>) | root build, selection, or project metadata |
| [financial-sources.bzl](<C:/Codex/mps-j2cl/financial-sources.bzl>) | root build, selection, or project metadata |
| [intellij-stubs.bzl](<C:/Codex/mps-j2cl/intellij-stubs.bzl>) | root build, selection, or project metadata |
| [mps-browser-projects.bzl](<C:/Codex/mps-j2cl/mps-browser-projects.bzl>) | root build, selection, or project metadata |
| [mps-j2cl.iml](<C:/Codex/mps-j2cl/mps-j2cl.iml>) | root build, selection, or project metadata |
| [mps-overrides.bzl](<C:/Codex/mps-j2cl/mps-overrides.bzl>) | root build, selection, or project metadata |
| [mps-projects.bzl](<C:/Codex/mps-j2cl/mps-projects.bzl>) | root build, selection, or project metadata |
| [mps-rules.bzl](<C:/Codex/mps-j2cl/mps-rules.bzl>) | root build, selection, or project metadata |
| [source-lock.json](<C:/Codex/mps-j2cl/source-lock.json>) | root build, selection, or project metadata |
| [webawt-net.BUILD.bazel](<C:/Codex/mps-j2cl/webawt-net.BUILD.bazel>) | root build, selection, or project metadata |
| [browser/browser.iml](<C:/Codex/mps-j2cl/browser/browser.iml>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [browser/editor-demo.html](<C:/Codex/mps-j2cl/browser/editor-demo.html>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [browser/financial/BrowserModelReader.java](<C:/Codex/mps-j2cl/browser/financial/BrowserModelReader.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [browser/financial/FinancialClassLoader.java](<C:/Codex/mps-j2cl/browser/financial/FinancialClassLoader.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [browser/financial/FinancialDemo.java](<C:/Codex/mps-j2cl/browser/financial/FinancialDemo.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [browser/financial/FinancialLanguages.java](<C:/Codex/mps-j2cl/browser/financial/FinancialLanguages.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [browser/financial/PrimitiveModelResource.java](<C:/Codex/mps-j2cl/browser/financial/PrimitiveModelResource.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [browser/financial/RuntimeTypesLanguage.java](<C:/Codex/mps-j2cl/browser/financial/RuntimeTypesLanguage.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [browser/financial/entry.js](<C:/Codex/mps-j2cl/browser/financial/entry.js>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [browser/financial/index.html](<C:/Codex/mps-j2cl/browser/financial/index.html>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [browser/src/mpsj2cl/editor/BrowserEditorActionBindings.java](<C:/Codex/mps-j2cl/browser/src/mpsj2cl/editor/BrowserEditorActionBindings.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [browser/src/mpsj2cl/editor/BrowserEditorHost.java](<C:/Codex/mps-j2cl/browser/src/mpsj2cl/editor/BrowserEditorHost.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [browser/src/mpsj2cl/editor/BrowserEditorShortcuts.java](<C:/Codex/mps-j2cl/browser/src/mpsj2cl/editor/BrowserEditorShortcuts.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [browser/src/mpsj2cl/editor/BrowserMpsAssetData.java](<C:/Codex/mps-j2cl/browser/src/mpsj2cl/editor/BrowserMpsAssetData.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [browser/src/mpsj2cl/editor/BrowserMpsAssets.java](<C:/Codex/mps-j2cl/browser/src/mpsj2cl/editor/BrowserMpsAssets.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [browser/src/mpsj2cl/editor/BrowserResourceIcon.java](<C:/Codex/mps-j2cl/browser/src/mpsj2cl/editor/BrowserResourceIcon.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [browser/src/mpsj2cl/editor/BrowserSelectionFactories.java](<C:/Codex/mps-j2cl/browser/src/mpsj2cl/editor/BrowserSelectionFactories.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [browser/src/mpsj2cl/editor/BrowserStyleRegistry.java](<C:/Codex/mps-j2cl/browser/src/mpsj2cl/editor/BrowserStyleRegistry.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [compatibility/action-dispatcher.json](<C:/Codex/mps-j2cl/compatibility/action-dispatcher.json>) | documentation or compatibility manifest |
| [compatibility/browser-profile.json](<C:/Codex/mps-j2cl/compatibility/browser-profile.json>) | documentation or compatibility manifest |
| [compatibility/browser-projects.json](<C:/Codex/mps-j2cl/compatibility/browser-projects.json>) | documentation or compatibility manifest |
| [compatibility/editor-assets.json](<C:/Codex/mps-j2cl/compatibility/editor-assets.json>) | documentation or compatibility manifest |
| [compatibility/editor-companion-imports.json](<C:/Codex/mps-j2cl/compatibility/editor-companion-imports.json>) | documentation or compatibility manifest |
| [compatibility/editor-plan-inventory.json](<C:/Codex/mps-j2cl/compatibility/editor-plan-inventory.json>) | documentation or compatibility manifest |
| [compatibility/editor-shortcuts.json](<C:/Codex/mps-j2cl/compatibility/editor-shortcuts.json>) | documentation or compatibility manifest |
| [compatibility/financial-language-profile.json](<C:/Codex/mps-j2cl/compatibility/financial-language-profile.json>) | documentation or compatibility manifest |
| [compatibility/intellij-api.json](<C:/Codex/mps-j2cl/compatibility/intellij-api.json>) | documentation or compatibility manifest |
| [compatibility/intellij-usage.json](<C:/Codex/mps-j2cl/compatibility/intellij-usage.json>) | documentation or compatibility manifest |
| [compatibility/java-characters.json](<C:/Codex/mps-j2cl/compatibility/java-characters.json>) | documentation or compatibility manifest |
| [compatibility/java-identifiers.json](<C:/Codex/mps-j2cl/compatibility/java-identifiers.json>) | documentation or compatibility manifest |
| [compatibility/kernel-platform.json](<C:/Codex/mps-j2cl/compatibility/kernel-platform.json>) | documentation or compatibility manifest |
| [compatibility/kernel-utilities.json](<C:/Codex/mps-j2cl/compatibility/kernel-utilities.json>) | documentation or compatibility manifest |
| [compatibility/mps-projects.json](<C:/Codex/mps-j2cl/compatibility/mps-projects.json>) | documentation or compatibility manifest |
| [compatibility/regex-unicode.json](<C:/Codex/mps-j2cl/compatibility/regex-unicode.json>) | documentation or compatibility manifest |
| [docs/editor-foundation.md](<C:/Codex/mps-j2cl/docs/editor-foundation.md>) | documentation or compatibility manifest |
| [docs/editor-icon-resources.md](<C:/Codex/mps-j2cl/docs/editor-icon-resources.md>) | documentation or compatibility manifest |
| [docs/editor-runtime-adaptations.md](<C:/Codex/mps-j2cl/docs/editor-runtime-adaptations.md>) | documentation or compatibility manifest |
| [docs/financial-calculations.md](<C:/Codex/mps-j2cl/docs/financial-calculations.md>) | documentation or compatibility manifest |
| [docs/formatting.md](<C:/Codex/mps-j2cl/docs/formatting.md>) | documentation or compatibility manifest |
| [docs/intellij-browser-boundary.md](<C:/Codex/mps-j2cl/docs/intellij-browser-boundary.md>) | documentation or compatibility manifest |
| [docs/runtime-factories.md](<C:/Codex/mps-j2cl/docs/runtime-factories.md>) | documentation or compatibility manifest |
| [docs/ui-dependencies.md](<C:/Codex/mps-j2cl/docs/ui-dependencies.md>) | documentation or compatibility manifest |
| [docs/validation.md](<C:/Codex/mps-j2cl/docs/validation.md>) | documentation or compatibility manifest |
| [docs/web-model-loading-proposal.md](<C:/Codex/mps-j2cl/docs/web-model-loading-proposal.md>) | documentation or compatibility manifest |
| [docs/images/mps-j2cl-build-architecture.md](<C:/Codex/mps-j2cl/docs/images/mps-j2cl-build-architecture.md>) | documentation or compatibility manifest |
| [tests/tests.iml](<C:/Codex/mps-j2cl/tests/tests.iml>) | test fixture or browser test entry |
| [tests/api_basics/CoreApiTest.java](<C:/Codex/mps-j2cl/tests/api_basics/CoreApiTest.java>) | test fixture or browser test entry |
| [tests/api_basics/entry.js](<C:/Codex/mps-j2cl/tests/api_basics/entry.js>) | test fixture or browser test entry |
| [tests/api_basics/jvm-expected.txt](<C:/Codex/mps-j2cl/tests/api_basics/jvm-expected.txt>) | test fixture or browser test entry |
| [tests/class_support/ClassSupportTest.java](<C:/Codex/mps-j2cl/tests/class_support/ClassSupportTest.java>) | test fixture or browser test entry |
| [tests/class_support/FixtureTypes.java](<C:/Codex/mps-j2cl/tests/class_support/FixtureTypes.java>) | test fixture or browser test entry |
| [tests/class_support/InteropBoundaryTest.java](<C:/Codex/mps-j2cl/tests/class_support/InteropBoundaryTest.java>) | test fixture or browser test entry |
| [tests/class_support/entry.js](<C:/Codex/mps-j2cl/tests/class_support/entry.js>) | test fixture or browser test entry |
| [tests/class_support/jvm-expected.txt](<C:/Codex/mps-j2cl/tests/class_support/jvm-expected.txt>) | test fixture or browser test entry |
| [tests/classloader/ClassLoaderTest.java](<C:/Codex/mps-j2cl/tests/classloader/ClassLoaderTest.java>) | test fixture or browser test entry |
| [tests/classloader/ModuleClassLoaderTest.java](<C:/Codex/mps-j2cl/tests/classloader/ModuleClassLoaderTest.java>) | test fixture or browser test entry |
| [tests/classloader/entry.js](<C:/Codex/mps-j2cl/tests/classloader/entry.js>) | test fixture or browser test entry |
| [tests/completion_matching/CompletionMatchingTest.java](<C:/Codex/mps-j2cl/tests/completion_matching/CompletionMatchingTest.java>) | test fixture or browser test entry |
| [tests/completion_matching/entry.js](<C:/Codex/mps-j2cl/tests/completion_matching/entry.js>) | test fixture or browser test entry |
| [tests/core/KernelSmokeTest.java](<C:/Codex/mps-j2cl/tests/core/KernelSmokeTest.java>) | test fixture or browser test entry |
| [tests/core/entry.js](<C:/Codex/mps-j2cl/tests/core/entry.js>) | test fixture or browser test entry |
| [tests/dynamic_components/DynamicComponentGcTest.java](<C:/Codex/mps-j2cl/tests/dynamic_components/DynamicComponentGcTest.java>) | test fixture or browser test entry |
| [tests/dynamic_components/DynamicComponentLifecycleTest.java](<C:/Codex/mps-j2cl/tests/dynamic_components/DynamicComponentLifecycleTest.java>) | test fixture or browser test entry |
| [tests/dynamic_components/README.md](<C:/Codex/mps-j2cl/tests/dynamic_components/README.md>) | test fixture or browser test entry |
| [tests/dynamic_components/entry.js](<C:/Codex/mps-j2cl/tests/dynamic_components/entry.js>) | test fixture or browser test entry |
| [tests/editor_live/EditorLiveTest.java](<C:/Codex/mps-j2cl/tests/editor_live/EditorLiveTest.java>) | test fixture or browser test entry |
| [tests/editor_live/EditorResourceContracts.java](<C:/Codex/mps-j2cl/tests/editor_live/EditorResourceContracts.java>) | test fixture or browser test entry |
| [tests/editor_live/ExpressionLanguage.java](<C:/Codex/mps-j2cl/tests/editor_live/ExpressionLanguage.java>) | test fixture or browser test entry |
| [tests/editor_live/FindUsagesContracts.java](<C:/Codex/mps-j2cl/tests/editor_live/FindUsagesContracts.java>) | test fixture or browser test entry |
| [tests/editor_live/README.md](<C:/Codex/mps-j2cl/tests/editor_live/README.md>) | test fixture or browser test entry |
| [tests/editor_live/entry.js](<C:/Codex/mps-j2cl/tests/editor_live/entry.js>) | test fixture or browser test entry |
| [tests/editor_runtime/EditorRuntimeContractsTest.java](<C:/Codex/mps-j2cl/tests/editor_runtime/EditorRuntimeContractsTest.java>) | test fixture or browser test entry |
| [tests/editor_runtime/entry.js](<C:/Codex/mps-j2cl/tests/editor_runtime/entry.js>) | test fixture or browser test entry |
| [tests/editor_utilities/EditorUtilitiesTest.java](<C:/Codex/mps-j2cl/tests/editor_utilities/EditorUtilitiesTest.java>) | test fixture or browser test entry |
| [tests/editor_utilities/README.md](<C:/Codex/mps-j2cl/tests/editor_utilities/README.md>) | test fixture or browser test entry |
| [tests/editor_utilities/entry.js](<C:/Codex/mps-j2cl/tests/editor_utilities/entry.js>) | test fixture or browser test entry |
| [tests/editor_utilities/jvm-baseline.json](<C:/Codex/mps-j2cl/tests/editor_utilities/jvm-baseline.json>) | test fixture or browser test entry |
| [tests/editor_utilities/jvm-expected.txt](<C:/Codex/mps-j2cl/tests/editor_utilities/jvm-expected.txt>) | test fixture or browser test entry |
| [tests/formatting/FormattingCases.java](<C:/Codex/mps-j2cl/tests/formatting/FormattingCases.java>) | test fixture or browser test entry |
| [tests/formatting/FormattingGolden.java](<C:/Codex/mps-j2cl/tests/formatting/FormattingGolden.java>) | test fixture or browser test entry |
| [tests/formatting/FormattingTest.java](<C:/Codex/mps-j2cl/tests/formatting/FormattingTest.java>) | test fixture or browser test entry |
| [tests/formatting/GenerateFormattingGolden.java](<C:/Codex/mps-j2cl/tests/formatting/GenerateFormattingGolden.java>) | test fixture or browser test entry |
| [tests/formatting/entry.js](<C:/Codex/mps-j2cl/tests/formatting/entry.js>) | test fixture or browser test entry |
| [tests/icon_creation/IconCreationJvmTest.java](<C:/Codex/mps-j2cl/tests/icon_creation/IconCreationJvmTest.java>) | test fixture or browser test entry |
| [tests/icon_creation/IconCreationTest.java](<C:/Codex/mps-j2cl/tests/icon_creation/IconCreationTest.java>) | test fixture or browser test entry |
| [tests/icon_creation/entry.js](<C:/Codex/mps-j2cl/tests/icon_creation/entry.js>) | test fixture or browser test entry |
| [tests/intellij_stubs/IntelliJBrowserTest.java](<C:/Codex/mps-j2cl/tests/intellij_stubs/IntelliJBrowserTest.java>) | test fixture or browser test entry |
| [tests/intellij_stubs/IntelliJContractsTest.java](<C:/Codex/mps-j2cl/tests/intellij_stubs/IntelliJContractsTest.java>) | test fixture or browser test entry |
| [tests/intellij_stubs/entry.js](<C:/Codex/mps-j2cl/tests/intellij_stubs/entry.js>) | test fixture or browser test entry |
| [tests/jdom_util/JDOMUtilTest.java](<C:/Codex/mps-j2cl/tests/jdom_util/JDOMUtilTest.java>) | test fixture or browser test entry |
| [tests/jdom_util/entry.js](<C:/Codex/mps-j2cl/tests/jdom_util/entry.js>) | test fixture or browser test entry |
| [tests/kernel_utilities/KernelUtilitiesTest.java](<C:/Codex/mps-j2cl/tests/kernel_utilities/KernelUtilitiesTest.java>) | test fixture or browser test entry |
| [tests/kernel_utilities/README.md](<C:/Codex/mps-j2cl/tests/kernel_utilities/README.md>) | test fixture or browser test entry |
| [tests/kernel_utilities/entry.js](<C:/Codex/mps-j2cl/tests/kernel_utilities/entry.js>) | test fixture or browser test entry |
| [tests/kernel_utilities/jvm-baseline.json](<C:/Codex/mps-j2cl/tests/kernel_utilities/jvm-baseline.json>) | test fixture or browser test entry |
| [tests/kernel_utilities/jvm-expected.txt](<C:/Codex/mps-j2cl/tests/kernel_utilities/jvm-expected.txt>) | test fixture or browser test entry |
| [tests/keystroke/KeyStrokeTest.java](<C:/Codex/mps-j2cl/tests/keystroke/KeyStrokeTest.java>) | test fixture or browser test entry |
| [tests/keystroke/entry.js](<C:/Codex/mps-j2cl/tests/keystroke/entry.js>) | test fixture or browser test entry |
| [tests/keystroke/jvm-expected.txt](<C:/Codex/mps-j2cl/tests/keystroke/jvm-expected.txt>) | test fixture or browser test entry |
| [tests/modifiers/ModifierTest.java](<C:/Codex/mps-j2cl/tests/modifiers/ModifierTest.java>) | test fixture or browser test entry |
| [tests/modifiers/entry.js](<C:/Codex/mps-j2cl/tests/modifiers/entry.js>) | test fixture or browser test entry |
| [tests/openapi/IdentifierBoundaryCases.java](<C:/Codex/mps-j2cl/tests/openapi/IdentifierBoundaryCases.java>) | test fixture or browser test entry |
| [tests/openapi/SModelNameTest.java](<C:/Codex/mps-j2cl/tests/openapi/SModelNameTest.java>) | test fixture or browser test entry |
| [tests/openapi/entry.js](<C:/Codex/mps-j2cl/tests/openapi/entry.js>) | test fixture or browser test entry |
| [tests/openapi/jvm-baseline.json](<C:/Codex/mps-j2cl/tests/openapi/jvm-baseline.json>) | test fixture or browser test entry |
| [tests/openapi/jvm-expected.txt](<C:/Codex/mps-j2cl/tests/openapi/jvm-expected.txt>) | test fixture or browser test entry |
| [tests/query_methods/QueryMethodsTest.java](<C:/Codex/mps-j2cl/tests/query_methods/QueryMethodsTest.java>) | test fixture or browser test entry |
| [tests/query_methods/entry.js](<C:/Codex/mps-j2cl/tests/query_methods/entry.js>) | test fixture or browser test entry |
| [tests/regex/README.md](<C:/Codex/mps-j2cl/tests/regex/README.md>) | test fixture or browser test entry |
| [tests/regex/RegexSubsetTest.java](<C:/Codex/mps-j2cl/tests/regex/RegexSubsetTest.java>) | test fixture or browser test entry |
| [tests/regex/RegexUnicodeDump.java](<C:/Codex/mps-j2cl/tests/regex/RegexUnicodeDump.java>) | test fixture or browser test entry |
| [tests/regex/entry.js](<C:/Codex/mps-j2cl/tests/regex/entry.js>) | test fixture or browser test entry |
| [tests/regex/jvm-baseline.json](<C:/Codex/mps-j2cl/tests/regex/jvm-baseline.json>) | test fixture or browser test entry |
| [tests/regex/jvm-expected.txt](<C:/Codex/mps-j2cl/tests/regex/jvm-expected.txt>) | test fixture or browser test entry |
| [tests/runtime_factories/ModuleRuntimeFactoryTest.java](<C:/Codex/mps-j2cl/tests/runtime_factories/ModuleRuntimeFactoryTest.java>) | test fixture or browser test entry |
| [tests/runtime_factories/RuntimeTestLoaders.java](<C:/Codex/mps-j2cl/tests/runtime_factories/RuntimeTestLoaders.java>) | test fixture or browser test entry |
| [tests/runtime_factories/entry.js](<C:/Codex/mps-j2cl/tests/runtime_factories/entry.js>) | test fixture or browser test entry |
| [tests/runtime_support/CharacterBoundaryCases.java](<C:/Codex/mps-j2cl/tests/runtime_support/CharacterBoundaryCases.java>) | test fixture or browser test entry |
| [tests/runtime_support/CharacterSupportTest.java](<C:/Codex/mps-j2cl/tests/runtime_support/CharacterSupportTest.java>) | test fixture or browser test entry |
| [tests/runtime_support/RuntimeSupportTest.java](<C:/Codex/mps-j2cl/tests/runtime_support/RuntimeSupportTest.java>) | test fixture or browser test entry |
| [tests/runtime_support/entry.js](<C:/Codex/mps-j2cl/tests/runtime_support/entry.js>) | test fixture or browser test entry |
| [tests/runtime_support/jvm-expected.txt](<C:/Codex/mps-j2cl/tests/runtime_support/jvm-expected.txt>) | test fixture or browser test entry |
| [tests/runtime_support/jvm/CharacterDataOracle.java](<C:/Codex/mps-j2cl/tests/runtime_support/jvm/CharacterDataOracle.java>) | test fixture or browser test entry |
| [tests/runtime_support/jvm/UuidParseOracle.java](<C:/Codex/mps-j2cl/tests/runtime_support/jvm/UuidParseOracle.java>) | test fixture or browser test entry |
| [tests/smodel/ActionDispatcherFailureTest.java](<C:/Codex/mps-j2cl/tests/smodel/ActionDispatcherFailureTest.java>) | test fixture or browser test entry |
| [tests/smodel/IconResourceTest.java](<C:/Codex/mps-j2cl/tests/smodel/IconResourceTest.java>) | test fixture or browser test entry |
| [tests/smodel/ProcessCanceledException.java](<C:/Codex/mps-j2cl/tests/smodel/ProcessCanceledException.java>) | test fixture or browser test entry |
| [tests/smodel/SModelAdaptationTest.java](<C:/Codex/mps-j2cl/tests/smodel/SModelAdaptationTest.java>) | test fixture or browser test entry |
| [tests/smodel/entry.js](<C:/Codex/mps-j2cl/tests/smodel/entry.js>) | test fixture or browser test entry |
| [tests/swing_editor/BrowserSwingEditorTest.java](<C:/Codex/mps-j2cl/tests/swing_editor/BrowserSwingEditorTest.java>) | test fixture or browser test entry |
| [tests/swing_editor/SwingEditorControlsTest.java](<C:/Codex/mps-j2cl/tests/swing_editor/SwingEditorControlsTest.java>) | test fixture or browser test entry |
| [tests/swing_editor/SwingEditorTest.java](<C:/Codex/mps-j2cl/tests/swing_editor/SwingEditorTest.java>) | test fixture or browser test entry |
| [tests/swing_editor/controls-entry.js](<C:/Codex/mps-j2cl/tests/swing_editor/controls-entry.js>) | test fixture or browser test entry |
| [tests/swing_editor/controls-jvm-expected.txt](<C:/Codex/mps-j2cl/tests/swing_editor/controls-jvm-expected.txt>) | test fixture or browser test entry |
| [tests/swing_editor/entry.js](<C:/Codex/mps-j2cl/tests/swing_editor/entry.js>) | test fixture or browser test entry |
| [tests/swing_editor/jvm-expected.txt](<C:/Codex/mps-j2cl/tests/swing_editor/jvm-expected.txt>) | test fixture or browser test entry |
| [tests/swing_editor/native-entry.js](<C:/Codex/mps-j2cl/tests/swing_editor/native-entry.js>) | test fixture or browser test entry |
| [tests/swing_utilities/SwingUtilitiesTest.java](<C:/Codex/mps-j2cl/tests/swing_utilities/SwingUtilitiesTest.java>) | test fixture or browser test entry |
| [tests/swing_utilities/entry.js](<C:/Codex/mps-j2cl/tests/swing_utilities/entry.js>) | test fixture or browser test entry |
| [tests/swing_utilities/jvm-expected.txt](<C:/Codex/mps-j2cl/tests/swing_utilities/jvm-expected.txt>) | test fixture or browser test entry |
| [tests/typechecking/BrowserPlatformTest.java](<C:/Codex/mps-j2cl/tests/typechecking/BrowserPlatformTest.java>) | test fixture or browser test entry |
| [tests/typechecking/FoundationJreJvm.java](<C:/Codex/mps-j2cl/tests/typechecking/FoundationJreJvm.java>) | test fixture or browser test entry |
| [tests/typechecking/FoundationJreTest.java](<C:/Codex/mps-j2cl/tests/typechecking/FoundationJreTest.java>) | test fixture or browser test entry |
| [tests/typechecking/FoundationTimeOracle.java](<C:/Codex/mps-j2cl/tests/typechecking/FoundationTimeOracle.java>) | test fixture or browser test entry |
| [tests/typechecking/TypecheckingFoundationTest.java](<C:/Codex/mps-j2cl/tests/typechecking/TypecheckingFoundationTest.java>) | test fixture or browser test entry |
| [tests/typechecking/entry.js](<C:/Codex/mps-j2cl/tests/typechecking/entry.js>) | test fixture or browser test entry |
| [tests/typechecking/jre-entry.js](<C:/Codex/mps-j2cl/tests/typechecking/jre-entry.js>) | test fixture or browser test entry |
| [tests/typechecking/platform-entry.js](<C:/Codex/mps-j2cl/tests/typechecking/platform-entry.js>) | test fixture or browser test entry |
| [tests/typechecking/time-jvm-expected.txt](<C:/Codex/mps-j2cl/tests/typechecking/time-jvm-expected.txt>) | test fixture or browser test entry |
| [tests/util/UtilCompatibilityTest.java](<C:/Codex/mps-j2cl/tests/util/UtilCompatibilityTest.java>) | test fixture or browser test entry |
| [tests/util/entry.js](<C:/Codex/mps-j2cl/tests/util/entry.js>) | test fixture or browser test entry |
| [tests/vfs/ArchiveBoundaryTest.java](<C:/Codex/mps-j2cl/tests/vfs/ArchiveBoundaryTest.java>) | test fixture or browser test entry |
| [tests/vfs/VfsLexicalTest.java](<C:/Codex/mps-j2cl/tests/vfs/VfsLexicalTest.java>) | test fixture or browser test entry |
| [tests/vfs/entry.js](<C:/Codex/mps-j2cl/tests/vfs/entry.js>) | test fixture or browser test entry |
| [tests/vfs/jvm-baseline.json](<C:/Codex/mps-j2cl/tests/vfs/jvm-baseline.json>) | test fixture or browser test entry |
| [tests/vfs/jvm-expected.txt](<C:/Codex/mps-j2cl/tests/vfs/jvm-expected.txt>) | test fixture or browser test entry |
| [third_party/intellij-stubs/README.md](<C:/Codex/mps-j2cl/third_party/intellij-stubs/README.md>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/intellij-stubs/intellij-stubs.iml](<C:/Codex/mps-j2cl/third_party/intellij-stubs/intellij-stubs.iml>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/intellij-stubs/src/com/intellij/codeInsight/CodeInsightSettings.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/codeInsight/CodeInsightSettings.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/codeInsight/documentation/DocFontSizePopup.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/codeInsight/documentation/DocFontSizePopup.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/codeInsight/documentation/DocumentationFontSize.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/codeInsight/documentation/DocumentationFontSize.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/codeInsight/hint/HintManagerImpl.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/codeInsight/hint/HintManagerImpl.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/codeInsight/hint/HintUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/codeInsight/hint/HintUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/codeInsight/hint/LineTooltipRenderer.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/codeInsight/hint/LineTooltipRenderer.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/codeInsight/hint/TooltipController.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/codeInsight/hint/TooltipController.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/codeInsight/hint/TooltipGroup.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/codeInsight/hint/TooltipGroup.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/codeInsight/hint/TooltipRenderer.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/codeInsight/hint/TooltipRenderer.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/compat/BrowserApplication.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/compat/BrowserApplication.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/compat/BrowserComponentManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/compat/BrowserComponentManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/compat/BrowserEdtExecutor.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/compat/BrowserEdtExecutor.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/compat/BrowserIcons.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/compat/BrowserIcons.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/compat/BrowserMessageBus.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/compat/BrowserMessageBus.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/compat/BrowserProject.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/compat/BrowserProject.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/diagnostic/StartUpPerformanceService.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/diagnostic/StartUpPerformanceService.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/execution/process/ProcessIOExecutorService.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/execution/process/ProcessIOExecutorService.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/featureStatistics/FeatureUsageTracker.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/featureStatistics/FeatureUsageTracker.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/icons/AllIcons.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/icons/AllIcons.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/BrowserUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/BrowserUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/CopyPasteManagerEx.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/CopyPasteManagerEx.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/CopyProvider.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/CopyProvider.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/CutProvider.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/CutProvider.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/DataManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/DataManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/HelpTooltipManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/HelpTooltipManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/IdeBundle.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/IdeBundle.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/IdeEventQueue.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/IdeEventQueue.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/IdeTooltip.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/IdeTooltip.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/IdeTooltipManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/IdeTooltipManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/PasteProvider.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/PasteProvider.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/PowerSaveMode.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/PowerSaveMode.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/actions/RecentProjectsGroup.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/actions/RecentProjectsGroup.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/lightEdit/LightEditCompatible.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/lightEdit/LightEditCompatible.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/plugins/newui/EmptyCaret.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/plugins/newui/EmptyCaret.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/ui/AntialiasingType.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/ui/AntialiasingType.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/ui/UISettings.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/ui/UISettings.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/ui/UISettingsListener.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/ui/UISettingsListener.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ide/ui/customization/CustomActionsSchema.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ide/ui/customization/CustomActionsSchema.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/lang/documentation/DocumentationMarkup.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/lang/documentation/DocumentationMarkup.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/notification/Notification.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/notification/Notification.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/notification/NotificationType.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/notification/NotificationType.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/notification/Notifications.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/notification/Notifications.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/Disposable.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/Disposable.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionGroup.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionGroup.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionInGroup.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionInGroup.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionPlaces.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionPlaces.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionPopupMenu.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionPopupMenu.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionToolbar.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionToolbar.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionUiKind.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionUiKind.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionUpdateThread.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ActionUpdateThread.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/AnAction.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/AnAction.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/AnActionEvent.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/AnActionEvent.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/Anchor.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/Anchor.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/CommonDataKeys.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/CommonDataKeys.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/Constraints.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/Constraints.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/CustomShortcutSet.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/CustomShortcutSet.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/CustomizedDataContext.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/CustomizedDataContext.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DataContext.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DataContext.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DataKey.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DataKey.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DataMap.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DataMap.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DataProvider.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DataProvider.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DataSink.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DataSink.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DataSnapshotProvider.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DataSnapshotProvider.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DefaultActionGroup.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/DefaultActionGroup.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/IdeActions.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/IdeActions.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/KeyboardShortcut.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/KeyboardShortcut.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/PlatformDataKeys.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/PlatformDataKeys.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/Presentation.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/Presentation.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/Separator.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/Separator.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/Shortcut.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/Shortcut.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ShortcutSet.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ShortcutSet.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ToggleAction.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ToggleAction.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/UiDataProvider.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/UiDataProvider.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ex/ActionManagerEx.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ex/ActionManagerEx.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ex/ActionUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/ex/ActionUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/impl/ActionButton.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/impl/ActionButton.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/impl/SimpleDataContext.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/actionSystem/impl/SimpleDataContext.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/application/Application.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/application/Application.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/application/ApplicationListener.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/application/ApplicationListener.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/application/ApplicationManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/application/ApplicationManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/application/ModalityState.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/application/ModalityState.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/command/CommandEvent.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/command/CommandEvent.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/command/CommandListener.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/command/CommandListener.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/command/CommandProcessor.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/command/CommandProcessor.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/command/UndoConfirmationPolicy.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/command/UndoConfirmationPolicy.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/command/undo/BasicUndoableAction.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/command/undo/BasicUndoableAction.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/command/undo/DocumentReference.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/command/undo/DocumentReference.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/command/undo/UndoManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/command/undo/UndoManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/command/undo/UndoableAction.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/command/undo/UndoableAction.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/command/undo/UnexpectedUndoException.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/command/undo/UnexpectedUndoException.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/components/ApplicationComponent.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/components/ApplicationComponent.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/components/BaseComponent.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/components/BaseComponent.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/components/ComponentManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/components/ComponentManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/components/PersistentStateComponent.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/components/PersistentStateComponent.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/components/ProjectComponent.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/components/ProjectComponent.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/components/RoamingType.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/components/RoamingType.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/components/State.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/components/State.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/components/Storage.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/components/Storage.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/components/StoragePathMacros.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/components/StoragePathMacros.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/diagnostic/Logger.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/diagnostic/Logger.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/Document.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/Document.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/Editor.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/Editor.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/EditorCoreUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/EditorCoreUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/LogicalPosition.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/LogicalPosition.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/ScrollType.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/ScrollType.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/ScrollingModel.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/ScrollingModel.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/CodeInsightColors.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/CodeInsightColors.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/ColorKey.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/ColorKey.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/EditorColors.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/EditorColors.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/EditorColorsListener.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/EditorColorsListener.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/EditorColorsManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/EditorColorsManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/EditorColorsScheme.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/EditorColorsScheme.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/EditorColorsUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/EditorColorsUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/FontPreferences.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/FontPreferences.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/ModifiableFontPreferences.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/ModifiableFontPreferences.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/TextAttributesKey.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/colors/TextAttributesKey.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/event/VisibleAreaEvent.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/event/VisibleAreaEvent.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/event/VisibleAreaListener.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/event/VisibleAreaListener.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/ex/EditorSettingsExternalizable.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/ex/EditorSettingsExternalizable.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/ex/util/EditorUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/ex/util/EditorUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/impl/EditorCssFontResolver.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/impl/EditorCssFontResolver.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/impl/EditorHeaderComponent.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/impl/EditorHeaderComponent.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/impl/EditorImpl.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/impl/EditorImpl.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/impl/FontInfo.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/impl/FontInfo.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/impl/view/FontLayoutService.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/impl/view/FontLayoutService.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/markup/EffectType.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/markup/EffectType.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/editor/markup/TextAttributes.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/editor/markup/TextAttributes.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/extensions/AreaInstance.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/extensions/AreaInstance.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/extensions/ExtensionPointName.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/extensions/ExtensionPointName.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/extensions/PluginId.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/extensions/PluginId.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/fileEditor/FileEditor.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/fileEditor/FileEditor.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/fileEditor/FileEditorManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/fileEditor/FileEditorManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/keymap/Keymap.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/keymap/Keymap.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/keymap/KeymapManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/keymap/KeymapManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/keymap/KeymapUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/keymap/KeymapUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/options/FontSize.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/options/FontSize.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/progress/EmptyProgressIndicator.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/progress/EmptyProgressIndicator.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/progress/ProcessCanceledException.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/progress/ProcessCanceledException.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/progress/ProgressIndicator.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/progress/ProgressIndicator.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/progress/ProgressManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/progress/ProgressManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/progress/util/ProgressIndicatorBase.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/progress/util/ProgressIndicatorBase.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/project/DumbAware.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/project/DumbAware.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/project/DumbAwareAction.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/project/DumbAwareAction.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/project/DumbService.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/project/DumbService.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/project/IndexNotReadyException.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/project/IndexNotReadyException.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/project/Project.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/project/Project.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/ui/JBPopupMenu.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/JBPopupMenu.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/ui/MessageType.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/MessageType.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/ui/Messages.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/Messages.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/Balloon.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/Balloon.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/BalloonBuilder.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/BalloonBuilder.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/ComponentPopupBuilder.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/ComponentPopupBuilder.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/JBPopup.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/JBPopup.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/JBPopupFactory.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/JBPopupFactory.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/ListPopup.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/ListPopup.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/ListPopupStep.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/ListPopupStep.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/ListSeparator.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/ListSeparator.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/MnemonicNavigationFilter.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/MnemonicNavigationFilter.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/PopupStep.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/PopupStep.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/SpeedSearchFilter.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/SpeedSearchFilter.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/util/BaseListPopupStep.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/ui/popup/util/BaseListPopupStep.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/CheckedDisposable.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/CheckedDisposable.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/Computable.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/Computable.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/Condition.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/Condition.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/Disposer.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/Disposer.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/IconLoader.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/IconLoader.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/InvalidDataException.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/InvalidDataException.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/Key.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/Key.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/NlsActions.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/NlsActions.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/NlsSafe.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/NlsSafe.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/Pair.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/Pair.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/Ref.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/Ref.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/ScalableIcon.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/ScalableIcon.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/SystemInfo.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/SystemInfo.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/TextRange.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/TextRange.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/ThrowableComputable.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/ThrowableComputable.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/UserDataHolder.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/UserDataHolder.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/UserDataHolderBase.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/UserDataHolderBase.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/registry/Registry.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/registry/Registry.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/text/HtmlBuilder.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/text/HtmlBuilder.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/text/HtmlChunk.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/text/HtmlChunk.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/text/StringUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/text/StringUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/util/text/Strings.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/util/text/Strings.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/vfs/VirtualFile.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/vfs/VirtualFile.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/vfs/VirtualFileManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/vfs/VirtualFileManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/vfs/VirtualFileSystem.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/vfs/VirtualFileSystem.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/wm/IdeFocusManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/IdeFocusManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/wm/IdeFrame.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/IdeFrame.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/wm/IdeGlassPane.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/IdeGlassPane.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/wm/StatusBar.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/StatusBar.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/wm/ToolWindow.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/ToolWindow.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/wm/ToolWindowManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/ToolWindowManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/wm/WindowManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/WindowManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/wm/ex/StatusBarEx.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/ex/StatusBarEx.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/wm/ex/ToolWindowEx.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/ex/ToolWindowEx.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/openapi/wm/impl/IdeRootPane.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/openapi/wm/impl/IdeRootPane.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/psi/codeStyle/MinusculeMatcher.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/psi/codeStyle/MinusculeMatcher.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/psi/codeStyle/NameUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/psi/codeStyle/NameUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/reference/SoftReference.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/reference/SoftReference.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/CollectionListModel.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/CollectionListModel.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/ColorUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/ColorUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/DarculaColors.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/DarculaColors.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/DirtyUI.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/DirtyUI.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/Gray.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/Gray.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/HintHint.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/HintHint.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/HyperlinkLabel.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/HyperlinkLabel.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/IconManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/IconManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/IdeBorderFactory.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/IdeBorderFactory.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/JBColor.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/JBColor.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/LayeredIcon.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/LayeredIcon.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/LightColors.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/LightColors.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/LightweightHint.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/LightweightHint.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/NewUI.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/NewUI.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/ScrollPaneFactory.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/ScrollPaneFactory.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/SideBorder.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/SideBorder.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/SimpleColoredComponent.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/SimpleColoredComponent.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/SimpleTextAttributes.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/SimpleTextAttributes.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/UserActivityProviderComponent.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/UserActivityProviderComponent.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/awt/RelativePoint.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/awt/RelativePoint.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/components/Interpolable.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/components/Interpolable.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/components/JBLayeredPane.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/components/JBLayeredPane.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/components/JBList.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/components/JBList.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/components/JBScrollBar.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/components/JBScrollBar.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/components/JBScrollPane.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/components/JBScrollPane.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/components/panels/NonOpaquePanel.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/components/panels/NonOpaquePanel.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/content/Content.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/content/Content.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/content/ContentFactory.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/content/ContentFactory.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/content/ContentManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/content/ContentManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/paint/LinePainter2D.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/paint/LinePainter2D.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/popup/AbstractPopup.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/popup/AbstractPopup.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/popup/ClosableByLeftArrow.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/popup/ClosableByLeftArrow.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/scale/JBUIScale.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/scale/JBUIScale.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/ui/speedSearch/SpeedSearchUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/ui/speedSearch/SpeedSearchUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/unscramble/AnalyzeStacktraceUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/unscramble/AnalyzeStacktraceUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/Alarm.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/Alarm.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/AlarmFactory.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/AlarmFactory.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/ArrayUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ArrayUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/ConcurrencyUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ConcurrencyUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/IconUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/IconUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/IncorrectOperationException.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/IncorrectOperationException.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/LocalTimeCounter.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/LocalTimeCounter.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/ThrowableRunnable.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ThrowableRunnable.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/concurrency/EdtExecutorService.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/concurrency/EdtExecutorService.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/concurrency/annotations/RequiresEdt.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/concurrency/annotations/RequiresEdt.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/containers/ContainerUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/containers/ContainerUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/containers/FList.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/containers/FList.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/containers/SortedList.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/containers/SortedList.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/io/URLUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/io/URLUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/messages/MessageBus.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/messages/MessageBus.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/messages/MessageBusConnection.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/messages/MessageBusConnection.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/messages/SimpleMessageBusConnection.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/messages/SimpleMessageBusConnection.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/messages/Topic.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/messages/Topic.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/text/Matcher.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/text/Matcher.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/ui/AbstractLayoutManager.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/AbstractLayoutManager.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/ui/ButtonlessScrollBarUI.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/ButtonlessScrollBarUI.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/ui/JBDimension.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/JBDimension.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/ui/JBUI.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/JBUI.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/ui/MouseEventAdapter.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/MouseEventAdapter.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/ui/StartupUiUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/StartupUiUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/ui/UIUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/UIUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/ui/accessibility/ScreenReader.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/accessibility/ScreenReader.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/ui/update/MergingUpdateQueue.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/update/MergingUpdateQueue.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/ui/update/Update.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/ui/update/Update.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/xmlb/XmlSerializerUtil.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/XmlSerializerUtil.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/AbstractCollection.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/AbstractCollection.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/Attribute.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/Attribute.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/CollectionBean.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/CollectionBean.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/MapAnnotation.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/MapAnnotation.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/OptionTag.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/OptionTag.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/Property.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/Property.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/Tag.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/Tag.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/Text.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/Text.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/Transient.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/Transient.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/XCollection.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/XCollection.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/XMap.java](<C:/Codex/mps-j2cl/third_party/intellij-stubs/src/com/intellij/util/xmlb/annotations/XMap.java>) | runtime or demo sources/assets (detailed in dedicated inventory) |
| [third_party/j2cl/composed.patch](<C:/Codex/mps-j2cl/third_party/j2cl/composed.patch>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/composition.json](<C:/Codex/mps-j2cl/third_party/j2cl/composition.json>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/j2cl.iml](<C:/Codex/mps-j2cl/third_party/j2cl/j2cl.iml>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/RUNTIME-APIS.md](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/RUNTIME-APIS.md>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/api-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/api-support.patch>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/provenance.json](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/provenance.json>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/module-info.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/module-info.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/io/InputStream.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/io/InputStream.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/io/PrintStream.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/io/PrintStream.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/io/Reader.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/io/Reader.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/lang/Character.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/lang/Character.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/lang/Deprecated.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/lang/Deprecated.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/lang/LinkageError.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/lang/LinkageError.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/lang/Long.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/lang/Long.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/lang/NoClassDefFoundError.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/lang/NoClassDefFoundError.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/lang/NoSuchMethodException.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/lang/NoSuchMethodException.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/lang/OutOfMemoryError.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/lang/OutOfMemoryError.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/lang/String.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/lang/String.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/lang/System.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/lang/System.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/lang/VirtualMachineError.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/lang/VirtualMachineError.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/lang/ref/PhantomReference.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/lang/ref/PhantomReference.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/lang/ref/Reference.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/lang/ref/Reference.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/lang/ref/ReferenceQueue.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/lang/ref/ReferenceQueue.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/lang/ref/SoftReference.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/lang/ref/SoftReference.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/lang/ref/WeakReference.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/lang/ref/WeakReference.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/lang/reflect/InvocationTargetException.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/lang/reflect/InvocationTargetException.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/lang/reflect/Modifier.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/lang/reflect/Modifier.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/security/SecureRandom.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/security/SecureRandom.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/time/DateTimeException.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/time/DateTimeException.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/time/Duration.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/time/Duration.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/time/Instant.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/time/Instant.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/util/Collections.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/util/Collections.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/util/List.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/util/List.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/util/StringTokenizer.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/util/StringTokenizer.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/util/UUID.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/util/UUID.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/util/WeakHashMap.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/util/WeakHashMap.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/util/logging/LogRecord.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/util/logging/LogRecord.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/util/regex/JavaRegexUnicodeData.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/util/regex/JavaRegexUnicodeData.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/util/regex/Matcher.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/util/regex/Matcher.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/util/regex/Pattern.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/util/regex/Pattern.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/util/regex/PatternSyntaxException.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/util/regex/PatternSyntaxException.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/java/util/stream/Stream.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/java/util/stream/Stream.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/javaemul/internal/CharacterData.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/javaemul/internal/CharacterData.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/api-support/jre/java/javaemul/internal/MpsFormatter.java](<C:/Codex/mps-j2cl/third_party/j2cl/api-support/jre/java/javaemul/internal/MpsFormatter.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/mps-support/classloader-provenance.json](<C:/Codex/mps-j2cl/third_party/j2cl/mps-support/classloader-provenance.json>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/mps-support/classloader.patch](<C:/Codex/mps-j2cl/third_party/j2cl/mps-support/classloader.patch>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/mps-support/jre/java/java/lang/ClassLoader.java](<C:/Codex/mps-j2cl/third_party/j2cl/mps-support/jre/java/java/lang/ClassLoader.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/type-support/README.md](<C:/Codex/mps-j2cl/third_party/j2cl/type-support/README.md>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/j2cl/type-support/class-type-support.patch](<C:/Codex/mps-j2cl/third_party/j2cl/type-support/class-type-support.patch>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webawt/elemental2-1.3.2-w3c-css.patch](<C:/Codex/mps-j2cl/third_party/webawt/elemental2-1.3.2-w3c-css.patch>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webawt/view-provenance.json](<C:/Codex/mps-j2cl/third_party/webawt/view-provenance.json>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webawt/webawt.iml](<C:/Codex/mps-j2cl/third_party/webawt/webawt.iml>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webawt/src/main/java/java/awt/AWTKeyStroke.java](<C:/Codex/mps-j2cl/third_party/webawt/src/main/java/java/awt/AWTKeyStroke.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/webswing.iml](<C:/Codex/mps-j2cl/third_party/webswing/webswing.iml>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/AbstractButton.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/AbstractButton.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/BorderFactory.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/BorderFactory.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/BrowserInputMethodSupport.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/BrowserInputMethodSupport.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/BrowserPaintSurface.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/BrowserPaintSurface.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/BrowserRendererSnapshot.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/BrowserRendererSnapshot.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/DefaultListCellRenderer.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/DefaultListCellRenderer.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/ImageIcon.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/ImageIcon.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/JComponent.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JComponent.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/JDialog.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JDialog.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/JEditorPane.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JEditorPane.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/JLayeredPane.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JLayeredPane.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/JList.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JList.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/JMenu.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JMenu.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/JMenuItem.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JMenuItem.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/JPopupMenu.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JPopupMenu.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/JRootPane.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JRootPane.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/JScrollBar.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JScrollBar.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/JScrollPane.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JScrollPane.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/JViewport.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JViewport.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/JWindow.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/JWindow.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/KeyStroke.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/KeyStroke.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/LayoutFocusTraversalPolicy.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/LayoutFocusTraversalPolicy.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/RepaintManager.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/RepaintManager.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/Scrollable.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/Scrollable.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/SwingUtilities.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/SwingUtilities.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/border/CompoundBorder.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/border/CompoundBorder.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/border/MatteBorder.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/border/MatteBorder.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/event/CaretEvent.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/event/CaretEvent.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/event/CaretListener.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/event/CaretListener.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/event/EventListenerList.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/event/EventListenerList.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/event/HyperlinkEvent.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/event/HyperlinkEvent.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/event/HyperlinkListener.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/event/HyperlinkListener.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/event/MenuEvent.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/event/MenuEvent.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/event/MenuListener.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/event/MenuListener.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/event/PopupMenuEvent.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/event/PopupMenuEvent.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/event/PopupMenuListener.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/event/PopupMenuListener.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/plaf/MenuItemUI.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/plaf/MenuItemUI.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/plaf/ScrollBarUI.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/plaf/ScrollBarUI.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/plaf/basic/BasicButtonUI.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/plaf/basic/BasicButtonUI.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/plaf/basic/BasicMenuItemUI.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/plaf/basic/BasicMenuItemUI.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/plaf/basic/BasicScrollBarUI.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/plaf/basic/BasicScrollBarUI.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/text/DefaultCaret.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/text/DefaultCaret.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/text/DefaultEditorKit.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/text/DefaultEditorKit.java>) | external compatibility source, patch, build metadata, or provenance |
| [third_party/webswing/src/main/java/javax/swing/text/JTextComponent.java](<C:/Codex/mps-j2cl/third_party/webswing/src/main/java/javax/swing/text/JTextComponent.java>) | external compatibility source, patch, build metadata, or provenance |
| [tools/JavaCharacterData.java](<C:/Codex/mps-j2cl/tools/JavaCharacterData.java>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/JavaIdentifiers.java](<C:/Codex/mps-j2cl/tools/JavaIdentifiers.java>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/bazel.sh](<C:/Codex/mps-j2cl/tools/bazel.sh>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/bootstrap.ps1](<C:/Codex/mps-j2cl/tools/bootstrap.ps1>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/bootstrap.py](<C:/Codex/mps-j2cl/tools/bootstrap.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/check_editor_keycodes.py](<C:/Codex/mps-j2cl/tools/check_editor_keycodes.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/check_formatting.py](<C:/Codex/mps-j2cl/tools/check_formatting.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/check_generated.py](<C:/Codex/mps-j2cl/tools/check_generated.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/check_sources.py](<C:/Codex/mps-j2cl/tools/check_sources.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/compose_jre.py](<C:/Codex/mps-j2cl/tools/compose_jre.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/fetch_j2cl.py](<C:/Codex/mps-j2cl/tools/fetch_j2cl.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/inventory_browser.py](<C:/Codex/mps-j2cl/tools/inventory_browser.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/inventory_financial.py](<C:/Codex/mps-j2cl/tools/inventory_financial.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/inventory_intellij.py](<C:/Codex/mps-j2cl/tools/inventory_intellij.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/inventory_mps.py](<C:/Codex/mps-j2cl/tools/inventory_mps.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/make_api_patch.py](<C:/Codex/mps-j2cl/tools/make_api_patch.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/make_character_data.py](<C:/Codex/mps-j2cl/tools/make_character_data.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/make_classloader_patch.py](<C:/Codex/mps-j2cl/tools/make_classloader_patch.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/make_dynamic_components_patch.py](<C:/Codex/mps-j2cl/tools/make_dynamic_components_patch.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/make_regex_unicode_data.py](<C:/Codex/mps-j2cl/tools/make_regex_unicode_data.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/make_type_support_patch.py](<C:/Codex/mps-j2cl/tools/make_type_support_patch.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/make_vfs_patch.py](<C:/Codex/mps-j2cl/tools/make_vfs_patch.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/prepare_action_dispatcher.py](<C:/Codex/mps-j2cl/tools/prepare_action_dispatcher.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/prepare_api_basics.py](<C:/Codex/mps-j2cl/tools/prepare_api_basics.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/prepare_dependency_views.py](<C:/Codex/mps-j2cl/tools/prepare_dependency_views.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/prepare_editor_assets.py](<C:/Codex/mps-j2cl/tools/prepare_editor_assets.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/prepare_editor_companions.py](<C:/Codex/mps-j2cl/tools/prepare_editor_companions.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/prepare_editor_runtime.py](<C:/Codex/mps-j2cl/tools/prepare_editor_runtime.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/prepare_editor_shortcuts.py](<C:/Codex/mps-j2cl/tools/prepare_editor_shortcuts.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/prepare_financial_adapters.py](<C:/Codex/mps-j2cl/tools/prepare_financial_adapters.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/prepare_kernel_jre.py](<C:/Codex/mps-j2cl/tools/prepare_kernel_jre.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/prepare_kernel_platform.py](<C:/Codex/mps-j2cl/tools/prepare_kernel_platform.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/prepare_kernel_utilities.py](<C:/Codex/mps-j2cl/tools/prepare_kernel_utilities.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/prepare_module_reference.py](<C:/Codex/mps-j2cl/tools/prepare_module_reference.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/prepare_openapi.py](<C:/Codex/mps-j2cl/tools/prepare_openapi.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/prepare_pattern_util.py](<C:/Codex/mps-j2cl/tools/prepare_pattern_util.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/prepare_runtime_flags.py](<C:/Codex/mps-j2cl/tools/prepare_runtime_flags.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/probe_regex_anchors.mjs](<C:/Codex/mps-j2cl/tools/probe_regex_anchors.mjs>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/run_core_oracle.py](<C:/Codex/mps-j2cl/tools/run_core_oracle.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/run_core_oracles.py](<C:/Codex/mps-j2cl/tools/run_core_oracles.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/run_dynamic_components_oracle.py](<C:/Codex/mps-j2cl/tools/run_dynamic_components_oracle.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/run_editor_utilities_oracle.py](<C:/Codex/mps-j2cl/tools/run_editor_utilities_oracle.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/run_foundation_time_oracle.py](<C:/Codex/mps-j2cl/tools/run_foundation_time_oracle.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/run_intellij_contracts.py](<C:/Codex/mps-j2cl/tools/run_intellij_contracts.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/run_kernel_utilities_oracle.py](<C:/Codex/mps-j2cl/tools/run_kernel_utilities_oracle.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/run_keystroke_oracle.py](<C:/Codex/mps-j2cl/tools/run_keystroke_oracle.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/run_openapi_oracle.py](<C:/Codex/mps-j2cl/tools/run_openapi_oracle.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/run_regex_oracle.py](<C:/Codex/mps-j2cl/tools/run_regex_oracle.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/run_runtime_oracle.py](<C:/Codex/mps-j2cl/tools/run_runtime_oracle.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/run_swing_controls_oracle.py](<C:/Codex/mps-j2cl/tools/run_swing_controls_oracle.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/run_swing_editor_oracle.py](<C:/Codex/mps-j2cl/tools/run_swing_editor_oracle.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/run_swing_utilities_oracle.py](<C:/Codex/mps-j2cl/tools/run_swing_utilities_oracle.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/run_vfs_oracle.py](<C:/Codex/mps-j2cl/tools/run_vfs_oracle.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/serve_editor_demo.py](<C:/Codex/mps-j2cl/tools/serve_editor_demo.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/serve_financial_demo.py](<C:/Codex/mps-j2cl/tools/serve_financial_demo.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/summarize_validation.py](<C:/Codex/mps-j2cl/tools/summarize_validation.py>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/test_browser.mjs](<C:/Codex/mps-j2cl/tools/test_browser.mjs>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/test_dynamic_components.mjs](<C:/Codex/mps-j2cl/tools/test_dynamic_components.mjs>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/test_editor_demo.mjs](<C:/Codex/mps-j2cl/tools/test_editor_demo.mjs>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/test_editor_live.mjs](<C:/Codex/mps-j2cl/tools/test_editor_live.mjs>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/test_financial_demo.mjs](<C:/Codex/mps-j2cl/tools/test_financial_demo.mjs>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/test_foundation_gc.mjs](<C:/Codex/mps-j2cl/tools/test_foundation_gc.mjs>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/test_icon_png.mjs](<C:/Codex/mps-j2cl/tools/test_icon_png.mjs>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/test_weakset_gc.mjs](<C:/Codex/mps-j2cl/tools/test_weakset_gc.mjs>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/validate.ps1](<C:/Codex/mps-j2cl/tools/validate.ps1>) | inventory, adaptation, generation, build, or validation tooling |
| [tools/validate.py](<C:/Codex/mps-j2cl/tools/validate.py>) | inventory, adaptation, generation, build, or validation tooling |

