/**********************************************************************************************************************/
package events;

import java.util.HashSet;
import java.util.Set;

/**********************************************************************************************************************/
public class Person 
{
	private Long id;
    private int age;
    private String firstname;
    private String lastname;
    private Set events = new HashSet();
    private Set emailAddresses = new HashSet();
    
    public Person() {}

	public int getAge() {
		return age;
	}

	public String getFirstname() {
		return firstname;
	}

	public Long getId() {
		return id;
	}

	public String getLastname() {
		return lastname;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
    
	public Set getEvents() {
        return events;
    }

    public void setEvents(Set events) {
        this.events = events;
    }
    
    public Set getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(Set emailAddresses) {
        this.emailAddresses = emailAddresses;
    }
    
    public void addToEvent(Event event) {
        this.getEvents().add(event);
        event.getParticipants().add(this);
    }

    public void removeFromEvent(Event event) {
        this.getEvents().remove(event);
        event.getParticipants().remove(this);
    }
}

/**********************************************************************************************************************/
