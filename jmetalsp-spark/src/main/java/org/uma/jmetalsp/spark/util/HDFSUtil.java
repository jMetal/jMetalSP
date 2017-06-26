package org.uma.jmetalsp.spark.util;

/**
 * Created by cris on 20/04/2016.
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.util.List;
import java.util.logging.Logger;


/**
 * Created by cris on 09/03/2016.
 */
public class HDFSUtil {
  public static final String FS_DEFAULT = "fs.defaultFS";
  public static final String HDFS_IP = "master.semantic.khaos.uma.es";
  public static final int HDFS_PORT = 8020;
  private static final Logger log = Logger.getAnonymousLogger();
  private static HDFSUtil instance = null;
  private FileSystem fs = null;
  private Configuration conf = null;
  private static String hdfsIp = HDFS_IP;
  private static int port = HDFS_PORT;

  private HDFSUtil(String auxIp, int auxPort) {
    conf = new Configuration();
    hdfsIp=auxIp;
    port=auxPort;
    String defaultName = "hdfs://" + hdfsIp + ":" + port;
    log.info(defaultName);
    conf.set("fs.defaultFS", defaultName);
  }

  public static HDFSUtil getInstance(String hdfs, int portAux) {
    if (instance == null || (hdfsIp != hdfs) || port != portAux) {
      instance = new HDFSUtil(hdfs,portAux);
      hdfsIp = hdfs;
      port = portAux;
    }
    return instance;
  }

  public boolean copyToHDFS(String pathFileLocal, String pathFolderHdfs) {
    boolean result = false;
    try {
      log.info(pathFileLocal);
      log.info(pathFolderHdfs);
      fs = FileSystem.get(conf);
      Path src = new Path(pathFileLocal);
      Path dst = new Path(pathFolderHdfs);
//fs.copyFromLocalFile(b1, b2, src, dst);
      fs.copyFromLocalFile(src, dst);
      fs.close();
      result = true;
    } catch (Exception ex) {
      result = false;
      ex.printStackTrace();
    }
    return result;
  }

  public boolean copyListToHDFS(List<String> pathsFileLocal, String pathFolderHdfs) {
    boolean result = false;
    try {
      if (pathsFileLocal != null && !pathsFileLocal.isEmpty()) {
        fs = FileSystem.get(conf);

        Path dst = new Path(pathFolderHdfs);
        for (String pathFileLocal : pathsFileLocal) {
//fs.copyFromLocalFile(b1, b2, src, dst);
          Path src = new Path(pathFileLocal);
          fs.copyFromLocalFile(src, dst);
        }
        fs.close();
        result = true;
      }
    } catch (Exception ex) {
      result = false;
      ex.printStackTrace();
    }
    return result;
  }

  public boolean deleteAllFile(String pathFolder) {
    boolean result = false;
    try {
      fs = FileSystem.get(conf);
      Path path = new Path(pathFolder);
      FileStatus[] fileStatus = fs.listStatus(path);
      if (fileStatus != null && fileStatus.length > 0) {
        for (FileStatus file : fileStatus) {
          if (file.isDirectory()) {
            fs.delete(file.getPath(), true);
          } else {
            fs.delete(file.getPath(), false);
          }
        }
      }
      result = true;
      fs.close();
    } catch (Exception e) {
      e.printStackTrace();
      result = false;
    }
    return result;
  }

  public boolean deleteFolder(String pathFolder) {
    boolean result = false;
    try {
      fs = FileSystem.get(conf);
      Path path = new Path(pathFolder);
      if (fs.exists(path)) {
        if (fs.isDirectory(path)) {
          fs.delete(path, true);
        } else {
          fs.delete(path, false);
        }
      }
      result = true;
      fs.close();
    } catch (Exception e) {
      e.printStackTrace();
      result = false;
    }
    return result;
  }

  public boolean createFolder(String pathFolder) {
    boolean result = false;
    try {
      fs = FileSystem.get(conf);
      Path path = new Path(pathFolder);
      fs.mkdirs(path, new FsPermission(FsAction.ALL, FsAction.ALL, FsAction.ALL));
      result = true;
      fs.close();
    } catch (Exception e) {
      e.printStackTrace();
      result = false;
    }
    return result;
  }

  public boolean createFile(String pathFile) {
    boolean result = false;
    try {
      fs = FileSystem.get(conf);
      Path path = new Path(pathFile);
      if (!fs.exists(path)) {
        result = fs.createNewFile(path);
      }

    } catch (Exception ex) {
      result = false;
      ex.printStackTrace();
    }
    return result;
  }

  public boolean writeFile(String text, String path) {
    boolean result = false;
    if (text != null && path != null) {
      try {
        fs = FileSystem.get(conf);
        InputStream in = org.apache.commons.io.IOUtils.toInputStream(text, "UTF-8");
        OutputStream out = fs.create(new Path(path));
        IOUtils.copyBytes(in, out, conf);
        result = true;
      } catch (Exception ex) {
        ex.printStackTrace();
        result = false;
      }

    }
    return result;
  }

  /**
   * Carga cualquier fichero que se le pase el path
   *
   * @param path
   * @return Cadena de caracters con el fichero en cuesti??n
   */
  public String readFileHadoop(String path) {


    String resultado = null;
    try {
      FileSystem fs = FileSystem.get(conf);

      FileStatus[] status = fs.listStatus(new Path(path)); // you need to
      // pass in
      // your hdfs
      // path
      // este for solo debe de tener un elemento
      for (int i = 0; i < status.length; i++) {
        BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(fs.open(status[i].getPath())));
        resultado = "";
        int c;
        StringBuilder response = new StringBuilder();

        while ((c = bufferedReader.read()) != -1) {
          // Since c is an integer, cast it to a char. If it isn't -1,
          // it will be in the correct range of char.
          response.append((char) c);
        }
        resultado = response.toString();

      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return resultado;
  }

  public BufferedReader getBufferedReaderHDFS(String pathFile) {
    BufferedReader bufferedReader = null;
    try {
      FileSystem fs = FileSystem.get(conf);
      Path path = new Path(pathFile);
      bufferedReader = new BufferedReader(new InputStreamReader(fs.open(path)));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return bufferedReader;
  }

  public BufferedWriter getBufferedWriter(String path) {
    BufferedWriter bufferedWriter = null;
    try {
      FileSystem fs = FileSystem.get(conf);

      FileStatus[] status = fs.listStatus(new Path(path)); // you need to
      // pass in
      // your hdfs
      // path
      if (status != null && status.length > 0) {
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(fs.append(status[0].getPath())));
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return bufferedWriter;
  }

  public void createDataDirectory(String outputDirectoryName) {
    File outputDirectory = new File(outputDirectoryName);

    if (outputDirectory.isDirectory()) {
      System.out.println("The output directory exists. Deleting and creating ...");
      for (File file : outputDirectory.listFiles()) {
        file.delete();
      }
      outputDirectory.delete();
      new File(outputDirectoryName).mkdir();
    } else {
      System.out.println("The output directory doesn't exist. Creating ...");
      new File(outputDirectoryName).mkdir();
    }
  }

}

