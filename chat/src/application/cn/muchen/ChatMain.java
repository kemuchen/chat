package application.cn.muchen;

import application.cn.muchen.util.ChatUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * 
 * @author 柯雷
 * 
 * @time 2018年12月17日 上午10:29:45
 *
 * @description 聊天工具
 */
public class ChatMain extends Application {
	@Override
	public void start(Stage stage) {
		try {
			// Read file fxml and draw interface.
			Parent root = FXMLLoader.load(getClass().getResource("/application/cn/muchen/fxml/Login.fxml"));

			// 设置窗体不可改变大小
			stage.setResizable(false);
			// 设置窗体无边框
			stage.initStyle(StageStyle.TRANSPARENT);

			Scene scene = new Scene(root);

			stage.setScene(scene);
			stage.show();
			// 拖动监听器
			ChatUtil.addDragListener(stage, root);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
