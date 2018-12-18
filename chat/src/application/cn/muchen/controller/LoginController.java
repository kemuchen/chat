package application.cn.muchen.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.cn.muchen.util.ChatUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 
 * @author 柯雷
 * 
 * @time 2018年12月17日 上午11:19:30
 *
 * @description 登录控制器
 */
public class LoginController implements Initializable {

	/**
	 * username: 用户名
	 */
	@FXML
	TextField username;

	/**
	 * password: 密码
	 */
	@FXML
	TextField password;

	/**
	 * autoLogin: 自动登录标志
	 */
	@FXML
	CheckBox autoLogin;

	/**
	 * rememberPassword: 记住密码
	 */
	@FXML
	CheckBox rememberPassword;

	/**
	 * @Description 初始化
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	/**
	 * @Description: 处理登录事件
	 * @param event
	 * @throws IOException 
	 */
	public void handleSubmitButtonAction(ActionEvent event) throws IOException {
		this.afterLoginSuccess();
	}

	/**
	 * @Description: 处理自动登录
	 * @param event
	 * @throws IOException 
	 */
	public void handleSelectAutoLogin(ActionEvent event) throws IOException {
		this.afterLoginSuccess();
	}

	/**
	 * @throws IOException 
	 * @Description: 登录成功后处理
	 */
	private void afterLoginSuccess() throws IOException {
		// close login window
		Stage loginStage = (Stage) rememberPassword.getScene().getWindow();
		loginStage.close();
		
		Stage mainStage = new Stage();
		// 设置窗体无边框
		mainStage.initStyle(StageStyle.TRANSPARENT);

		// open mainarea window
		Parent root = FXMLLoader.load(getClass().getResource("/application/cn/muchen/fxml/MainArea.fxml"));
		Scene scene = new Scene(root);
		mainStage.setScene(scene);
		mainStage.setX(900);
		mainStage.show();
		// 拖动监听器
		ChatUtil.addDragListener(mainStage, root);
	}

	/**
	 * @Description: 处理记住密码
	 * @param event
	 */
	public void handleRememberPassword(ActionEvent event) {

	}

	public void handleFindBackPassword(ActionEvent event) {

	}
}
