package world.cn.sinobest.file;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * 
 * @author 柯雷
 * 
 * @time 2018年12月26日 下午4:09:02
 *
 * @description 上传本地文件到HDFS
 */
public class UploadFileToHdfs {
	
	public static final String FS_DEFAULT_FS = "fs.defaultFS";
    public static final String HDFS_HOST = "hdfs://192.168.24.62:9000";
    public static final String CROSS_PLATFORM = "mapreduce.app-submission.cross-platform";

    
	public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
		Configuration conf = new Configuration();
		
		conf.setBoolean(CROSS_PLATFORM, true);
        conf.set(FS_DEFAULT_FS, HDFS_HOST);
        conf.set("HADOOP_USER_NAME", "root");

        GenericOptionsParser optionsParser = new GenericOptionsParser(conf, args);
        String[] remainingArgs = optionsParser.getRemainingArgs();
        if (remainingArgs.length < 2) {
            System.err.println("Usage: upload <source> <dest>");
            System.exit(2);
        }

        //要上传的源文件所在路径
        Path source = new Path(args[0]);
        //hadoop文件系统的跟目录
        Path dest = new Path(args[1]);

        FileSystem fileSystem = FileSystem.get(new URI("hdfs://master:9000/"), conf, "root");

        fileSystem.copyFromLocalFile(false, true, source, dest);
	}
}
