package application.cn.muchen.customecontrol.title;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * 
 * @author 柯雷
 * 
 * @time 2018年12月18日 下午2:03:31
 *
 * @description 自定义头部标题栏
 */
public class TitleControl extends HBox {

	/**
	 * minBtn：最小化按钮
	 */
	@FXML
	Label minBtn;

	/**
	 * closeBtn：关闭按钮
	 */
	@FXML
	Label closeBtn;

	/**
	 * constructor：构造函数
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
	 * @Description：处理关闭按钮点击事件
	 */
	@FXML
	public void handleCloseBtnClicked() {
		Stage stage = (Stage) closeBtn.getScene().getWindow();
		stage.close();
	}

	/**
	 * @Description：处理最小化按钮点击事件
	 */
	@FXML
	public void handleMinBtnClicked() {
		Stage stage = (Stage) closeBtn.getScene().getWindow();
		stage.setIconified(true);
	}
}
