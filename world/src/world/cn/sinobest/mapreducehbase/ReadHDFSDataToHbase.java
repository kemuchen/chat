package world.cn.sinobest.mapreducehbase;

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
 * @time 2018年12月25日 下午4:34:25
 *
 * @description 从HDFS文件中读取数据存储到HBase数据库中
 */
public class ReadHDFSDataToHbase extends Configured implements Tool {

	private static Logger logger = LoggerFactory.getLogger(ReadHDFSDataToHbase.class);

	/**
	 * 
	 * @author 柯雷
	 * 
	 * @time 2018年12月25日 下午4:34:57
	 *
	 * @description Map操作
	 */
	public static class HdfsToHbaseMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			context.write(value, NullWritable.get());
		}
	}

	/**
	 * 
	 * @author 柯雷
	 * 
	 * @time 2018年12月25日 下午4:35:14
	 *
	 * @description Reduce操作
	 */
	public static class HdfsToHbaseReducer extends TableReducer<Text, NullWritable, NullWritable> {
		@Override
		protected void reduce(Text key, Iterable<NullWritable> values, Context context)
				throws IOException, InterruptedException {
			logger.info("学生信息：" + key.toString());
			String[] split = key.toString().split(",");

			Put put = new Put(split[0].getBytes());

			put.addColumn("info".getBytes(), "name".getBytes(), split[1].getBytes());
			put.addColumn("info".getBytes(), "sex".getBytes(), split[2].getBytes());
			put.addColumn("info".getBytes(), "age".getBytes(), split[3].getBytes());
			put.addColumn("score".getBytes(), split[4].getBytes(), split[5].getBytes());
			context.write(NullWritable.get(), put);
		}

	}

	/**
	 * map-reduce任务配置
	 */
	@Override
	public int run(String[] arg0) throws Exception {
		Configuration configuration = HBaseConfiguration.create();
		configuration.set("hbase.master", "192.168.24.62:60000");
		configuration.set("hbase.zookeeper.quorum", "master:2181");
		configuration.set("HADOOP_USER_NAME", "root");
		
		Job job = Job.getInstance(configuration);

		job.setJarByClass(ReadHDFSDataToHbase.class);
		job.setMapperClass(HdfsToHbaseMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(NullWritable.class);

		TableMapReduceUtil.initTableReducerJob("student", HdfsToHbaseReducer.class, job, null, null, null, null, false);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Put.class);

		Path inputPath = new Path(arg0[0]);

		FileInputFormat.setInputPaths(job, inputPath);

		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int run = ToolRunner.run(new ReadHDFSDataToHbase(), args);
		System.exit(run);
	}

}
