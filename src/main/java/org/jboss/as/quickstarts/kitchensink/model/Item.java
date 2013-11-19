package org.jboss.as.quickstarts.kitchensink.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotEmpty;
@SuppressWarnings("serial")
@Entity
@XmlRootElement
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "seat_id"))
public class Item implements Serializable{

	
	@Id
    @GeneratedValue
    private Long id;
	
	@NotNull
    @Size(min = 3, max = 5)
    @Pattern(regexp = "[^a-zA-Z]*", message = "Must not contain letters")
    private String flight_num;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 3)
   // @Pattern(regexp = "[0-9]{2}", message = "eg: 18D or 09B")
    private String seat_num;

    @NotNull
    @Size(min = 6, max = 8)
    @Column(name = "seat_id")
    private String seat_id;
    
    @NotNull
    private boolean state = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
	public String getFlight_num() {
		return flight_num;
	}

	public void setFlight_num(String flight_num) {
		this.flight_num = flight_num;
	}

	public String getSeat_num() {
		return seat_num;
	}

	public void setSeat_num(String seat_num) {
		this.seat_num = seat_num;
	}

	public String getSeat_id() {
		return seat_id;
	}

	public void setSeat_id(String seat_id) {
		this.seat_id = seat_id;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}
    
}
