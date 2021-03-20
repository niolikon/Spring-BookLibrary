package org.niolikon.springbooklibrary.publisher;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.niolikon.springbooklibrary.publisher.web.PublisherRequest;
import org.niolikon.springbooklibrary.publisher.web.PublisherView;
import org.niolikon.springbooklibrary.publisher.converter.PublisherToPublisherViewConverter;
import org.niolikon.springbooklibrary.system.MessageProvider;
import org.niolikon.springbooklibrary.system.exceptions.EntityDuplicationException;
import org.niolikon.springbooklibrary.system.exceptions.EntityNotFoundException;

@Service
public class PublisherService {
    
    private final PublisherRepository publisherRepo;
    private final PublisherToPublisherViewConverter publisherConverter;
    private final MessageProvider messageProvider;
    
    public PublisherService(PublisherRepository publisherRepo,
            PublisherToPublisherViewConverter publisherConverter,
            MessageProvider messageUtil) {
        this.publisherRepo = publisherRepo;
        this.publisherConverter = publisherConverter;
        this.messageProvider = messageUtil;
    }
    
    public Publisher findPublisherOrThrow(Long id) {
        return publisherRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(messageProvider.getMessage("publisher.NotFound", id)));
    }
    
    public PublisherView getPublisher(Long id) {
        Publisher publisher = findPublisherOrThrow(id);
        return publisherConverter.convert(publisher);
    }
    
    public Page<PublisherView> findAllPublishers(Pageable pageable) {
        Page<Publisher> publishers = publisherRepo.findAll(pageable);
        List<PublisherView> publisherViews = new ArrayList<>();
        publishers.forEach(publisher -> {
            PublisherView publisherView = publisherConverter.convert(publisher);
            publisherViews.add(publisherView);
        });
        return new PageImpl<>(publisherViews, pageable, publishers.getTotalElements());
    }
    
    public PublisherView create(PublisherRequest req) {
        Publisher publisher = new Publisher();
        publisher = this.fetchFromRequest(publisher, req);
        
        try {
            Publisher publisherSaved = publisherRepo.save(publisher);
            return publisherConverter.convert(publisherSaved);
        }  catch (DataIntegrityViolationException e) {
            throw new EntityDuplicationException(messageProvider.getMessage("publisher.Duplication", publisher.getName()));
        }
    }
    
    @Transactional
    public void delete(Long id) {
        try {
            publisherRepo.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(messageProvider.getMessage("publisher.NotFound", id));
        }
    }
    
    public PublisherView update(Publisher publisher, PublisherRequest req) {
        Publisher publisherUpdated = this.fetchFromRequest(publisher, req);
        Publisher publisherSaved = publisherRepo.save(publisherUpdated);
        return publisherConverter.convert(publisherSaved);
    }
    
    private Publisher fetchFromRequest(Publisher publisher, PublisherRequest req) {
        publisher.setName(req.getName());
        return publisher;
    }
}
