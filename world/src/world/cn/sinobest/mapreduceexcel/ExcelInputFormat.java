package world.cn.sinobest.mapreduceexcel;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelInputFormat extends FileInputFormat<Text, Text> {

	private static Logger logger = LoggerFactory.getLogger(ExcelInputFormat.class);
	
	@Override
	public RecordReader<Text, Text> createRecordReader(InputSplit arg0, TaskAttemptContext arg1)
			throws IOException, InterruptedException {
		return new ExcelRecordReader();
	}

	public class ExcelRecordReader extends RecordReader<Text, Text> {

		private Text key = new Text("0");
		private Text value = new Text();
		private InputStream inputStream;
		private List<String> strArrayofLines;

		@Override
		public void initialize(InputSplit genericSplit, TaskAttemptContext context)
				throws IOException, InterruptedException {
			// 分片
			FileSplit split = (FileSplit) genericSplit;
			// 获取配置
			Configuration job = context.getConfiguration();

			// 分片路径
			Path filePath = split.getPath();

			FileSystem fileSystem = filePath.getFileSystem(job);

			inputStream = fileSystem.open(split.getPath());

			// 调用解析excel方法
			this.strArrayofLines = ExcelParser.parseExcelData(inputStream);
		}

		@Override
		public boolean nextKeyValue() throws IOException, InterruptedException {
			int pos = 0;
			try {
				pos = Integer.parseInt(key.toString()) + 1;
			} catch (Exception e) {
				logger.error("Integer.parseInt error" + e);
				System.err.println(key.toString());
				throw e;
			}
			

			if (pos < strArrayofLines.size()) {

				if (strArrayofLines.get(pos) != null) {
					key.set(pos + "");
					value.set(strArrayofLines.get(pos));
					return true;
				}
			}
			return false;
		}

		@Override
		public Text getCurrentKey() throws IOException, InterruptedException {
			return key;
		}

		@Override
		public Text getCurrentValue() throws IOException, InterruptedException {
			return value;
		}

		@Override
		public float getProgress() throws IOException, InterruptedException {
			return 0;
		}

		@Override
		public void close() throws IOException {
			if (inputStream != null) {
				inputStream.close();
			}
		}

	}

}
