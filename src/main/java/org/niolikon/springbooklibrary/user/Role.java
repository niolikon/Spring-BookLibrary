package org.niolikon.springbooklibrary.user;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.data.domain.Persistable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role")
@NoArgsConstructor
@Data
public class Role implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
    
    @ManyToMany(mappedBy = "roles")
    @EqualsAndHashCode.Exclude
    private Set<User> users;

    @Override
    public boolean isNew() {
        if (this.id == null) {
            return true;
        }
        
        return false;
    }
}
