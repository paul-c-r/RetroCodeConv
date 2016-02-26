
package Company.My.Department

import company.department.application;
import company.department.presenter;
import ninject.core;
import company.core.hardwareagents.scanner;
public class Program extends BaseClass
{
    public static void main(String[] args)
    {
        StandardApplication Application =
        new StandardApplication(new DepartmentApplication());
        BaseDepartmentPresenter DepartmentPresenter =
        new BaseDepartmentPresenter(Application.Get<IAgent>());
        DepartmentPresenter.Enabled = true;
        DepartmentPresenter.Show();
    }
}