package application.cn.muchen.util;

import application.cn.muchen.listener.DragListener;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * 
 * @author 柯雷
 * 
 * @time 2018年12月17日 上午10:49:38
 *
 * @description 工具类
 */
public class ChatUtil {
	
	/**
	 * @Description 添加鼠标拖拽监听器
	 * @param stage
	 * @param root
	 */
	public static void addDragListener(Stage stage, Node root) {
		new DragListener(stage).enableDrag(root);
	}
}
