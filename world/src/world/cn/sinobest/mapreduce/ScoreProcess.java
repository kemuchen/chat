package world.cn.sinobest.mapreduce;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * 
 * @author 柯雷
 * 
 * @time 2018年12月25日 下午2:51:06
 *
 * @description 通过map reduce算法计算学生平均分数
 */
public class ScoreProcess extends Configured implements Tool {

	/**
	 * 
	 * @author 柯雷
	 * 
	 * @time 2018年12月25日 下午4:36:00
	 *
	 * @description Map操作：从HDFS文件中读取数据
	 */
	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			System.out.println(line);

			StringTokenizer tokenizerArticle = new StringTokenizer(line, "\n");
			while (tokenizerArticle.hasMoreTokens()) {
				StringTokenizer tokenizerLine = new StringTokenizer(tokenizerArticle.nextToken());
				String strName = tokenizerLine.nextToken();
				String strScore = tokenizerLine.nextToken();

				Text name = new Text(strName); // name of student
				int scoreInt = Integer.parseInt(strScore); // score of student
				context.write(name, new IntWritable(scoreInt)); // 输出姓名和成绩
			}
		}
	}

	/**
	 * 
	 * @author 柯雷
	 * 
	 * @time 2018年12月25日 下午4:36:21
	 *
	 * @description Reduce操作：根据Map操作读取的数据求平均数并写道文件中
	 */
	public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {
		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			int count = 0;
			Iterator<IntWritable> iterable = values.iterator();

			while (iterable.hasNext()) {
				sum += iterable.next().get(); // 计算总分
				count++;
			}
			int average = (int) sum / count;
			context.write(key, new IntWritable(average));
		}
	}

	/**
	 * Map-Reduce任务配置
	 */
	@Override
	public int run(String[] arg0) throws Exception {
		Configuration conf = getConf();
		Job job = Job.getInstance(conf);
		job.setJarByClass(ScoreProcess.class);
		job.setJobName("score_process");

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setMapperClass(Map.class);
		job.setCombinerClass(Reduce.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		Path inputPath = new Path(arg0[0]);
		Path outputPath = new Path(arg0[1]);

		FileSystem fs = FileSystem.get(conf);
		if (fs.exists(outputPath)) {
			fs.delete(outputPath, true);
		}

		FileInputFormat.setInputPaths(job, inputPath);
		FileOutputFormat.setOutputPath(job, outputPath);

		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int ret = ToolRunner.run(new ScoreProcess(), args);
		System.exit(ret);
	}
}
