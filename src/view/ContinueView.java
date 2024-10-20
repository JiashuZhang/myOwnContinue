package view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import controller.ContinueController;
import org.eclipse.jface.text.Document;

public class ContinueView extends ViewPart {

    public static final String ID = "view.ContinueView";
    private TextViewer messageViewer;
    private Text inputText;
    private ContinueController controller;

    public ContinueView() {
        // 无参构造函数
    }

    @Override
    public void createPartControl(Composite parent) {
        controller = new ContinueController(this);

        GridLayout layout = new GridLayout(1, false);
        parent.setLayout(layout);

        // 上半部分：显示接收到的消息
        messageViewer = new TextViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        messageViewer.getTextWidget().setLayoutData(new GridData(GridData.FILL_BOTH));
        messageViewer.getTextWidget().setEditable(false);

        // 创建并设置Document
        Document document = new Document();
        messageViewer.setDocument(document);

        // 下半部分：输入框和按钮
        Composite inputComposite = new Composite(parent, SWT.NONE);
        inputComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        inputComposite.setLayout(new GridLayout(2, false));

        inputText = new Text(inputComposite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData inputGridData = new GridData(GridData.FILL_HORIZONTAL);
        inputGridData.heightHint = 60; // 设置固定高度
        inputGridData.verticalAlignment = GridData.FILL;
        inputGridData.grabExcessVerticalSpace = true;
        inputText.setLayoutData(inputGridData);

        // 添加键盘监听器
        inputText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    if ((e.stateMask & SWT.SHIFT) == 0) {
                        e.doit = false;
                        sendMessage();
                    }
                }
            }
        });

        Button sendButton = new Button(inputComposite, SWT.PUSH);
        sendButton.setText("发送");
        sendButton.addListener(SWT.Selection, event -> sendMessage());
    }

    private void sendMessage() {
        controller.sendMessage(inputText.getText());
    }

    public void clearInputText() {
        inputText.setText("");
    }

    public void receiveMessage(String message) {
        if (messageViewer != null && !messageViewer.getTextWidget().isDisposed()) {
            messageViewer.getTextWidget().getDisplay().asyncExec(() -> {
                String currentText = messageViewer.getDocument().get();
                messageViewer.getDocument().set(currentText + "\n" + message);
            });
        }
    }

    @Override
    public void setFocus() {
        inputText.setFocus();
    }
}
