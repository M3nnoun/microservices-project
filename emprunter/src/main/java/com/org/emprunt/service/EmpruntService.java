package com.org.emprunt.service;

import com.org.emprunt.DTO.BookDTO;
import com.org.emprunt.DTO.EmpruntDetailsDTO;
import com.org.emprunt.DTO.UserDTO;
import com.org.emprunt.entities.Emprunter;
import com.org.emprunt.event.EmpruntEvent;
import com.org.emprunt.feign.BookClient;
import com.org.emprunt.feign.UserClient;
import com.org.emprunt.repositories.EmpruntRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpruntService {

    private final EmpruntRepository empruntRepository;
    private final UserClient userClient;
    private final BookClient bookClient;
    private final EmpruntProducer empruntProducer;

    public EmpruntService(EmpruntRepository empruntRepository,
                          UserClient userClient,
                          BookClient bookClient,
                          EmpruntProducer empruntProducer) {
        this.empruntRepository = empruntRepository;
        this.userClient = userClient;
        this.bookClient = bookClient;
        this.empruntProducer = empruntProducer;
    }

    public Emprunter createEmprunt(Long userId, Long bookId) {
        Emprunter emprunter = new Emprunter();
        emprunter.setUserId(userId);
        emprunter.setBookId(bookId);
        
        Emprunter saved = empruntRepository.save(emprunter);
        
        // Publier evenement Kafka
        EmpruntEvent event = new EmpruntEvent(saved.getId(), userId, bookId);
        empruntProducer.sendEmpruntEvent(event);
        
        return saved;
    }

    public List<Emprunter> getAllEmprunts() {
        return empruntRepository.findAll();
    }

    public Emprunter getEmpruntById(Long id) {
        return empruntRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emprunt non trouve avec id: " + id));
    }

    public EmpruntDetailsDTO getEmpruntDetails(Long id) {
        Emprunter emprunt = getEmpruntById(id);
        UserDTO user = userClient.getUser(emprunt.getUserId());
        BookDTO book = bookClient.getBook(emprunt.getBookId());
        
        return new EmpruntDetailsDTO(
                emprunt.getId(),
                user.getName(),
                book.getTitle(),
                emprunt.getEmpruntDate()
        );
    }
}