package de.ancash.dragonsimulator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.output.NullOutputStream;

@SuppressWarnings("unused")
public class FileUtils {
  
  public static FileOutputStream openOutputStream(File paramFile) throws IOException {
    return openOutputStream(paramFile, false);
  }
  
  public static FileOutputStream openOutputStream(File paramFile, boolean paramBoolean) throws IOException {
    if (paramFile.exists()) {
      if (paramFile.isDirectory())
        throw new IOException("File '" + paramFile + "' exists but is a directory"); 
      if (!paramFile.canWrite())
        throw new IOException("File '" + paramFile + "' cannot be written to"); 
    } else {
      File file = paramFile.getParentFile();
      if (file != null && 
        !file.mkdirs() && !file.isDirectory())
        throw new IOException("Directory '" + file + "' could not be created"); 
    } 
    return new FileOutputStream(paramFile, paramBoolean);
  }
  
  public static void copyInputStreamToFile(InputStream paramInputStream, File paramFile) throws IOException {
    try {
      FileOutputStream fileOutputStream = openOutputStream(paramFile);
      try {
        IOUtils.copy(paramInputStream, fileOutputStream);
        fileOutputStream.close();
      } finally {
        IOUtils.closeQuietly(fileOutputStream);
      } 
    } finally {
      IOUtils.closeQuietly(paramInputStream);
    } 
  }    
}
