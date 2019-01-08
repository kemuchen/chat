package world.cn.sinobest.mapreducepicture;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author 柯雷
 * 
 * @time 2018年12月26日 下午1:57:55
 *
 * @description 读取图片保存在HBase中
 */
public class ReadImageToHBase extends Configured implements Tool {

	private static Logger logger = LoggerFactory.getLogger(ReadImageToHBase.class);
	/**
	 * 
	 * @author 柯雷
	 * 
	 * @time 2018年12月26日 下午1:59:09
	 *
	 * @description 读取图片配置信息
	 */
	public static class ImageToHbaseMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			context.write(value, NullWritable.get());
		}
	}
	
	/**
	 * 
	 * @author 柯雷
	 * 
	 * @time 2018年12月26日 下午1:59:37
	 *
	 * @description 处理图片保存
	 */
	public static class ImageToHbaseReducer extends TableReducer<Text, NullWritable, NullWritable> { 
		@Override
		protected void reduce(Text key, Iterable<NullWritable> value, Context context) throws IOException, InterruptedException {
			logger.info("学生图片信息为:" + key.toString());
			
			String[] images = key.toString().split(",");
			Put put = new Put(images[0].getBytes());
			put.addColumn("info".getBytes(), "image".getBytes(), ImageUtil.imageToBase64(images[1]).getBytes());
			context.write(NullWritable.get(), put);
		}
	}
	
	@Override
	public int run(String[] arg0) throws Exception {
		Configuration configuration = HBaseConfiguration.create();
		configuration.set("hbase.master", "192.168.24.62:60000");
		configuration.set("hbase.zookeeper.quorum", "master:2181");
		configuration.set("HADOOP_USER_NAME", "root");

		Job job = Job.getInstance(configuration);

		job.setJarByClass(ReadImageToHBase.class);
		job.setMapperClass(ImageToHbaseMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(NullWritable.class);

		TableMapReduceUtil.initTableReducerJob("student", ImageToHbaseReducer.class, job, null, null, null, null, false);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Put.class);

		Path inputPath = new Path(arg0[0]);

		FileInputFormat.setInputPaths(job, inputPath);

		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int run = ToolRunner.run(new ReadImageToHBase(), args);
		System.exit(run);
	}

}
