package com.isabelly.tessiebooks.service;

import com.isabelly.tessiebooks.entity.Book;
import com.isabelly.tessiebooks.entity.ReadingStatus;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.entity.UserBookStatus;
import com.isabelly.tessiebooks.repository.BookRepository;
import com.isabelly.tessiebooks.repository.UserBookStatusRepository;
import com.isabelly.tessiebooks.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryService {

    private final UserBookStatusRepository userBookStatusRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public LibraryService(UserBookStatusRepository userBookStatusRepository, 
                          BookRepository bookRepository, 
                          UserRepository userRepository) {
        this.userBookStatusRepository = userBookStatusRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public UserBookStatus setStatus(User user, Long bookId, ReadingStatus status) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
        
        Optional<UserBookStatus> existing = userBookStatusRepository
                .findByUserIdAndBookId(user.getId(), bookId);
        
        UserBookStatus ub;
        
        if (existing.isPresent()) {
            ub = existing.get();
            ub.setStatus(status);
        } else {
            ub = new UserBookStatus();
            ub.setUser(user);
            ub.setBook(book);
            ub.setStatus(status);
            
            // Se o livro tem páginas definidas, definir paginasTotais
            if (book.getPages() != null && book.getPages() > 0) {
                ub.setPaginasTotais(book.getPages());
            }
        }
        
        return userBookStatusRepository.save(ub);
    }

    public List<UserBookStatus> getLibrary(User user) {
        return userBookStatusRepository.findByUserId(user.getId());
    }

    public List<UserBookStatus> getLibraryByStatus(User user, ReadingStatus status) {
        return userBookStatusRepository.findByUserIdAndStatus(user.getId(), status);
    }

    public UserBookStatus updateReadProgress(User user, Long bookId, Integer paginasLidas, Integer paginasTotais, Boolean favorito) {
    UserBookStatus ub = userBookStatusRepository
            .findByUserIdAndBookId(user.getId(), bookId)
            .orElseThrow(() -> new RuntimeException("Livro não encontrado na sua biblioteca"));
    
    if (paginasLidas != null) {
        ub.setPaginasLidas(paginasLidas);
    }
    if (paginasTotais != null && paginasTotais > 0) {
        ub.setPaginasTotais(paginasTotais);
    }
    
    // Se paginasTotais ainda for 0 ou null, tentar pegar do livro
    if ((ub.getPaginasTotais() == null || ub.getPaginasTotais() == 0) && ub.getBook() != null && ub.getBook().getPages() != null) {
        ub.setPaginasTotais(ub.getBook().getPages());
    }
    
    if (favorito != null) {
        ub.setFavorito(favorito);
    }
    
    // Se o status é LIDO e não há data de término, definir
    if (ub.getStatus() == ReadingStatus.LIDO && ub.getFinishDate() == null) {
        ub.setFinishDate(java.time.LocalDate.now());
    }
    
    // Se o status é LENDO e não há data de início, definir
    if (ub.getStatus() == ReadingStatus.LENDO && ub.getStartDate() == null) {
        ub.setStartDate(java.time.LocalDate.now());
    }
    
    return userBookStatusRepository.save(ub);
}
}
