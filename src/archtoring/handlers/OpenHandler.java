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
import damp.ekeko.soot.SootNature;
import damp.util.Natures;
public class OpenHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//get selected file
		IStructuredSelection selectedStructure = HandlerUtil.getCurrentStructuredSelection(event);
		//IFile file = (IFile) selectedStructure.getFirstElement();
		//IPath path = file.getLocation();
		//open file dialog
		Shell shell = HandlerUtil.getActiveShell(event);
		FileDialog dialog = new FileDialog(shell);
		dialog.setText("Select Search Template");
		dialog.open();
		//create template using selected file
		selectForEkeko(selectedStructure);
		TemplateGroup template = TemplateGroup.newFromClojureGroup("C:\\Users\\Asistente\\Desktop\\EkekoToAcademia\\TemplatesEkekoX\\FindDoPost-MatchSet2.ekx");
		System.out.println(template.getResults());
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
						//enable Ekeko nature if disabled
						if(project.hasNature(EkekoNature.NATURE_ID)) {
							//remove Soot nature first, because it requires Ekeko nature
							if(project.hasNature(SootNature.NATURE_ID))
								Natures.removeNature(project, SootNature.NATURE_ID);
							Natures.removeNature(project, EkekoNature.NATURE_ID);
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