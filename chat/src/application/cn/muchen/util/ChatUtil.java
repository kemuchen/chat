package application.cn.muchen.util;

import application.cn.muchen.listener.DragListener;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * 
 * @author ����
 * 
 * @time 2018��12��17�� ����10:49:38
 *
 * @description ������
 */
public class ChatUtil {
	
	/**
	 * @Description ��������ק������
	 * @param stage
	 * @param root
	 */
	public static void addDragListener(Stage stage, Node root) {
		new DragListener(stage).enableDrag(root);
	}
}
