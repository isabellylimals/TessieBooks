package com.isabelly.tessiebooks.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.isabelly.tessiebooks.entity.Book;
import com.isabelly.tessiebooks.entity.ReadingStatus;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.entity.UserBookStatus;
import com.isabelly.tessiebooks.repository.BookRepository;
import com.isabelly.tessiebooks.repository.UserBookStatusRepository;

@Service
public class LibraryService {

    private final UserBookStatusRepository userBookStatusRepository;
    private final BookRepository bookRepository;

    public LibraryService(UserBookStatusRepository userBookStatusRepository, BookRepository bookRepository) {
        this.userBookStatusRepository = userBookStatusRepository;
        this.bookRepository = bookRepository;
    }

    // Define ou atualiza o status de um livro na biblioteca do usuário
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

    ub.setStatus(status);

    return userBookStatusRepository.save(ub);
}

    public List<UserBookStatus> getLibrary(User user) {
        return userBookStatusRepository.findByUserId(user.getId());
    }

    public List<UserBookStatus> getLibraryByStatus(User user, ReadingStatus status) {
        List<UserBookStatus> all = userBookStatusRepository.findByUserId(user.getId());
        return all.stream()
                .filter(ub -> ub.getStatus() == status)
                .toList();
    }

    public UserBookStatus updateReadProgress(User user, Long bookId, Integer paginasLidas, Integer paginasTotais, Boolean favorito) {
    // 1. Busca o status do livro para aquele usuário
    UserBookStatus ub = userBookStatusRepository.findByUserIdAndBookId(user.getId(), bookId)
    .orElse(null); // ou .orElseGet(() -> new UserBookStatus())
    // 2. Atualiza os novos campos
ub.setPaginasLidas(paginasLidas);
 ub.setPaginasTotais(paginasTotais);
 ub.setFavorito(favorito);

    // 3. Salva no banco (o RDS da AWS receberá isso!)
    return userBookStatusRepository.save(ub);
}
}
