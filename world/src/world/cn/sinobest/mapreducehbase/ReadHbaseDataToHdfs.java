package world.cn.sinobest.mapreducehbase;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
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
 * @time 2018年12月25日 下午4:37:34
 *
 * @description 从HBase数据库中读取数据，并进行分析，将分析结果写到HDFS文件中
 */
public class ReadHbaseDataToHdfs extends Configured implements Tool {

	private static Logger logger = LoggerFactory.getLogger(ReadHbaseDataToHdfs.class);
	
	/**
	 * 
	 * @author 柯雷
	 * 
	 * @time 2018年12月25日 下午4:40:40
	 *
	 * @description Map操作：从HBase数据库中读取数据
	 */
	public static class HBaseToHdfsMapper extends TableMapper<Text, MapWritable> {
		
		Text outKey = new Text("student");
		
		/**
		 * HBase数据库查询结果：key是HBase中行的键，value是HBase中key对应行的所有数据
		 * @throws InterruptedException 
		 * @throws IOException 
		 */
		@Override
		protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
			// contains info column
			boolean isContainsInfo = value.containsColumn("info".getBytes(), "age".getBytes());
			
			// contains score column
			boolean isContainsScore = value.containsColumn("score".getBytes(), "Math".getBytes());
			
			// outValue：学生成绩作为输出数据
			MapWritable outValue = new MapWritable();
			if (isContainsInfo) {
				String nameValue = Bytes.toString(value.getValue("info".getBytes(), "name".getBytes()));
				String sexValue = Bytes.toString(value.getValue("info".getBytes(), "sex".getBytes()));
				String ageValue = Bytes.toString(value.getValue("info".getBytes(), "age".getBytes()));
				logger.info("学生信息：姓名-" + nameValue + "；性别-" + sexValue + "；年龄-" + ageValue);
				
				outValue.put(new Text("name"), new Text(nameValue));
				outValue.put(new Text("sex"), new Text(sexValue));
				outValue.put(new Text("age"), new Text(ageValue));
			}
			
			if (isContainsScore) {
				int mathValue = Integer.parseInt(Bytes.toString(value.getValue("score".getBytes(), "Math".getBytes())));
				int eglishValue = Integer.parseInt(Bytes.toString(value.getValue("score".getBytes(), "Eglish".getBytes())));
				int chianeseValue = Integer.parseInt(Bytes.toString(value.getValue("score".getBytes(), "Chianese".getBytes())));
				
				outValue.put(new Text("math"), new IntWritable(mathValue));
				outValue.put(new Text("eglish"), new IntWritable(eglishValue));
				outValue.put(new Text("chianese"), new IntWritable(chianeseValue));
			}
			
			context.write(outKey, outValue);
		}
	}
	
	/**
	 * 
	 * @author 柯雷
	 * 
	 * @time 2018年12月25日 下午4:42:10
	 *
	 * @description Reduce操作：
	 */
	public static class HBaseToHdfsReducer extends Reducer<Text, MapWritable, Text, Text> {	
		/**
		 * Reduce进行数据分析
		 * @throws InterruptedException 
		 * @throws IOException 
		 */
		@Override
		protected void reduce(Text key, Iterable<MapWritable> values, Context context) throws IOException, InterruptedException {
			int studentCount = 0;
			int ageSum = 0;
			
			// student info
			StringBuilder studentInfo;
			Integer studentMathScore = 0;
			Integer studentEglishScore = 0;
			Integer studentChianeseScore = 0;
			
			Integer mathScoreSum = 0;
			Integer eglishScoreSum = 0;
			Integer chianeseScoreSum = 0;
			
			for (MapWritable value : values) {
				studentInfo = new StringBuilder();
				studentCount++;
				ageSum += Integer.parseInt(value.get(new Text("age")).toString());
	
				studentMathScore = Integer.parseInt(value.get(new Text("math")).toString());
				studentEglishScore = Integer.parseInt(value.get(new Text("eglish")).toString());
				studentChianeseScore = Integer.parseInt(value.get(new Text("chianese")).toString());
				
				//studentMathScore
				mathScoreSum += studentMathScore;
				eglishScoreSum += studentEglishScore;
				chianeseScoreSum += studentChianeseScore;
				
				studentInfo.append("姓名：" + value.get(new Text("name"))).append("   性别：").append(value.get(new Text("sex")))
					.append("   年龄：").append(value.get(new Text("age"))).append("  数学成绩:").append(studentMathScore)
					.append("   英语成绩：").append(studentEglishScore).append("  语文成绩:").append(studentChianeseScore)
					.append("  平均成绩：" + (studentMathScore + studentEglishScore + studentChianeseScore) / 3);
				context.write(new Text(value.get(new Text("name")).toString()), new Text(studentInfo.toString()));
			}
			
			context.write(new Text("student"), new Text("总计-----平均年龄：" + (ageSum / studentCount) + 
					"    数学平均成绩：" + (mathScoreSum / studentCount) + "    英语平均成绩：" + (eglishScoreSum / studentCount) + 
					"    语文平均成绩：" + (chianeseScoreSum / studentCount)));
		}
	}
	
	/**
	 * Map-Reduce任务配置
	 */
	@Override
	public int run(String[] arg0) throws Exception {
		/** HBase数据库连接配置 */
		Configuration configuration = HBaseConfiguration.create();
		configuration.set("hbase.master", "192.168.24.62:60000");
		configuration.set("hbase.zookeeper.quorum", "master:2181");
		configuration.set("HADOOP_USER_NAME", "root");
		
		FileSystem fs = FileSystem.get(configuration);
		Job job = Job.getInstance(configuration);
		
		/** Job Map-Reduce设置 */
		job.setJarByClass(ReadHbaseDataToHdfs.class);
		job.setReducerClass(HBaseToHdfsReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(DoubleWritable.class);

		// info,age
        Scan scan = new Scan();
        //scan.addColumn("info".getBytes(), "age".getBytes());
        scan.addFamily("info".getBytes());
        scan.addFamily("score".getBytes());

		TableMapReduceUtil.initTableMapperJob("student".getBytes(), scan, HBaseToHdfsMapper.class, Text.class, MapWritable.class, job, false);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Put.class);

		Path outputPath = new Path(arg0[0]);
		if (fs.exists(outputPath)) {
			fs.delete(outputPath, true);
		}

		FileOutputFormat.setOutputPath(job, outputPath);

		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int run = ToolRunner.run(new ReadHbaseDataToHdfs(), args);
		System.exit(run);
	}
}
