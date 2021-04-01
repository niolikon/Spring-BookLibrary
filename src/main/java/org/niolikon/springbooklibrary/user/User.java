package org.niolikon.springbooklibrary.user;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.data.domain.Persistable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "enduser")
@NoArgsConstructor
@Data
public class User implements Persistable<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "enduserrole", 
            joinColumns = @JoinColumn(name = "enduser_id"), 
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @Override
    public boolean isNew() {
        if (this.id == null) {
            return true;
        }
        
        return false;
    }
}
