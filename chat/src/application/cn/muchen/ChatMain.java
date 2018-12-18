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
 * @author ����
 * 
 * @time 2018��12��17�� ����10:29:45
 *
 * @description ���칤��
 */
public class ChatMain extends Application {
	@Override
	public void start(Stage stage) {
		try {
			// Read file fxml and draw interface.
			Parent root = FXMLLoader.load(getClass().getResource("/application/cn/muchen/fxml/Login.fxml"));

			// ���ô��岻�ɸı��С
			stage.setResizable(false);
			// ���ô����ޱ߿�
			stage.initStyle(StageStyle.TRANSPARENT);

			Scene scene = new Scene(root);

			stage.setScene(scene);
			stage.show();
			// �϶�������
			ChatUtil.addDragListener(stage, root);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
