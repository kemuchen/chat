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
 * @author ����
 * 
 * @time 2018��12��25�� ����4:37:34
 *
 * @description ��HBase���ݿ��ж�ȡ���ݣ������з��������������д��HDFS�ļ���
 */
public class ReadHbaseDataToHdfs extends Configured implements Tool {

	private static Logger logger = LoggerFactory.getLogger(ReadHbaseDataToHdfs.class);
	
	/**
	 * 
	 * @author ����
	 * 
	 * @time 2018��12��25�� ����4:40:40
	 *
	 * @description Map��������HBase���ݿ��ж�ȡ����
	 */
	public static class HBaseToHdfsMapper extends TableMapper<Text, MapWritable> {
		
		Text outKey = new Text("student");
		
		/**
		 * HBase���ݿ��ѯ�����key��HBase���еļ���value��HBase��key��Ӧ�е���������
		 * @throws InterruptedException 
		 * @throws IOException 
		 */
		@Override
		protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
			// contains info column
			boolean isContainsInfo = value.containsColumn("info".getBytes(), "age".getBytes());
			
			// contains score column
			boolean isContainsScore = value.containsColumn("score".getBytes(), "Math".getBytes());
			
			// outValue��ѧ���ɼ���Ϊ�������
			MapWritable outValue = new MapWritable();
			if (isContainsInfo) {
				String nameValue = Bytes.toString(value.getValue("info".getBytes(), "name".getBytes()));
				String sexValue = Bytes.toString(value.getValue("info".getBytes(), "sex".getBytes()));
				String ageValue = Bytes.toString(value.getValue("info".getBytes(), "age".getBytes()));
				logger.info("ѧ����Ϣ������-" + nameValue + "���Ա�-" + sexValue + "������-" + ageValue);
				
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
	 * @author ����
	 * 
	 * @time 2018��12��25�� ����4:42:10
	 *
	 * @description Reduce������
	 */
	public static class HBaseToHdfsReducer extends Reducer<Text, MapWritable, Text, Text> {	
		/**
		 * Reduce�������ݷ���
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
				
				studentInfo.append("������" + value.get(new Text("name"))).append("   �Ա�").append(value.get(new Text("sex")))
					.append("   ���䣺").append(value.get(new Text("age"))).append("  ��ѧ�ɼ�:").append(studentMathScore)
					.append("   Ӣ��ɼ���").append(studentEglishScore).append("  ���ĳɼ�:").append(studentChianeseScore)
					.append("  ƽ���ɼ���" + (studentMathScore + studentEglishScore + studentChianeseScore) / 3);
				context.write(new Text(value.get(new Text("name")).toString()), new Text(studentInfo.toString()));
			}
			
			context.write(new Text("student"), new Text("�ܼ�-----ƽ�����䣺" + (ageSum / studentCount) + 
					"    ��ѧƽ���ɼ���" + (mathScoreSum / studentCount) + "    Ӣ��ƽ���ɼ���" + (eglishScoreSum / studentCount) + 
					"    ����ƽ���ɼ���" + (chianeseScoreSum / studentCount)));
		}
	}
	
	/**
	 * Map-Reduce��������
	 */
	@Override
	public int run(String[] arg0) throws Exception {
		/** HBase���ݿ��������� */
		Configuration configuration = HBaseConfiguration.create();
		configuration.set("hbase.master", "192.168.24.62:60000");
		configuration.set("hbase.zookeeper.quorum", "master:2181");
		configuration.set("HADOOP_USER_NAME", "root");
		
		FileSystem fs = FileSystem.get(configuration);
		Job job = Job.getInstance(configuration);
		
		/** Job Map-Reduce���� */
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
