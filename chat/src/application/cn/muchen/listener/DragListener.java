package application.cn.muchen.listener;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * 
 * @author 柯雷
 * 
 * @time 2018年12月17日 上午10:53:21
 *
 * @description 鼠标拖拽事件监听器
 */
public class DragListener implements EventHandler<MouseEvent> {
	private double xOffset = 0;
	private double yOffset = 0;
	private final Stage stage;

	public DragListener(Stage stage) {
		this.stage = stage;
	}

	@Override
	public void handle(MouseEvent event) {
		event.consume();
		if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
			xOffset = event.getSceneX();
			yOffset = event.getSceneY();
		} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
			stage.setX(event.getScreenX() - xOffset);
			if (event.getScreenY() - yOffset < 0) {
				stage.setY(0);
			} else {
				stage.setY(event.getScreenY() - yOffset);
			}
		}
	}

	public void enableDrag(Node node) {
		node.setOnMousePressed(this);
		node.setOnMouseDragged(this);
	}
}
