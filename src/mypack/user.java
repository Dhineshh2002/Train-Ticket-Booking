package mypack;
import java.util.*;
import java.sql.*;
import java.text.*;

class User
{
	Admin ad = new Admin();
	Railway r = new Railway();
	Connection c = DBConnection.createDBConnection();

	String tname,bp,dp;
	int tnum,seat,catnum,ch;
	String[] pname = new String[1000];
	int[] page = new int[1000];
	String[] pgen = new String[1000];
	
	Scanner a = new Scanner(System.in);
	
	int check1(int tnum) throws Exception
	{
		Statement s = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		ResultSet r = s.executeQuery("select * from train where tnum = '"+tnum+"' ");
		if(r.first())	
		    return 1;
		else
		    return 0;
	}
			
	void inputreserve() throws Exception
	{
		System.out.print("Enter train number : ");
		tnum = a.nextInt();
		if(check1(tnum) == 0)
		{
			System.out.println("Train number doesn't exist");
			r.user_mode();
		}
		
		System.out.print("Enter boarding : ");
		bp = a.next();
		
		System.out.print("Enter destination : ");
		dp = a.next();
		
		System.out.print("Number of seats required : ");
		seat = a.nextInt();

		java.sql.Date dt2 = null;
		try
		{
			System.out.print("Enter date of train's journey in (yyyy-mm-dd) format : ");
			String dt = a.next();
			java.util.Date dt1 = new SimpleDateFormat("yyyy-MM-dd").parse(dt);
			dt2 = new java.sql.Date(dt1.getTime());
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		int j = 0;
		int k = 0;
		for(int i = 0;i<seat;i++)
		{			
			System.out.print("Enter "+(i+1)+" passenger's name : ");
			pname[i] = a.next();
						
			System.out.print("Enter "+(i+1)+" passenger's age : ");
			page[i] = a.nextInt();
			if((page[i] > 0 && page[i] <= 12) || (page[i] >= 60 && page[i] < 120))
			k++;
			if(page[i] < 0 || page[i] > 120)
			{
				j = 1;
				System.out.println("Enter a valid age");
			}
			System.out.print("Enter "+(i+1)+" passenger's gender : ");
			pgen[i] = a.next();
		}
		if(j == 1)
		    return;
		
		System.out.println("Enter the class : ");
		System.out.println("1 - First AC");
		System.out.println("2 - Second AC");
		System.out.println("3 - Third AC");
		System.out.println("4 - Sleeper coach");
		ch = a.nextInt();
		if((ch != 1)&&(ch != 2)&&(ch != 3)&&(ch != 4))
		    System.out.println("Choose from above options only");
		else
		{
			String coach;
			if(ch == 1)  coach = "First AC";
			else if(ch == 2)  coach = "Second AC";
			else if(ch == 3)  coach = "Third AC";
			else  coach = "Sleeper Coach";

			int fare = reserve(tnum,tname,bp,dp,seat,ch);
			if(fare == 0)
			{
				System.out.println("Train number doesn't exist");
				r.user_mode();
			}
			System.out.println("Amount to be paid is "+(fare-(k*(fare/seat)*0.5)));
			chart(pname,page,coach,tnum,dt2);					
		}
	}
	int reserve(int tnum,String tname,String bp,String dp,int seat,int ch) 
	{ 
		int flag = 0;
		int fare = 0;
		try
		{
			
			Statement s = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r = s.executeQuery("select * from train");
			while(r.next())
			{
				if(tnum == r.getInt(1))	
				{			
					flag = 1;	
					if(ch == 1)
					fare = seat*r.getInt(6);
					else if(ch == 2)
					fare = seat*r.getInt(7);
					else if(ch == 3)	
					fare = seat*r.getInt(8);
					else			
					fare = seat*r.getInt(9);
					break;
				}
			}	
		}
		catch(Exception e)
		{
			System.out.println(e);	
		}
		if(flag == 0)
		    return 0;
		return fare;
	}

    void chart(String pname[],int page[],String coach,int tnum,java.sql.Date dt2)throws Exception
	{
		int i = 0;
		try
		{
			java.util.Date date = new java.util.Date();
			java.sql.Timestamp sqt = new java.sql.Timestamp(date.getTime());
			
			Statement s1 = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			Statement s2 = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			Statement s3 = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			Statement s4 = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			ResultSet r3 = s3.executeQuery("select * from chart order by sno desc limit 1");
			r3.first();
			
			for(i = 0;i<seat;i++)
			{	
				ResultSet r2 = s2.executeQuery("select * from chart order by sno desc limit 1");
				r2.first();
				ResultSet r1 = s1.executeQuery("select * from train where tnum = '"+tnum+"' and doj = '"+dt2+"' and bp = '"+bp+"' and dp = '"+dp+"'");
				
				if(r1.first()){
				PreparedStatement st = c.prepareStatement("insert into chart (pnr,name,age,gender,seatno,coach,bp,dp,status,timestamp,dot,tnum) values(?,?,?,?,?,?,?,?,?,?,?,?)");
				if(r3.first())
					st.setInt(1, r3.getInt(1)+1);
				else
					st.setInt(1, 999);
				st.setString(2,pname[i]);
				st.setInt(3,page[i]);
				st.setString(4,pgen[i]);
				if(r1.getInt(3) <=  0)	
					st.setInt(5,-1);	
				else if(r2.first())
				{
					ResultSet rr = s4.executeQuery("select * from chart where status = 'cancel' limit 1");
					if(rr.first())
					{
						st.setInt(5, rr.getInt(5));
						PreparedStatement psm = c.prepareStatement("delete from chart where status = 'cancel' limit 1");
						psm.executeUpdate();
					}
					else
						st.setInt(5,r2.getInt(5)+1);
				}			
				else						
					st.setInt(5,1);
				st.setString(6,coach);
				st.setString(7,bp);
				st.setString(8,dp);
				if(r1.getInt(3) > 0)
				{
					st.setString(9,"confirmed");
					PreparedStatement stm = c.prepareStatement("update train set seats = seats-1 where tnum = '"+tnum+"'");
					stm.execute();
				}
				else						
					st.setString(9,"waiting");
				st.setTimestamp(10,sqt);
				st.setDate(11,r1.getDate(10));
				st.setInt(12,tnum);	
				st.executeUpdate();	
				}
				else
					System.out.println("No train available");
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
			r.user_mode();
		}
		System.out.println("Congrats!!!! Your ticket is booked. Have a nice day!!");
		try
		{
			tckt1();
		}
		catch(Exception e)	
		{
			System.out.println(e);
		}	
	}
	void tckt1() throws Exception
	{
		tckt();
		System.out.print("Do you want to continue or return to main menu (y/n) respectively  : ");
		String ch = a.next();
		if(ch.equals("y"))
			r.user_mode();
		else
			r.main_menu();
	}
	
	void tckt() 
	{
		try
		{	
			
			Statement s1 = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r1 = s1.executeQuery("select * from chart order by sno desc limit 1");
			r1.first();		
			Statement s2 = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet r2 = s2.executeQuery("select * from chart where pnr = '"+r1.getInt(1)+"' ");
			r2.first();
			java.util.Date d = new java.util.Date();
			d.setTime(r2.getTimestamp(10).getTime());
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String str7 = df.format(d);		
			
			DateFormat dF = new SimpleDateFormat("dd/MM/yyyy");
			String str8 = dF.format(r2.getDate(11));
			System.out.println("***************************************************************************************");
			System.out.println("PNR Number  : "+r2.getInt(1)+"\t"+"      Coach : "+r2.getString(6));
			System.out.println("----------------------------------------");
			do
			{
				System.out.println("Name        : "+r2.getString(2)+"\n"+
								"Age         : "+r2.getInt(3)+"\n"+
								"Gender      : "+r2.getString(4)+"\n"+
								"Seat Number : "+r2.getInt(5)+"\n"+
								"Status      : "+r2.getString(9));
				System.out.println("----------------------------------------");
			}while(r2.next());
			System.out.println("Date of Travelling : "+str8+"\t\t"+"Booked on : "+str7);	
			System.out.println("***************************************************************************************");
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
	
	void cancel1() throws Exception
	{
		cancel();
		System.out.print("Do you want to continue or return to main menu (y/n) respectively  : ");
		String ch = a.next();
		if(ch.equals("y"))
			r.user_mode();
		else
			r.main_menu();
	}

	void cancel() throws Exception
	{
		long pnr;
			
		System.out.print("Enter PNR Number : ");
		pnr = a.nextInt();
		Statement stmt = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		Statement stmt2 = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		ResultSet r = stmt.executeQuery("select * from chart where pnr = '"+pnr+"'");
		
		if(r.first())
		{
			System.out.println("Your ticket has been canceled.");
			PreparedStatement st = c.prepareStatement("update chart set status = 'cancel' where pnr = '"+pnr+"' ");
			st.executeUpdate();
			st = c.prepareStatement("delete from chart where status = 'cancel' and seatno = -1");
			st.executeUpdate();
			ResultSet r2 = stmt2.executeQuery("select * from chart where status = 'cancel' and pnr = '"+pnr+"'");
			ResultSet r3 = stmt.executeQuery("select * from chart where status  = 'waiting'");
			if(r2.first() && r3.first())
			{
				do
				{
					st = c.prepareStatement("update chart set seatno = '"+r2.getInt(5)+"',status = 'confirmed' where status = 'waiting' limit 1");
					st.executeUpdate();
					PreparedStatement psm = c.prepareStatement("delete from chart where status = 'cancel' and seatno = '"+r2.getInt(5)+"'");
					psm.executeUpdate();
				}while(r2.next() && r3.next());
				r2.close();
				r3.close();
			}

			ResultSet r1 = stmt.executeQuery("select count(*), tnum from chart where pnr = '"+pnr+"' and status = 'cancel' group by tnum");
			int count = 0,tnum = 0;
			while(r1.next())
			{
				count = r1.getInt(1);
				tnum = r1.getInt(2);
			}
			st = c.prepareStatement("update train set seats = seats + "+count+" where tnum = '"+tnum+"'");
			st.executeUpdate();
			stmt.close();
			stmt2.close();
			c.close();
		}
		else
			System.out.println("PNR number does not exist");
	}	
	
	void enquiry1() throws Exception
	{
		enquiry();
		System.out.print("Do you want to continue or return to main menu (y/n) respectively : ");
		String ch = a.next();
		if(ch.equals("y"))
			r.user_mode();
		else
			r.main_menu();
	}
		
	void enquiry() throws Exception
	{
		System.out.print("From: ");
		String from = a.next();	
		System.out.print("To: ");
		String to = a.next();	
			
		System.out.println("***************************************************************************************************************************************************************************************");
		System.out.println("Train Number   Train Name     Seats          Boarding       Destination    First AC       Second AC      Third AC       Sleeper Coach  Journey date   DepartureTime  ArrivalTime");
		System.out.println("***************************************************************************************************************************************************************************************");

		Statement st = c.createStatement();
		ResultSet r = st.executeQuery("select * from train where bp = '"+from+"' and dp = '"+to+"' and doj >= CURDATE()");
		while(r.next())
		{
			setw(r.getInt(1),r.getString(2),r.getInt(3),r.getString(4),r.getString(5),r.getInt(6),r.getInt(7),r.getInt(8),r.getInt(9),r.getDate(10),r.getString(11),r.getString(12),15);
		}
	}

	void setw(int tnum, String str1, int seats,String str10,String str11, int fAc,int sAc,int tAc,int sc,java.sql.Date doj, String str7,String str9, int width)
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
}