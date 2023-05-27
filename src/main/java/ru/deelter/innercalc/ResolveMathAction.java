package ru.deelter.innercalc;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class OpenMenuAction extends AnAction {

	@Override
	public void actionPerformed(@NotNull AnActionEvent e) {
		Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
		CaretModel model = editor.getCaretModel();
		String selectedText = model.getCurrentCaret().getSelectedText();

		System.out.println(selectedText);
		int result = calcString(selectedText);
		System.out.println(result);

		Project project = e.getRequiredData(CommonDataKeys.PROJECT);
		Document document = editor.getDocument();

		// Work off of the primary caret to get the selection info
		Caret primaryCaret = model.getPrimaryCaret();
		int start = primaryCaret.getSelectionStart();
		int end = primaryCaret.getSelectionEnd();

		// Replace the selection with a fixed string.
		// Must do this document change in a write action context.
		WriteCommandAction.runWriteCommandAction(project, () ->
				document.replaceString(start, end, String.valueOf(result))
		);
		// De-select the text range that was just replaced
		primaryCaret.removeSelection();
	}

	@Override
	public void update(@NotNull AnActionEvent e) {
		Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
		CaretModel model = editor.getCaretModel();
		e.getPresentation().setEnabledAndVisible(model.getCurrentCaret().hasSelection());
	}

	private int calcString(@NotNull String expression) {
		String[] operators = expression.split("[0-9]+");
		String[] operands = expression.split("[+-]");
		int agregate = Integer.parseInt(operands[0]);
		for (int i = 1; i < operands.length; i++) {
			if (operators[i].equals("+"))
				agregate += Integer.parseInt(operands[i]);
			else
				agregate -= Integer.parseInt(operands[i]);
		}
		return agregate;
	}
}
