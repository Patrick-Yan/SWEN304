//
///*
// * LibraryModel.java
// * Author: Yanzich 300476924
// * Created on: start at 5/10/2019
// */
//
//
//
//import static javax.swing.JOptionPane.ERROR_MESSAGE;
//import static javax.swing.JOptionPane.showMessageDialog;
//import static javax.swing.JOptionPane.showConfirmDialog;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.Date;
//
//import javax.swing.*;
//import javax.swing.JFrame;
//
//public class LibraryModel {
//
//	// For use in creating dialogs and making them modal
//	private JFrame dialogParent;
//	private Connection con;
//	/*
//      Constructor
//	 */
//	public LibraryModel(JFrame parent, String userid, String password) {
//		dialogParent = parent;
//		try {
//			Class.forName("org.postgresql.Driver");
//
//			con = DriverManager.getConnection("jdbc:postgresql://db.ecs.vuw.ac.nz/"+ userid+ "_jdbc",userid,password);
//			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			//System.out.print(e);
//			showMessageDialog(dialogParent,
//					e.toString(),
//					"Error information: ",
//					ERROR_MESSAGE);
//			System.exit(0);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			//System.out.print(e);
//			showMessageDialog(dialogParent,
//					e.toString(),
//					"Error information: ",
//					ERROR_MESSAGE);
//			System.exit(0);
//		}
//	}
//	/*
//    BookLookup
//	 */
//	public String bookLookup(int isbn) {
//		ResultSet r = null;
//		StringBuilder out = new StringBuilder("Book lookup:\n");
//		try {
//			r = query(String.format("SELECT book.isbn, title, edition_no, numofcop, numleft, "
//					+ "string_agg((author.surname || ' ' || author.name),'; ' ORDER BY book_author.authorseqno) AS authors "
//					//combine author name and surname and because they published more than one books so order them.
//					+ "FROM book "
//					+ "NATURAL JOIN book_author "
//					+ "NATURAL JOIN author "
//					+ "WHERE book.isbn = " //%d ?
//					+ isbn
//					+ "GROUP BY book.isbn "
//					+ "ORDER BY book.isbn", isbn));
//
//			if(r.next()) {
//				out.append(String.format(" %d: %s\n\t Edition %d - Copies: %d (%d left)\n\t",
//						r.getInt("isbn"), r.getString("title").trim(), r.getInt("edition_no"), r.getInt("numofcop"), r.getInt("numleft")));//get each value
//				//%d                %s                            %d                     %d                     %d
//				String authors = r.getString("authors");
//				if(authors != null && authors.trim().length() > 0) {////////////////////////
//					out.append("Authors: " + authors);
//				}else out.append("No authors.");
//			}else {
//				return "Book lookup:\n\tThere is no book with that ISBN";
//			}
//		} catch (SQLException e) {
//			return " Executing query fail: "+ e.getMessage();
//		}
//		return out.toString();
//	}
//	/*
//    showCatalogue
//	 */
//	public String showCatalogue() {
//		ResultSet r = null;
//		try {
//			r = query(String.format("SELECT book.isbn, title, edition_no, numofcop, numleft, string_agg((author.surname || ' ' || author.name), ', ' ORDER BY book_author.authorseqno) as authors "
//					+ "FROM book "
//					+ "NATURAL JOIN book_author "
//					+ "NATURAL JOIN author "
//					+ "GROUP BY book.isbn "
//					+ "ORDER BY book.isbn"));
//			StringBuilder out = new StringBuilder("Show Catalogue:\n");
//			while(r.next()) {
//				out.append(String.format("\n %d: %s\n\tEdition %d - Copies: %d (%d left)\n\t",
//						r.getInt("isbn"), r.getString("title").trim(), r.getInt("edition_no"), r.getInt("numofcop"), r.getInt("numleft")));
//				//%d                %s                            %d                     %d                     %d
//				String authors = r.getString("authors");
//				if(authors != null && authors.trim().length() > 0) {
//					out.append("Authors: " + authors);
//				}else out.append("No authors.");
//			}
//			return out.toString();
//		} catch (SQLException e) {
//			return "Executing query fail: %s"+ e.getMessage();
//		}
//	}
//	//=====================================================================
//	//=====================================================================
//	//=====================================================================
//
//
//	private ResultSet query(String query) throws SQLException {
//		Statement st = con.createStatement();
//		return st.executeQuery(query);
//	}
//
//	private int update(String query) throws SQLException {
//		Statement st = con.createStatement();
//		return st.executeUpdate(query);
//	}
//
//	//=====================================================================
//	//=====================================================================
//	//=====================================================================
//
//	public String showLoanedBooks() {
//		ResultSet r = null;
//		try {
//			r = query(String.format("SELECT book.isbn, book.title, book.edition_no, book.numofcop, book.numleft, string_agg(customer.customerid || ' : ' || customer.l_name || ' ' || customer.f_name, '; ') as customers " +
//					"FROM book " +
//					"NATURAL JOIN cust_book " +
//					"NATURAL JOIN customer " +
//					"GROUP BY book.isbn"));
//			StringBuilder out = new StringBuilder("Show Loaned Books:\n");
//			int i = 1;
//			while(r.next()) {
//				i++;
//				out.append(String.format("\t%d - %s (Ed. %d)\n\tNum copies: %d, Num left: %d\n\tBooks loaned to customers:\n", r.getInt("isbn"), r.getString("title").trim(), r.getInt("edition_no"), r.getInt("numofcop"), r.getInt("numleft")));
//				String[] custs = r.getString("customers").split(";");
//				for(String c : custs) {
//					out.append(String.format("\t\t%s\n", c));
//				}
//				out.append("\n");
//			}
//			if(i == 1) {
//				out.append("\t None. \n");
//			}
//			return out.toString();
//		} catch (SQLException e) {
//			return "Executing query fail: %s"+ e.getMessage();
//		}
//	}
//
//	public String showAuthor(int authorID) { 
//		ResultSet r = null;
//		try {
//			r = query(String.format("SELECT author.authorid, author.name, author.surname, "
//					+ "string_agg(book.isbn::varchar || ': ' || book.title || ' (Ed. ' || book.edition_no || ')', ', ') as books " +
//					"FROM author " +
//					"NATURAL JOIN book_author " +
//					"NATURAL JOIN book  " +
//					"WHERE author.authorid = %d" +
//					"GROUP BY author.authorid", authorID));
//			StringBuilder out = new StringBuilder("Show Author:\n");
//
//			int i = 1;
//			while(r.next()) {
//				i++;
//				out.append(String.format("\t%d - %s %s\n\tBooks written:\n",
//						r.getInt("authorid"), r.getString("name").trim(), r.getString("surname").trim()));
//				String books = r.getString("books");
//				if(books != null) {
//					String[] Books = books.split(", ");
//					for(String s : Books) {
//						out.append("\t\t" + s + "\n");
//					}
//				} else {
//					out.append("\t\tNone\n");
//				}
//				out.append("\n");
//			}
//			if(i == 1) {
//				return "ID is incrrorect";
//			}
//			return out.toString();
//		} catch (SQLException e) {
//			return "Executing query fail: %s"+ e.getMessage();
//		}
//	}
//
//	public String showAllAuthors() {
//		ResultSet r = null;
//		try {
//			r = query("SELECT * FROM author;");
//			StringBuilder out = new StringBuilder("Show All Authors:\n");
//			int i = 1 ;
//			while(r.next()) {
//				i++;
//				out.append(String.format("\t%d : %s %s\n",
//						r.getInt("authorid"), r.getString("name").trim(), r.getString("surname").trim()));
//			}
//			if(i == 1) {
//				return "There are no authors in the database.";
//			}
//			return out.toString();
//		} catch (SQLException e) {
//			return "Executing query fail: %s"+ e.getMessage();
//		}
//	}
//	public String showCustomer(int customerID) {
//		ResultSet r = null;
//		try {
//			r = query(String.format("SELECT customer.customerid, customer.l_name, customer.f_name, customer.city, string_agg(book.isbn || ' - f[Due: ' || cust_book.duedate || ']' || ' - ' || book.title || ' (Ed. ' || book.edition_no || ')', ', ') as loaned_books " +
//					"FROM customer " 
//					+ "LEFT OUTER JOIN cust_book ON (cust_book.customerid = customer.customerid) " 
//					+ "LEFT OUTER JOIN book ON (cust_book.isbn = book.isbn) " 
//					+ "WHERE customer.customerid = %d" 
//					+ "GROUP BY customer.customerid " 
//					+ "ORDER BY customer.customerid", customerID));
//			StringBuilder out = new StringBuilder("Show Customer:\n");
//			int i = 1;
//			while(r.next()) {
//				i++;
//				out.append(String.format("\t%d: %s %s - %s\n",
//						r.getInt("customerid"), r.getString("l_name"), r.getString("f_name"), r.getString("city")));
//				//%d                    %s                                      %s                                     %s                     %d
//				out.append("\tBooks borrowed:\n");
//				String loaned_books = r.getString("loaned_books");
//				if(loaned_books != null) {
//					String[] lbarr = loaned_books.split(", ");
//					for(String s : lbarr) {
//						out.append("\t\t" + s + "\n");
//					}
//				} else {
//					out.append("\t\tNone.\n");
//				}
//			}
//			if(i == 1 ) {
//				return "ID incorrect";
//			}
//			return out.toString();
//		} catch (SQLException e) {
//			return "Executing query: %s"+ e.getMessage();
//		}
//	}
//
//	public String showAllCustomers() {
//		ResultSet r = null;
//		try {
//			r = query(String.format("SELECT * FROM customer;" ));
//			StringBuilder out = new StringBuilder("Show Customer:\n");
//			int i = 1;
//			while(r.next()) {
//				i++;
//				out.append(String.format("\t%d: %s %s - %s\n",
//						r.getInt("customerid"), r.getString("l_name"), r.getString("f_name"), r.getString("city")));
//				//%d                    %s                                      %s                                     %s                     %d
//			}
//			if(i == 1) {
//				return "No customers found";
//			}
//			return out.toString();
//		} catch (SQLException e) {
//			return "Executing query fail: %s"+ e.toString();
//		}
//	}
//
//	public String borrowBook(int isbn, int customerID,int day, int month, int year) {
//		try {
//			con.setAutoCommit(false);
//			//lock customer
//			ResultSet customer = query(String.format("SELECT * FROM customer WHERE customerid = "
//					+ customerID
//					+ "FOR UPDATE", customerID));
//			if(!customer.next()) {
//				throw new RuntimeException("CustomerID is incorrect or Customer has been deleted.");
//			}
//			//lock book
//			ResultSet book = query(String.format("SELECT * FROM book WHERE isbn = "
//					+ isbn
//					+ "FOR UPDATE", isbn));
//			if(!book.next())
//				throw new RuntimeException("ISBN is incorrect or Book has been removed.");
//
//			int CopyNum = book.getInt("numleft");
//			if(CopyNum <= 0)
//				throw new RuntimeException("There are no copies left of this book");
//
//			PreparedStatement cust_book = con.prepareStatement("INSERT INTO cust_book VALUES(?, ?, ?)");
//
//			java.sql.Date date = java.sql.Date.valueOf(String.format("%d-%s-%s", year, month, day));
//
//			cust_book.setInt(1, isbn);
//			cust_book.setDate(2, date);
//			cust_book.setInt(3, customerID);
//
//			cust_book.executeUpdate();
//
//			int i = showConfirmDialog(dialogParent,
//					"Locked the tuple(s), ready to update. Click OK to continue",
//					"Information",
//					JOptionPane.YES_NO_OPTION);
//			if(i==0) {
//				update(String.format("UPDATE book SET numleft = numleft - 1 WHERE isbn = "+ isbn, isbn));
//
//
//				con.commit();
//
//				return String.format("Borrow Book:\n\tBook: %d (%s)\n\tLoaned to: %d (%s, %s)\n\tDue Date: %s",
//						isbn, book.getString("title"), customerID, customer.getString("l_name"),  customer.getString("f_name"),  date.toString());
//			}else return "Cancel the borrow.";
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			try {
//				con.rollback();
//			} catch (SQLException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			try {
//				throw e;
//			} catch (SQLException e1) {
//				// TODO Auto-generated catch block
//				return "Borrow book:\n\tDatabase error";
//			}
//		}
//		catch(RuntimeException e) {
//			try {
//				con.rollback();
//			} catch (SQLException e1) {
//				// TODO Auto-generated catch block
//				return "Borrow book:\n\t " + e.toString();
//			}
//			return "Borrow book:\n\t" + e.getMessage();
//		}finally {
//			try {
//				con.setAutoCommit(true);
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				return "Borrow book:\n\tDatabase error";
//			}
//		}
//	}
//
//	public String returnBook(int isbn, int customerid) {
//		try {
//			con.setAutoCommit(false);
//			//lock customer
//			ResultSet customer = query(String.format("SELECT * FROM customer WHERE customerid = %d" + "FOR UPDATE", customerid));
//			if(!customer.next()) {
//				throw new RuntimeException("CustomerID is incorrect.");
//			}
//			//lock book
//			ResultSet cust_book = query(String.format("SELECT * FROM book WHERE isbn = "
//					+ isbn
//					+ "FOR UPDATE", isbn));
//			if(!cust_book.next())
//				throw new RuntimeException("ISBN is incorrect.");
//			//	int CopyNum = cust_book.getInt("numleft");
//			PreparedStatement cust_book1 = con.prepareStatement("DELETE FROM cust_book WHERE customerid =" + customerid + "AND isbn =" + isbn);
//
//			cust_book1.executeUpdate();
//			update(String.format("UPDATE book SET numleft = numleft + 1 WHERE isbn = "+ isbn, isbn));
//
//			con.commit();
//
//			return String.format("Return Book:\n\tBook: %d (%s)\n\tLoaned to: %d (%s, %s)\n\t Now return successfully.",
//					isbn, cust_book.getString("title"),  customerid, customer.getString("l_name"),  customer.getString("f_name"));
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			try {
//				con.rollback();
//			} catch (SQLException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			try {
//				throw e;
//			} catch (SQLException e1) {
//				// TODO Auto-generated catch block
//				return "Return book:\n\tDatabase error";
//			}
//		}
//		catch(RuntimeException e) {
//			try {
//				con.rollback();
//			} catch (SQLException e1) {
//				// TODO Auto-generated catch block
//
//				return "Return book:\n\t"+e.toString();
//
//			}
//			return "Return book:\n\t" + e.getMessage();
//		}finally {
//			try {
//				con.setAutoCommit(true);
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				return "Return book:\n\tDatabase error";
//			}
//		}
//	}
//
//	public void closeDBConnection() {
//		try {
//			con.close();
//		} catch (SQLException e) {
//			showMessageDialog(dialogParent,
//					e.toString(),
//					"Error performing action",
//					ERROR_MESSAGE);
//		}
//	}
//
//	public String deleteCus(int customerID) {
//		try {
//			con.setAutoCommit(false);
//			//lock customer
//			ResultSet customer = query(String.format("SELECT * FROM customer WHERE customerid = %d FOR UPDATE", customerID));
//			if(!customer.next())
//				throw new RuntimeException("Customer does not exist");
//			//lock book
//			ResultSet cust_book = query(String.format("SELECT * FROM cust_book WHERE customerid = %d FOR UPDATE", customerID));
//			if(cust_book.next())
//				throw new RuntimeException("Customer still has loans - can not be removed.");
//
//			int cust_result = update(String.format("DELETE FROM customer WHERE customerid = %d", customerID));
//			if(cust_result == 0)
//				throw new RuntimeException("There was an error removing the customer.");
//
//			con.commit();
//
//			return "Customer "+customerID+" deleted.";
//		}
//		catch(RuntimeException e) {
//			try {
//				con.rollback();
//				return "Delete customer: " + e.getMessage();
//			} catch (SQLException e1) {
//				// TODO Auto-generated catch block
//				return e1.toString()+"\n"+"Delete customer:\n\tDatabase error.";
//			}
//		}
//		catch(SQLException e) {
//			try {
//				con.rollback();
//				throw e;
//			} catch (SQLException e1) {
//				// TODO Auto-generated catch block
//				return "Delete customer:\n\tDatabase error.";
//			}
//			finally {
//				try {
//					con.setAutoCommit(true);
//				} catch (SQLException e1) {
//					// TODO Auto-generated catch block
//					return "Delete customer:\n\tDatabase error. ";
//				}
//			}
//		}
//	}
//
//	public String deleteAuthor(int authorID) {
//		try {
//			con.setAutoCommit(false);
//			//lock customer
//			ResultSet customer = query(String.format("SELECT * FROM author WHERE authorid = "+authorID+" FOR UPDATE", authorID));
//			if(!customer.next())
//				throw new RuntimeException("ID incorrect");
//			//lock book
//			ResultSet book_author = query(String.format("SELECT * FROM book_author WHERE authorid = "+authorID+" FOR UPDATE", authorID));
//			while(book_author.next()) {
//				int sequencesNo = book_author.getInt("authorseqno");
//				int isbn = book_author.getInt("isbn");
//
//				update(String.format("UPDATE book_author SET authorseqno = authorseqno - 1 WHERE isbn = "+authorID+" AND authorseqno > "+ authorID, isbn,sequencesNo));
//
//				int result = update(String.format("DELETE FROM book_author WHERE authorid = "+ authorID +" AND isbn = " + isbn,isbn, authorID));
//				if(result == 0)
//					throw new RuntimeException("There was an error removing the author.");
//			}
//			con.commit();
//
//			int result2 = update(String.format("DELETE FROM author WHERE authorid = "+authorID, authorID));
//			if(result2 == 0)
//				throw new RuntimeException("There was an error removing the author!");
//
//			return "Author " + authorID +" deleted successfully.";
//		}
//		catch(RuntimeException e) {
//			try {
//				con.rollback();
//				return "Delete author: " + e.getMessage();
//			} catch (SQLException e1) {
//				// TODO Auto-generated catch block
//				return e1.toString()+"\n"+"Delete author:\n\tDatabase error.";
//			}
//		}
//		catch(SQLException e) {
//			try {
//				con.rollback();
//				throw e;
//			} catch (SQLException e1) {
//				// TODO Auto-generated catch block
//				return "Delete author:\n\tDatabase error."+e.toString();
//			}
//			finally {
//				try {
//					con.setAutoCommit(true);
//				} catch (SQLException e1) {
//					// TODO Auto-generated catch block
//					return "Delete author:\n\tDatabase error. ";
//				}
//			}
//		}
//	}
//
//	public String deleteBook(int isbn) {
//		try {
//			con.setAutoCommit(false);
//			//lock customer
//			ResultSet book = query(String.format("SELECT * FROM book WHERE isbn = "+ isbn +" FOR UPDATE", isbn));
//			if(!book.next())throw new RuntimeException("Book isbn is not correct");
//			//lock book
//			ResultSet cust_book = query(String.format("SELECT * FROM cust_book WHERE isbn = "+ isbn, isbn));
//			if(cust_book.next())throw new RuntimeException("Some customer still loaned this book, reject delete.");
//
//			update(String.format("DELETE FROM book_author WHERE isbn = %d", isbn));
//
//			int result = update(String.format("DELETE FROM book WHERE isbn = %d", isbn));
//			if(result == 0)throw new RuntimeException("Delete book error.");
//
//			con.commit();
//
//			return "Book " + isbn + " deleted successfully.";
//		}
//		catch(RuntimeException e) {
//			try {
//				con.rollback();
//				return "Delete book: " + e.getMessage();
//			} catch (SQLException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			return "Delete book: " + e.getMessage();
//		}
//		catch(SQLException e) {
//			try {
//				con.rollback();
//			} catch (SQLException e1) {
//				// TODO Auto-generated catch block
//				return "Delete book:\n\tDatabase error";
//			}
//			try {
//				throw e;
//			} catch (SQLException e1) {
//				// TODO Auto-generated catch block
//				return "Delete book:\n\tDatabase error";
//			}
//			finally {
//				try {
//					con.setAutoCommit(true);
//				} catch (SQLException e1) {
//					// TODO Auto-generated catch block
//					return "Delete book:\n\tDatabase error";
//				}
//			}
//		}
//	}
//}
/*
 * LibraryModel.java
 * Author:YINJIE WANG  300385286
 * Created on:12/10/2019
 */



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.*;

public class LibraryModel {
	Connection con;
	Statement stmt;
    ResultSet rs;
    private JFrame dialogParent;

    public LibraryModel(JFrame parent, String userid, String password)  {
	dialogParent = parent;
	try {

		Class.forName("org.postgresql.Driver");

	String url = "jdbc:postgresql://db.ecs.vuw.ac.nz/"+userid + "_jdbc";

		con = DriverManager.getConnection(url, userid, password);

	} catch (SQLException e) {
		e.printStackTrace();
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
	}


    }

    /**
     * Shows the book authors sorted according to AuthSeqNo
     * @param isbn
     * @return "Book Lookup:
     * 				ISBN: title,
     * 				Edition: 1, Number of copies: 10, copies left: 10
     * 				Authors: Sunames
     */
    public String bookLookup(int isbn) {
    	String edi = "";
    	String noc = "";
    	String cl = "";
    	String Author = "";
    	String title = "No such isbn!";
    	try {
			String select = "SELECT * FROM Book NATURAL JOIN Book_Author NATURAL JOIN AUTHOR "
							+ "WHERE isbn = "+ isbn
							+"ORDER BY AuthorSeqNo ASC;";

			//if(title)
	    	Statement stmt = con.createStatement();
	    	ResultSet result = stmt.executeQuery(select);
	    	while(result.next()){//then add book information into the result
	    		edi = result.getString("edition_no");
	    		noc =result.getString("numofcop");
	    		cl = "copies left: "+result.getString("numleft");
	    		Author += result.getString("surname")+ ',';
	    		title = result.getString("Title");
	    	}
	    	stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	return isbn +": "+title +"\n    Edition: "+ edi + " - Number of copies: "+noc+" - Copies Left: "+ cl+"\n    Authors: " + Author.replaceAll("\\s+","") ;
    }

    /***
     * This returns the book lookup for all books in the catalogue.
     * @return
     */
    public String showCatalogue() {
    	String output="";
    	try {
			String select = "SELECT isbn FROM book ORDER BY isbn ASC;";
			Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(select);
	    	while(rs.next()){
	    		int isbn = rs.getInt("isbn");
	    		output += "\n \n "+bookLookup(isbn);//finding book using isbn
	    	}
		} catch (SQLException e) {
			return "error getting books";
		}
	return output;
    }


    public String showLoanedBooks() {  //look up for books if copies != numbers left
    	String out = "Books Loaned: \n";
    	try {
    		int isbn=0;
    		boolean loaned = false;//set a boolean, when there's no books borrowed, print No books loaned
	    	String select = "SELECT * FROM Book WHERE numofcop > numLeft ORDER BY isbn ASC;";
	    	Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(select);
	    	while(rs.next()){
	    		loaned = true;//book successfully loaned to a person
	    		isbn = rs.getInt("isbn");
	    		out +=  bookLookup(isbn) +" \n";
	    	}
	    	stmt.close();

	    	if(loaned == false){
	    		return "\n   No books are loaned";
	    		}

		} catch (SQLException e) {
			return "Error getting books.";
		}
    	return out;
    }

    public String showAuthor(int authorID) {
    	String title="";
    	String out = "" ;
    	String bookWritten="";
    	int num=0;
    	//int ID;
    	try {
			String select = "SELECT * FROM Book NATURAL JOIN Book_Author NATURAL JOIN AUTHOR "
					+ "WHERE AuthorId = "+ authorID
					+"ORDER BY AuthorSeqNo ASC;";
	    	Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(select);
	    	while(rs.next()){
	    		num++;//record +1 books has been written
	    		out =authorID +":"+ rs.getString("name").replaceAll("\\s+","") + rs.getString("surname").replaceAll("\\s+","")
	    				+"\n   ";
	    		bookWritten +="\n     "+rs.getInt("isbn")+":"+ rs.getString("title");
	    	}

	    	if(num == 0){//no book has beed written
	    		title="No books has been written: ";
	    	}
	    	else {//>=1 books has been written
	    		title = "Books written: ";
	    	}
	    	stmt.close();

		} catch (SQLException e) {
			return "No such author ID";
		}

    	return "Show the Author: \n"+ out+title+ bookWritten+" \n";
    }



    public String showAllAuthors() {
    	String allAuthor = "Showing authors: \n";
    	try {
			String select = "SELECT * FROM author;";
			Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(select);
	    	while(rs.next()){
	    		allAuthor +=rs.getInt("AuthorId")+":"+ rs.getString("name").replaceAll("\\s+","")+","+rs.getString("surname")+" \n ";
	    	}

		} catch (SQLException e) {
			return"error showing all authors";
		}

    if(allAuthor==null) {return "there are no authors";}

	return allAuthor;
    }


    public String showCustomer(int customerID) {

    	String result = "Customer: \n" ;
    	String book="";
    	String borrowed="";
    	try{
    		int customer =0;
    		Statement stmt = con.createStatement();
    		String select = "SELECT * FROM  Customer WHERE customerId = "+ customerID
    				+";";
    		ResultSet rs = stmt.executeQuery(select);//finding customers by ID
    		while(rs.next()){
    			customer ++;
    		}
    		if(customer ==0){
    			return "No such Customer ID";
    		}
    		try {
    			select = "SELECT * FROM Customer "
    					+ "WHERE customerId = "+ customerID
    					+";";
    			rs = stmt.executeQuery(select);
    			while(rs.next()){
    				result +=customerID +"-"+ rs.getString("l_name").replaceAll("\\s+","") + rs.getString("f_name").replaceAll("\\s+","")+"- "+rs.getString("city");
    			}
    			select = "SELECT * FROM Cust_book NATURAL JOIN book "
    					+ "WHERE customerId = "+ customerID
    					+";";
    			rs = stmt.executeQuery(select);
    			while(rs.next()){
        			book +="\n        "+rs.getInt("isbn")+"-"+ rs.getString("title");
        			customer++;    			}

    			if(customer == 0){
    				borrowed="\n (No books borrowed) ";
    			}
    			else{
    				borrowed= "\n Books borrowed: "+customer;
    			}

    			stmt.close();

    		} catch (SQLException e) {
    			return "cannot find books";
    		}
    	} catch (SQLException e) {
    		return "No such Customer ID";
    	}

    	return result+borrowed + book+" \n";
    }

    public String showAllCustomers() {
    	String allCus = "Show all customers now: \n";
    	try {
			String select = "SELECT *FROM customer;";
			Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(select);

	    	while(rs.next()){
	    		allCus +=rs.getInt("customerid")+":"+ rs.getString("l_name").replaceAll("\\s+","")+","+rs.getString("f_name").replaceAll("\\s+","")+
	    				"-"+rs.getString("city")+" \n ";
	    	}


		} catch (SQLException e) {
			return "Error getting all customers";
		}
    	return allCus;
    }

    public String borrowBook(int isbn, int customerID, int day, int month, int year) {
	StringBuilder output = new StringBuilder("");
	String print = "can't borrow the book";
	Statement s = null;
	ResultSet rs = null;

	try {
		s = con.createStatement();
		rs  = s.executeQuery("SELECT numLeft FROM book  WHERE isbn = "+ isbn+"AND numLeft>0;");

		if(rs.next()!=false) {
			rs = s.executeQuery("SELECT customerid FROM cust_book WHERE isbn= "+ isbn+"AND customerid = "+customerID+";");
			if(rs.next() !=false)print = "Already borrowed this book";
			else {
				print = "borrowed succesfull";
				LocalDate date = LocalDate.of(year, month, day);
				s.executeUpdate("INSERT INTO cust_book VALUES('"+isbn+"','"+date+"','"+customerID+"');");
				s.executeUpdate("UPDATE book SET numleft = numleft-1 WHERE isbn ="+isbn+" ;");

		}
			}
		else print = "No books left!";

		rs = s.executeQuery("SELECT * FROM cust_book WHERE customerid = "+customerID+";");

	}
	catch (SQLException sqlex){
		System.out.println("error occured");
	}

	return print +" \n" +"Books has been borrowed" ;

    }

    public String returnBook(int isbn, int customerID) {
    	StringBuilder output = new StringBuilder("");
    	String print = "returned successful";
    	Statement s = null;
    	ResultSet rs = null;

    	try {
    		s = con.createStatement();

    		rs = s.executeQuery("SELECT * FROM customer WHERE customerid = "+customerID+";");
    		if(rs.next()!=false) {

    			if(rs.next() !=false)print = "no such isbn";

    			else {
    				rs  = s.executeQuery("SELECT * FROM book  WHERE isbn = "+ isbn+";");
    				print = "returned successful";
    				//LocalDate date = LocalDate.of(year, month, day);
    				s.executeUpdate("DELETE FROM cust_book WHERE customerid ="+customerID+";");
    				s.executeUpdate("UPDATE book SET numleft = numleft+1 WHERE isbn ="+isbn+" ;");

    		}
    			}

    		else print = "No books left!";

    		rs = s.executeQuery("SELECT * FROM cust_book WHERE customerid = "+customerID+";");
    		con.commit();
    	}
    	catch (SQLException sqlex){
    		System.out.println("error occured");
    	}

    	return print +" \n" +"Books has been returned" ;
    }

    public void closeDBConnection() {
    	try {
			con.close();
    	}
    	catch (SQLException e) {
    		System.out.println("the connection can't be closed");
    	}
    }

    public String deleteCus(int customerID) {
    	return "Delete Customer";
    }

    public String deleteAuthor(int authorID) {
    	return "Delete Author";
    }

    public String deleteBook(int isbn) {
    	return "Delete Book";
    }
}