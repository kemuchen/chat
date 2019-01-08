package world.cn.sinobest.file;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * 
 * @author 柯雷
 * 
 * @time 2018年12月26日 下午5:14:06
 *
 * @description 从HDFS下载文件
 */
public class DownloadFileFromHdfs {

	public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
		// TODO Auto-generated method stub
		Configuration conf = new Configuration();

		conf.set("fs.defaultFS", "hdfs://master:9000");

		FileSystem fs = FileSystem.get(new URI("hdfs://master:9000/"), conf, "root");

		FSDataInputStream fsDataInputStream = fs.open(new Path("/home/hadoop/hadoop-2.7.7/hdfs/image.jpg"));

		FileOutputStream fileOutputStream = new FileOutputStream("e:/image.jpg");

		IOUtils.copy(fsDataInputStream, fileOutputStream);

	}
}
