package org.niolikon.springbooklibrary.book;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.niolikon.springbooklibrary.author.Author;
import org.niolikon.springbooklibrary.publisher.Publisher;
import org.springframework.data.domain.Persistable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "book")
@NoArgsConstructor
@Data
public class Book implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable=false, updatable=true)
    private Author author;

    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable=false, updatable=true)
    private Publisher publisher;
    
    @Column(name = "quantity")
    private int quantity;

	@Override
	public boolean isNew() {
		if (this.id == null) {
			return true;
		}
		
		return false;
	}
}
