package mypack;

import java.util.*;  
class Railway
{
	Scanner r = new Scanner(System.in);
	void main_menu() throws Exception
	{
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~MAIN MENU~~~~~~~~~~~~~~~~~~~~~~~~~\n1. Admin Mode\n2. User Mode\n3. Exit");
		int ch = r.nextInt();
		switch(ch)
		{
			case 1: admin_log();
				break;
			case 2: u_user_login();
				break;
			default:System.out.println("---x---");
				   System.exit(0);
				break;
		}	
	}

	void admin_log() throws Exception 
	{
		System.out.print("Enter password : ");
		String ps = r.next();
		if(ps.equals("dhinesh"))
			admin_mode();
		else
		{
			System.out.println("Wrong password contact to the creator!!!!!!");
			main_menu();
		}
	}

	void admin_mode() throws Exception
	{
		Admin ad = new  Admin();
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~ADMINISTRATOR MENU~~~~~~~~~~~~~~~~~~~~~~~~~\n1. Create detail database of trains\n2. Display all the database of trains\n3. Display Chart of a train\n4. Display all users\n5. Update train date\n6. Return to main menu \n7. Exit");
		int ch = r.nextInt();
		switch(ch)
		{
			case 1:ad.cr_train_info();
				break;
			case 2: ad.dis_train_db();
				break;
			case 3: ad.disp_chart();
				break;
			case 4: ad.disp_User();
				break;
			case 5:ad.train_update_date();
				break;		
			case 6:main_menu();
				break;	
			default:
				System.out.println("---x---");
				System.exit(0);
				break;
		}
	}
	
	void u_user_login() throws Exception
	{
		Admin ad = new  Admin();
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~WELCOME TO User MENU~~~~~~~~~~~~~~~~~~~~~~~~~\n1. Login\n2. Sign Up\n3. Return to main menu\n4. Exit");
		int ch = r.nextInt();
		switch(ch)
		{
			case 1:user_mode();
				break;
			case 2:ad.user_signup();
				break;
			case 3:main_menu();
				break;
			default:System.out.println("");
					System.exit(0);
				break;
		}
	}
	
	void user_mode() throws Exception
	{
		User us = new User();
		Admin ad = new  Admin();
		if(ad.user_login() == 1)
		{
			System.out.println("1. Book a ticket\n2. Cancel a ticket\n3. Enquiry\n4. Return to main menu\n5. Exit");
			int ch = r.nextInt();
			switch(ch)
			{
				case 1: us.inputreserve();
					break;
				case 2: us.cancel1();
					break;
				case 3: us.enquiry();
					break;
				case 4: main_menu();
					break;
				default:
					 System.exit(0);
					break;
			}
		}
		else
		{
			System.out.println("Wrong username or password!!!!!!");
			u_user_login();
		}
	}		
}