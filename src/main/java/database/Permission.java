package database;

public class Permission {
	private boolean addQuestions;
	private boolean deleteQuestions;
	private boolean updateQuestions;
	private boolean viewQuestions;
	
	public boolean getAddQuestions() {
		return addQuestions;
	}
	
	public boolean getDeleteQuestions() {
		return deleteQuestions;
	}
	
	public boolean getUpdateQuestions() {
		return updateQuestions;
	}
	
	public boolean getViewQuestions() {
		return viewQuestions;
	}
	
	public void setAddQuestions(boolean addQuestions) {
		this.addQuestions = addQuestions;	
	}
	
	public void setDeleteQuestions(boolean deleteQuestions) {
		this.deleteQuestions = deleteQuestions;
	}
	
	public void setUpdateQuestions(boolean updateQuestions) {
		this.updateQuestions = updateQuestions;
	}
	
	public void setViewQuestions(boolean viewQuestions) {
		this.viewQuestions = viewQuestions;
	}
	
	public void updatePermissions(String permissionName, String resourceName, String actionType) {
		if (permissionName.equals("View Questions")) {
			setViewQuestions(true);
		}else if (permissionName.equals("Add Questions")) {
			setAddQuestions(true);
		}else if (permissionName.equals("Delete Questions")) {
			setDeleteQuestions(true);
		}else if (permissionName.equals("Update Questions")) {
			setUpdateQuestions(true);
		}
	}
}
