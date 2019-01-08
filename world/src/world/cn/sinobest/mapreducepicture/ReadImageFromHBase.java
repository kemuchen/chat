package world.cn.sinobest.mapreducepicture;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author 柯雷
 * 
 * @time 2018年12月26日 下午3:04:29
 *
 * @description 从HBase中读取图片
 */
public class ReadImageFromHBase extends Configured implements Tool {

	private static Logger logger = LoggerFactory.getLogger(ReadImageFromHBase.class);
	
	/**
	 * 
	 * @author 柯雷
	 * 
	 * @time 2018年12月26日 下午3:06:26
	 *
	 * @description 从HBase中查询图片内容
	 */
	public static class ImageFromHbaseMapper extends TableMapper<Text, Text> {
		@Override
		protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
			boolean isContainsName = value.containsColumn("info".getBytes(), "name".getBytes());
			boolean isContainsImage = value.containsColumn("info".getBytes(), "image".getBytes());
			
			if (isContainsName && isContainsImage) {
				String name = Bytes.toString(value.getValue("info".getBytes(), "name".getBytes()));
				String image = Bytes.toString(value.getValue("info".getBytes(), "image".getBytes()));
				context.write(new Text(name), new Text(image));
			}
		}
	}
	
	/**
	 * 
	 * @author 柯雷
	 * 
	 * @time 2018年12月26日 下午3:08:33
	 *
	 * @description 将从HBase中查询的图片保存
	 */
	public static class ImageFromHbaseReducer extends Reducer<Text, Text, Text, Text> {
		@Override
		protected void reduce(Text key, Iterable<Text> value, Context context) throws IOException, InterruptedException {
			logger.info("学生：" + key.toString());
			
			for (Text image : value) {
				ImageUtil.base64ToImage(image.toString(), "F:\\Java\\work\\world\\student\\imageoutput\\" + key.toString() + ".jpg");
			}
			context.write(key, new Text("F:\\Java\\work\\world\\student\\imageoutput\\" + key.toString() + ".jpg"));
		}
	}
	
	@Override
	public int run(String[] arg0) throws Exception {
		Configuration configuration = HBaseConfiguration.create();
		
		// HBase配置
		configuration.set("hbase.master", "master:60000");
		configuration.set("hbase.zookeeper.quorum", "master:2181");
		configuration.set("HADOOP_USER_NAME", "root");
		
		FileSystem fileSystem = FileSystem.get(configuration);
		Job job = Job.getInstance(configuration);
		
		// job map-reduce配置
		job.setJarByClass(ReadImageFromHBase.class);
		job.setReducerClass(ImageFromHbaseReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		// 查询列族----info:name;info:image
		Scan scan = new Scan();
		scan.addColumn("info".getBytes(), "name".getBytes());
		scan.addColumn("info".getBytes(), "image".getBytes());
		
		TableMapReduceUtil.initTableMapperJob("student", scan, ImageFromHbaseMapper.class, Text.class, Text.class, job);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		Path outPutPath = new Path(arg0[0]);
		
		if (fileSystem.exists(outPutPath)) {
			fileSystem.delete(outPutPath, true);
		}
		
		FileOutputFormat.setOutputPath(job, outPutPath);
		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int run = ToolRunner.run(new ReadImageFromHBase(), args);
		System.exit(run);
	}
}
