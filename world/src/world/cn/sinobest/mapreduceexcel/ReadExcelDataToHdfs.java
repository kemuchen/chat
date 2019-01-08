package world.cn.sinobest.mapreduceexcel;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author 柯雷
 * 
 * @time 2018年12月26日 上午10:50:20
 *
 * @description 从Excel中读取数据到Hdfs中，生成文本文件
 */
public class ReadExcelDataToHdfs extends Configured implements Tool {

	private static Logger logger = LoggerFactory.getLogger(ReadExcelDataToHdfs.class);
	
	/**
	 * 
	 * @author 柯雷
	 * 
	 * @time 2018年12月26日 上午11:10:55
	 *
	 * @description 从Excel中读取数据
	 */
	public static class ExcelToHdfsMapper extends Mapper<Text, Text, Text, Text> {
		@Override
		protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
			String[] ypjg = value.toString().split(",");
			// 用医院编号作为key进行分组处理
			context.write(new Text(ypjg[0]), new Text(key.toString() + "," + value.toString()));
		}
	}
	
	/**
	 * 
	 * @author 柯雷
	 * 
	 * @time 2018年12月26日 上午11:13:23
	 *
	 * @description
	 */
	public static class ExcelToHdfsReducer extends Reducer<Text, Text, Text, Text> {
		@Override
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			int ypjgNum = 0;
			for (Text ypjg : values) {
				ypjgNum++;
				StringBuilder ypjgxx = new StringBuilder();
				String[] ypjgs = ypjg.toString().split(",");
				ypjgxx.append("医院编号:").append(ypjgs[1]).append(",就诊记录号:").append(ypjgs[2])
					.append(",项目序号:").append(ypjgs[3]).append(",药品追溯码:").append(ypjgs[4])
					.append(",经办时间:").append(ypjgs[5]).append(",收单业务号:").append(ypjgs[6])
					.append(",追溯码状态:").append(ypjgs[7]).append(",追溯码状态描述:").append(ypjgs[8])
					.append(",医院项目编号:").append(ypjgs[9]).append(",时间戳:").append(ypjgs[10]);
				context.write(new Text(ypjgs[0]), new Text(ypjgxx.toString()));
			}
			logger.info("医院" + key.toString() + "共有" + ypjgNum + "条追溯码");
			context.write(key, new Text("医院" + key.toString() + "共有" + ypjgNum + "条追溯码"));
		}
	}
	
	@Override
	public int run(String[] arg0) throws Exception {
		Configuration conf = getConf();
		Job job = Job.getInstance(conf);
		job.setJarByClass(ReadExcelDataToHdfs.class);
		job.setJobName("readExcel");

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setMapperClass(ExcelToHdfsMapper.class);
		job.setReducerClass(ExcelToHdfsReducer.class);

		job.setInputFormatClass(ExcelInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		Path inputPath = new Path(arg0[0]);
		Path outputPath = new Path(arg0[1]);

		FileSystem fs = FileSystem.get(conf);
		if (fs.exists(outputPath)) {
			fs.delete(outputPath, true);
		}

		ExcelInputFormat.setInputPaths(job, inputPath);
		FileOutputFormat.setOutputPath(job, outputPath);

		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int ret = ToolRunner.run(new ReadExcelDataToHdfs(), args);
		System.exit(ret);
	}
}
