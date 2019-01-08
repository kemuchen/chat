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
 * @author ����
 * 
 * @time 2018��12��26�� ����10:50:20
 *
 * @description ��Excel�ж�ȡ���ݵ�Hdfs�У������ı��ļ�
 */
public class ReadExcelDataToHdfs extends Configured implements Tool {

	private static Logger logger = LoggerFactory.getLogger(ReadExcelDataToHdfs.class);
	
	/**
	 * 
	 * @author ����
	 * 
	 * @time 2018��12��26�� ����11:10:55
	 *
	 * @description ��Excel�ж�ȡ����
	 */
	public static class ExcelToHdfsMapper extends Mapper<Text, Text, Text, Text> {
		@Override
		protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
			String[] ypjg = value.toString().split(",");
			// ��ҽԺ�����Ϊkey���з��鴦��
			context.write(new Text(ypjg[0]), new Text(key.toString() + "," + value.toString()));
		}
	}
	
	/**
	 * 
	 * @author ����
	 * 
	 * @time 2018��12��26�� ����11:13:23
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
				ypjgxx.append("ҽԺ���:").append(ypjgs[1]).append(",�����¼��:").append(ypjgs[2])
					.append(",��Ŀ���:").append(ypjgs[3]).append(",ҩƷ׷����:").append(ypjgs[4])
					.append(",����ʱ��:").append(ypjgs[5]).append(",�յ�ҵ���:").append(ypjgs[6])
					.append(",׷����״̬:").append(ypjgs[7]).append(",׷����״̬����:").append(ypjgs[8])
					.append(",ҽԺ��Ŀ���:").append(ypjgs[9]).append(",ʱ���:").append(ypjgs[10]);
				context.write(new Text(ypjgs[0]), new Text(ypjgxx.toString()));
			}
			logger.info("ҽԺ" + key.toString() + "����" + ypjgNum + "��׷����");
			context.write(key, new Text("ҽԺ" + key.toString() + "����" + ypjgNum + "��׷����"));
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
