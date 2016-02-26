using Company.Department.Application;
using Company.Department.Presenter;
using Ninject.Core;
using Company.Core.HardwareAgents.Scanner;

namespace Company.My.Department
{
    public static class Program : BaseClass
    {
        public static void Main()
        {
            StandardApplication Application =
            new StandardApplication(new DepartmentApplication());
            BaseDepartmentPresenter DepartmentPresenter =
            new BaseDepartmentPresenter(Application.Get<IAgent>());
            DepartmentPresenter.Enabled = true;
            DepartmentPresenter.Show();
        }
    }
}
