package archtoring.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import damp.ekeko.EkekoNature;
import damp.ekeko.snippets.data.TemplateGroup;
import damp.ekeko.snippets.gui.TransformationEditor;
import damp.util.Natures;

public class OpenHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// get selected project
		IStructuredSelection selectedStructure = HandlerUtil.getCurrentStructuredSelection(event);
		// open file dialog
		Shell shell = HandlerUtil.getActiveShell(event);
		FileDialog dialog = new FileDialog(shell);
		dialog.setText("Select Search Template");
		String[] extensions = { "*.ekx" };
		dialog.setFilterExtensions(extensions);
		if (dialog.open() != null) {
			String filename = dialog.getFileName();
			if (filename == "")
				System.out.println("You didn't select any file.");
			else
				System.out.println("You chose " + dialog.getFilterPath() + "\\" + filename);
			// create template using selected file
			selectForEkeko(selectedStructure);
			TransformationEditor editor = new TransformationEditor();
			Object myTransformation = TransformationEditor.deserializeClojureTransformation(dialog.getFilterPath() + "\\" + filename);
			Object mySearchTemplate = editor.getLHSOfTransformation(myTransformation);
			TemplateGroup lhsTemplateGroup = TemplateGroup.newFromClojureGroup(mySearchTemplate);
			System.out.println("Resultados:" + lhsTemplateGroup.getResults());
		}
		return null;
	}

	/*
	 * Include selection in Ekeko queries
	 */
	private void selectForEkeko(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			for (Iterator<?> it = ((IStructuredSelection) selection).iterator(); it.hasNext();) {
				Object element = it.next();
				IProject project = null;
				if (element instanceof IProject)
					project = (IProject) element;
				else if (element instanceof IAdaptable)
					project = (IProject) ((IAdaptable) element).getAdapter(IProject.class);
				if (project != null) {
					try {
						// enable Ekeko nature if disabled
						if (project.hasNature(EkekoNature.NATURE_ID)) {
							// remove Soot nature first, because it requires Ekeko nature
							// if(project.hasNature(SootNature.NATURE_ID))
							// Natures.removeNature(project, SootNature.NATURE_ID);
							// Natures.removeNature(project, EkekoNature.NATURE_ID);
						} else
							Natures.addNature(project, EkekoNature.NATURE_ID);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}