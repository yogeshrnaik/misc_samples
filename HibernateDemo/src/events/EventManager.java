/**********************************************************************************************************************/
package events;

/**********************************************************************************************************************/
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import util.HibernateUtil;

/**********************************************************************************************************************/
public class EventManager {

	public static void main(String[] args) 
	{
		EventManager mgr = new EventManager();
		//mgr.createAndStoreEvent("My Event", new Date());
		//mgr.listEvents(mgr.getEvents());
		//mgr.createAndStorePerson(27, "John", "Mclane");
		//mgr.addPersonToEvent(new Long(7), new Long(1));
		mgr.addEmailToPerson(new Long(7), "yogesh.naik@iflexsolutions.com");
		HibernateUtil.getSessionFactory().close();
	}
/**********************************************************************************************************************/
	private void createAndStoreEvent(String title, Date theDate) 
	{
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Event theEvent = new Event();
		theEvent.setTitle(title);
		theEvent.setDate(theDate);

		session.save(theEvent);
		System.out.println("The Event ID of newly added Event is: " + theEvent.getId());
		session.getTransaction().commit();
	}
/**********************************************************************************************************************/
	private void createAndStorePerson(int age, String firstname, String lastname) 
	{
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Person thePerson = new Person();
		thePerson.setAge(age);
		thePerson.setFirstname(firstname);
		thePerson.setLastname(lastname);

		session.save(thePerson);
		System.out.println("The Person ID of newly added Person is: " + thePerson.getId());
		session.getTransaction().commit();
	}
/**********************************************************************************************************************/
	private List getEvents() 
	{
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		List result = session.createQuery("from Event").list();
		session.getTransaction().commit();
		return result;
	}
/**********************************************************************************************************************/
	private void listEvents(List listEvents) 
	{
		for (int i = 0; i < listEvents.size(); i++) 
		{
			Event theEvent = (Event) listEvents.get(i);
			System.out.println("Event: " + theEvent.getTitle() + " Time: " + theEvent.getDate());
		}
	}
/**********************************************************************************************************************/
	private void addPersonToEvent(Long personId, Long eventId) 
	{
	    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	    session.beginTransaction();

	    Person aPerson = (Person) session.load(Person.class, personId);
	    Event anEvent = (Event) session.load(Event.class, eventId);

	    aPerson.getEvents().add(anEvent);
	    session.getTransaction().commit();
	}
/**********************************************************************************************************************/
	private void addEmailToPerson(Long personId, String emailAddress) 
	{

	    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	    session.beginTransaction();

	    Person aPerson = (Person) session.load(Person.class, personId);

	    // The getEmailAddresses() might trigger a lazy load of the collection
	    aPerson.getEmailAddresses().add(emailAddress);

	    session.getTransaction().commit();
	}
/**********************************************************************************************************************/
/**********************************************************************************************************************/
/**********************************************************************************************************************/
/**********************************************************************************************************************/
}
/**********************************************************************************************************************/
