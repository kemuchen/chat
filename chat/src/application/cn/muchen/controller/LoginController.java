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
 * @author ����
 * 
 * @time 2018��12��17�� ����11:19:30
 *
 * @description ��¼������
 */
public class LoginController implements Initializable {

	/**
	 * username: �û���
	 */
	@FXML
	TextField username;

	/**
	 * password: ����
	 */
	@FXML
	TextField password;

	/**
	 * autoLogin: �Զ���¼��־
	 */
	@FXML
	CheckBox autoLogin;

	/**
	 * rememberPassword: ��ס����
	 */
	@FXML
	CheckBox rememberPassword;

	/**
	 * @Description ��ʼ��
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	/**
	 * @Description: �����¼�¼�
	 * @param event
	 * @throws IOException 
	 */
	public void handleSubmitButtonAction(ActionEvent event) throws IOException {
		this.afterLoginSuccess();
	}

	/**
	 * @Description: �����Զ���¼
	 * @param event
	 * @throws IOException 
	 */
	public void handleSelectAutoLogin(ActionEvent event) throws IOException {
		this.afterLoginSuccess();
	}

	/**
	 * @throws IOException 
	 * @Description: ��¼�ɹ�����
	 */
	private void afterLoginSuccess() throws IOException {
		// close login window
		Stage loginStage = (Stage) rememberPassword.getScene().getWindow();
		loginStage.close();
		
		Stage mainStage = new Stage();
		// ���ô����ޱ߿�
		mainStage.initStyle(StageStyle.TRANSPARENT);

		// open mainarea window
		Parent root = FXMLLoader.load(getClass().getResource("/application/cn/muchen/fxml/MainArea.fxml"));
		Scene scene = new Scene(root);
		mainStage.setScene(scene);
		mainStage.setX(900);
		mainStage.show();
		// �϶�������
		ChatUtil.addDragListener(mainStage, root);
	}

	/**
	 * @Description: �����ס����
	 * @param event
	 */
	public void handleRememberPassword(ActionEvent event) {

	}

	public void handleFindBackPassword(ActionEvent event) {

	}
}
