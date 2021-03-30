package org.niolikon.springbooklibrary.publisher;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.niolikon.springbooklibrary.book.Book;
import org.springframework.data.domain.Persistable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "publisher")
@NoArgsConstructor
@Data
public class Publisher implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
    
    @OneToMany(mappedBy="publisher",cascade=CascadeType.REMOVE)
    @EqualsAndHashCode.Exclude
    Set<Book> books;

	@Override
	public boolean isNew() {
		if (this.id == null) {
			return true;
		}
		
		return false;
	}
}
