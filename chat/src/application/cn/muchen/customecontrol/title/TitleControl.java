package application.cn.muchen.customecontrol.title;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * 
 * @author ����
 * 
 * @time 2018��12��18�� ����2:03:31
 *
 * @description �Զ���ͷ��������
 */
public class TitleControl extends HBox {

	/**
	 * minBtn����С����ť
	 */
	@FXML
	Label minBtn;

	/**
	 * closeBtn���رհ�ť
	 */
	@FXML
	Label closeBtn;

	/**
	 * constructor�����캯��
	 */
	public TitleControl() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TitleControl.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	/**
	 * @Description������رհ�ť����¼�
	 */
	@FXML
	public void handleCloseBtnClicked() {
		Stage stage = (Stage) closeBtn.getScene().getWindow();
		stage.close();
	}

	/**
	 * @Description��������С����ť����¼�
	 */
	@FXML
	public void handleMinBtnClicked() {
		Stage stage = (Stage) closeBtn.getScene().getWindow();
		stage.setIconified(true);
	}
}
