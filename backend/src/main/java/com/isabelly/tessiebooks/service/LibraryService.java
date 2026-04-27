package com.isabelly.tessiebooks.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.isabelly.tessiebooks.entity.Book;
import com.isabelly.tessiebooks.entity.ReadingStatus;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.entity.UserBookStatus;
import com.isabelly.tessiebooks.repository.BookRepository;
import com.isabelly.tessiebooks.repository.UserBookStatusRepository;
import com.isabelly.tessiebooks.repository.UserRepository;

@Service
public class LibraryService {

    private final UserBookStatusRepository userBookStatusRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;  // ← precisa ter!

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

        UserBookStatus ub = userBookStatusRepository.findByUserIdAndBookId(user.getId(), bookId)
                .orElse(null);
        
        if (ub == null) {
            ub = new UserBookStatus();
            ub.setUser(user);
            ub.setBook(book);
        }

        ReadingStatus oldStatus = ub.getStatus();
        ub.setStatus(status);
        
        // Garantir que não seja null
        if (user.getTotalBooksRead() == null) user.setTotalBooksRead(0);
        if (user.getTotalPagesRead() == null) user.setTotalPagesRead(0);
        
        if (status == ReadingStatus.LIDO && oldStatus != ReadingStatus.LIDO) {
            user.setTotalBooksRead(user.getTotalBooksRead() + 1);
            if (ub.getPaginasTotais() != null && ub.getPaginasTotais() > 0) {
                user.setTotalPagesRead(user.getTotalPagesRead() + ub.getPaginasTotais());
            }
            userRepository.save(user);
        } 
        else if (oldStatus == ReadingStatus.LIDO && status != ReadingStatus.LIDO) {
            user.setTotalBooksRead(Math.max(0, user.getTotalBooksRead() - 1));
            if (ub.getPaginasTotais() != null && ub.getPaginasTotais() > 0) {
                user.setTotalPagesRead(Math.max(0, user.getTotalPagesRead() - ub.getPaginasTotais()));
            }
            userRepository.save(user);
        }
        
        return userBookStatusRepository.save(ub);
    }

    public List<UserBookStatus> getLibrary(User user) {
        return userBookStatusRepository.findByUserId(user.getId());
    }

    public List<UserBookStatus> getLibraryByStatus(User user, ReadingStatus status) {
        return userBookStatusRepository.findByUserId(user.getId()).stream()
                .filter(ub -> ub.getStatus() == status)
                .toList();
    }

    public UserBookStatus updateReadProgress(User user, Long bookId, Integer paginasLidas, Integer paginasTotais, Boolean favorito) {
        UserBookStatus status = userBookStatusRepository.findByUserIdAndBookId(user.getId(), bookId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado na sua biblioteca"));

        status.setPaginasLidas(paginasLidas);
        status.setPaginasTotais(paginasTotais);
        if (favorito != null) status.setFavorito(favorito);

        return userBookStatusRepository.save(status);
    }
}