package mypack;
import java.util.*;

import java.sql.*;
import java.sql.Date;
import java.text.*;

class Admin
{
	Railway r = new Railway();
	Connection c = DBConnection.createDBConnection();
	Scanner i = new Scanner(System.in);
    
	void cr_train_info() throws Exception
	{	
		String ch;
		do
		{
			train_info();
			System.out.print("Do you want to continue (y/n) : ");
			ch = i.next();
		}while(ch.equals("y"));
		r.admin_mode();
	}
	
	void train_info()
	{
		int num,seats;
		String name,bp,dp;
		int[] f = new int[4];
		
		System.out.print("Enter train number : ");
		num = i.nextInt();

		if(num<1000 || num>99999)
			System.out.println("Enter correct train number!!!");
		else
		{	
			System.out.print("Enter Train name for Train number "+num+" : ");
			name = i.next();
			java.sql.Date date = null;
			System.out.print("Enter date of train's journey in (yyyy-mm-dd) format : ");
			String dt = i.next();
			try
			{
				java.util.Date dt1 = new SimpleDateFormat("yyyy-MM-dd").parse(dt);
				date = new java.sql.Date(dt1.getTime());
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		
			System.out.print("Enter Boarding Point : "); 
			bp = i.next();
			System.out.print("Destination Point : ");
			dp = i.next();
			
			System.out.print("Enter Departure time of train's journey in (hh:mm:ss) format : "); 
			String dtime = i.next();
			
			System.out.print("Enter arrival time of train's journey in (hh:mm:ss) format : "); 
			String atime = i.next();
			
			System.out.print("Enter the total number of seats in the train : ");
			seats = i.nextInt();
		
			System.out.println("Enter price for each ticket type : ");
			System.out.print("First AC : "); 
			f[0] = i.nextInt();
			System.out.print("Second AC : "); 
			f[1] = i.nextInt();
			System.out.print("Third AC : "); 
			f[2] = i.nextInt();
			System.out.print("Sleeper Class : "); 
			f[3] = i.nextInt();
			
			train_db(num,name,seats,bp,dp,f[0],f[1],f[2],f[3],date,dtime,atime);
		}
	}

    void train_db(int tnum,String tname,int seats,String bp,String dp,int i,int j,int k,int l,java.sql.Date date,String dtime,String atime)
	{
		try
		{
			
			Statement s = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery("select * from train order by sno desc limit 1");
			PreparedStatement st = c.prepareStatement("insert into train (tnum,tname,seats,bp,dp,fAC,sAC,tAC,sc,doj,dtime,atime,sno) values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
			st.setInt(1,tnum);
			st.setString(2,tname);
			st.setInt(3,seats);
			st.setString(4,bp);
			st.setString(5,dp);	
			st.setInt(6,i);
			st.setInt(7,j);
			st.setInt(8,k);
			st.setInt(9,l);
			st.setDate(10,date);
			st.setString(11,dtime);
			st.setString(12,atime);
			if(r.first())
				st.setInt(13, r.getInt(13)+1);
			else
				st.setInt(13,1);	
			st.executeUpdate();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	void user_signup() throws Exception
	{
		String uname;
		String password;
		String gen;
		int age;
		System.out.print("Enter Username : ");
		uname = i.next();
		
		Statement s = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rl = s.executeQuery("select uname from User where uname = '"+uname+"'");
		if(!rl.first())		
		{
			System.out.print("Please Enter Your Password : ");
			password = i.next();
			System.out.print("Please Enter Your Age : ");
			age = i.nextInt();
			System.out.print("Enter Gender(M/F) : ");
			gen = i.next();
			user_db(uname,password,age,gen);
		}
		else
		System.out.println("Username alredy exist");
		System.out.print("Do you want to continue or return to main menu (y/n) respectively  : ");
		String ch = i.next();
		if(ch.equals("y"))
			r.user_mode();
		else
			r.main_menu();
	}
	
	int user_login() throws Exception
	{
		String uname;
		String password;

		System.out.print("Enter Username : ");
		uname = i.next();
		System.out.print("Please Enter Your Password : ");
		password = i.next();
		
		
		Statement s = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet r = s.executeQuery("select uname,pass from user");
		r.first();
		if(uname.equals(r.getString(1))&&password.equals(r.getString(2)))
			return 1;
		else
		{
			while(r.next())
			{
				if(uname.equals(r.getString(1))&&password.equals(r.getString(2)))
				return 1;
			}
		}
		return 0;
	}
		
	void user_db(String uname,String pass,int age,String g)
	{
		try
		{
			java.util.Date date = new java.util.Date();
			java.sql.Timestamp sqt = new java.sql.Timestamp(date.getTime());
			
			PreparedStatement st = c.prepareStatement("insert into user(uname,pass,age,g,timestamp) values(?,?,?,?,?)");
			st.setString(1,uname);
			st.setString(2,pass);
			st.setInt(3,age);
			st.setString(4,g);
			st.setTimestamp(5,sqt);				
			st.executeUpdate();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	void train_update_date() throws Exception
	{
		try
		{
			int tnum,d;
			
			System.out.print("Enter train Number whose date you want to update : ");
			tnum = i.nextInt();
			System.out.print("Enter number of days to be incremented : ");
			d = i.nextInt();	
			int seat = 0;
			
			Statement s = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery("select doj from train where tnum = "+tnum);
			r.first();
			Date date = r.getDate(1);
			
			r = s.executeQuery("select count(*) from chart where dot = '"+date+"' and tnum = "+tnum);
			r.first();
			seat = r.getInt(1);

			PreparedStatement st = c.prepareStatement("update train set doj = DATE_ADD(doj, INTERVAL '"+d+"' DAY) where tnum = '"+tnum+"'");
			st.execute();
			st = c.prepareStatement("update train set seats = seats +"+seat+" where tnum = '"+tnum+"'");
			st.execute();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		System.out.print("Do you want to continue or return to main menu (y/n) respectively  : ");
		String ch = i.next();
		if(ch.equals("y"))
			r.admin_mode();
		else
			r.main_menu();
	}			
	
	void dis_train_db()
	{
		disp_train_db();
		try
		{
			System.out.print("Do you want to continue or return to main menu (y/n) respectively  : ");
			String ch = i.next();
			if(ch.equals("y"))
				r.admin_mode();
			else
				r.main_menu();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	//tnum*****tname****seats******bp*******dp********fAC****sAC*****tAC******sc***doj*******dtime***atime*****sno
	void disp_train_db()
	{
		try
		{
				
					System.out.println("***************************************************************************************************************************************************************************************");
			System.out.println("Train Number   Train Name     Seats          Boarding       Destination    First AC       Second AC      Third AC       Sleeper Coach  Journey date   Departure      Arrival");
			System.out.println("***************************************************************************************************************************************************************************************");
			Statement st = c.createStatement();
			ResultSet r = st.executeQuery("select * from train");
			while(r.next())
			{
				setwR(r.getInt(1),r.getString(2),r.getInt(3),r.getString(4),r.getString(5),r.getInt(6),r.getInt(7),r.getInt(8),r.getInt(9),r.getDate(10),r.getString(11),r.getString(12),15);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

	void disp_chart() throws Exception
	{
		try
		{
			System.out.print("Enter the train number ");
			int tn = i.nextInt();	
				
			System.out.println("***************************************************************************************************************************************************************************************");
			System.out.println("PNR Number          Name                Age                 Gender              Seat Number         Coach               Boarding            Designation         Status              Booking time        Date of travelling");
			System.out.println("***************************************************************************************************************************************************************************************");
			Statement st = c.createStatement();
			ResultSet r = st.executeQuery("select * from chart where pnr>998 and tnum = '"+tn+"' ");
			while(r.next())
			{
				setwR(r.getInt(1),r.getString(2),r.getInt(3),r.getString(4),r.getInt(5),r.getString(6),r.getString(7),r.getString(8),r.getString(9),r.getTimestamp(10),r.getDate(11),20);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		System.out.print("Do you want to continue or return to main menu (y/n) respectively  : ");
		String ch = i.next();
		if(ch.equals("y"))
			r.admin_mode();
		else
			r.main_menu();
	}	
	
	void disp_User() throws Exception
	{
		try
		{
				
			System.out.println("***************************************************************************************************************************************************************************************");
			System.out.println("Username                 Age                      Gender                   Booking time");
			System.out.println("***************************************************************************************************************************************************************************************");		
			Statement st = c.createStatement();
			ResultSet r = st.executeQuery("select * from user");
			while(r.next())
			{
				setwR(r.getString(1),r.getInt(3),r.getString(4),r.getTimestamp(5),25);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		System.out.print("Do you want to continue or return to main menu (y/n) respectively  : ");
		String ch = i.next();
		if(ch.equals("y"))
			r.admin_mode();
		else
			r.main_menu();
	}	

	void setwR(String str,int age,String str3,Timestamp tm,int width)
	{
		int x;
		System.out.print(str);
		for (x = str.length(); x < width; ++x) 
		System.out.print(' ');		
		String str2 = Integer.toString(age);
		System.out.print(str2);
		for (x = str2.length(); x < width; ++x) 
		System.out.print(' ');	
		System.out.print(str3);
		for (x = str3.length(); x < width; ++x) 
		System.out.print(' ');	
		java.util.Date d = new java.util.Date();
		d.setTime(tm.getTime());
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String str7 = df.format(d);
		System.out.println(str7);
	}

	void setwR(int tnum, String str1, int seats,String str10,String str11, int fAc,int sAc,int tAc,int sc,java.util.Date doj, String str7,String str9, int width)
	{
		int x;
		String str = Integer.toString(tnum);
		System.out.print(str);
		for (x = str.length(); x < width; ++x) 
		System.out.print(' ');	
		System.out.print(str1);		
		for (x = str1.length(); x < width; ++x) 
		System.out.print(' ');		
		String str8 = Integer.toString(seats);
		System.out.print(str8);		
		for (x = str8.length(); x < width; ++x) 
		System.out.print(' ');		
		System.out.print(str10);		
		for (x = str10.length(); x < width; ++x) 
		System.out.print(' ');
		System.out.print(str11);		
		for (x = str11.length(); x < width; ++x) 
		System.out.print(' ');		
		String str2 = Integer.toString(fAc);
		System.out.print(str2);
		for (x = str2.length(); x < width; ++x) 
		System.out.print(' ');	
		String str3 = Integer.toString(sAc);
		System.out.print(str3);
		for (x = str3.length(); x < width; ++x) 
		System.out.print(' ');	
		String str4 = Integer.toString(tAc);
		System.out.print(str4);
		for (x = str4.length(); x < width; ++x) 
		System.out.print(' ');	
		String str5 = Integer.toString(sc);
		System.out.print(str5);
		for (x = str5.length(); x < width; ++x) 
		System.out.print(' ');
		

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String str6 = df.format(doj);
		System.out.print(str6);
		for (x = str6.length(); x < width; ++x) 
		System.out.print(' ');
		System.out.print(str7);
		for (x = str7.length(); x < width; ++x) 
		System.out.print(' ');
		System.out.println(str9);
	}

	void setwR(long pnr, String str1,int age,String str3, int seats,String str5, String str6, String str7, String str8, Timestamp tm,java.util.Date dot, int width)
	{
		int x;
		String str = Long.toString(pnr);
		System.out.print(str);
		for (x = str.length(); x < width; ++x) 
		System.out.print(' ');	
		System.out.print(str1);		
		for (x = str1.length(); x < width; ++x) 
		System.out.print(' ');	
		String str2 = Integer.toString(age);
		System.out.print(str2);
		for (x = str2.length(); x < width; ++x) 
		System.out.print(' ');	
		System.out.print(str3);
		for (x = str3.length(); x < width; ++x) 
		System.out.print(' ');	
		String str4 = Integer.toString(seats);
		System.out.print(str4);		
		for (x = str4.length(); x < width; ++x) 
		System.out.print(' ');		
		System.out.print(str5);
		for (x = str5.length(); x < width; ++x) 
		System.out.print(' ');		
		System.out.print(str6);
		for (x = str6.length(); x < width; ++x) 
		System.out.print(' ');
		System.out.print(str7);

		for(x = str7.length(); x < width; ++x)
		System.out.print(' ');
		System.out.print(str8);
		for(x = str8.length(); x < width; ++x)
		System.out.print(' ');
		java.util.Date d = new java.util.Date();
		d.setTime(tm.getTime());
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String str9 = df.format(d);
		System.out.print(str9);

		for (x = str9.length(); x < width; ++x) 
		System.out.print(' ');		
		DateFormat dF = new SimpleDateFormat("dd/MM/yyyy");
		String str10 = dF.format(dot);
		System.out.println(str10);
	}
}