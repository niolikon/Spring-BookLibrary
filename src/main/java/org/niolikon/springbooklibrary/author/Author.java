package org.niolikon.springbooklibrary.author;

import java.util.Set;

import javax.persistence.*;

import org.niolikon.springbooklibrary.book.Book;
import org.springframework.data.domain.Persistable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "author")
@NoArgsConstructor
@Data
public class Author implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;
    
    @OneToMany(mappedBy="author",cascade=CascadeType.REMOVE)
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