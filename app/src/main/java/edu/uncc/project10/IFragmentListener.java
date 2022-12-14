package edu.uncc.project10;

public interface IFragmentListener {
    public void gotoGroupFragment(String email, String fName, String scoredGroupName);
    public void gotoQuestionnaire(String email, String fName, String studentGroupName);
}
