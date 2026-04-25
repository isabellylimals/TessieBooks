// app.js

let authToken = null;
let loginSection, registerSection, appSection, loginBtn, registerBtn, loginError, registerError, logoutBtn;
let toggleRegister, toggleLogin, navButtons, booksSection, feedSection, reviewsSection, profileSection, exploreSection;
let booksList, feedList, reviewsList, profileContent, reviewForm, reviewBookIdInput, reviewTitleInput;
let reviewTextInput, reviewMessage, postForm, postContent, postMessage, reviewStars, reviewRating, reviewImage;
let myBooksList, allBooksList, tabBtns, usersList, searchUsersInput, searchUsersBtn;
let currentViewingUserId = null;
let myFollowing = [];

// ---------- Inicialização ----------
// Delegação para botões de status de livros
document.body.addEventListener("click", async (e) => {
  const btn = e.target.closest("[data-book][data-status]");
  if (!btn) return;

  const bookId = btn.dataset.book;
  const status = btn.dataset.status;

  try {
    await apiSetBookStatus(authToken, bookId, status);
    carregarMeusLivros();
  } catch (err) {
    alert("Erro ao atualizar status: " + err.message);
  }
});


// Inicializa referências aos elementos
function initializeElements() {
  loginSection = document.getElementById("login-section");
  registerSection = document.getElementById("register-section");
  appSection = document.getElementById("app-section");
  loginBtn = document.getElementById("loginBtn");
  registerBtn = document.getElementById("registerBtn");
  loginError = document.getElementById("loginError");
  registerError = document.getElementById("registerError");
  logoutBtn = document.getElementById("logoutBtn");
  toggleRegister = document.getElementById("toggleRegister");
  toggleLogin = document.getElementById("toggleLogin");

  navButtons = document.querySelectorAll(".nav-btn");
  booksSection = document.getElementById("books-section");
  exploreSection = document.getElementById("explore-section");
  feedSection = document.getElementById("feed-section");
  reviewsSection = document.getElementById("reviews-section");
  profileSection = document.getElementById("profile-section");

  booksList = document.getElementById("books-list");
  usersList = document.getElementById("users-list");
  feedList = document.getElementById("feed-list");
  reviewsList = document.getElementById("reviews-list");
  profileContent = document.getElementById("profile-content");

  myBooksList = document.getElementById("my-books-list");
  allBooksList = document.getElementById("all-books-list");
  tabBtns = document.querySelectorAll(".tab-btn");

  searchUsersInput = document.getElementById("search-users-input");
  searchUsersBtn = document.getElementById("search-users-btn");

  reviewForm = document.getElementById("review-form");
  reviewBookIdInput = document.getElementById("review-book-id");
  reviewTitleInput = document.getElementById("review-title");
  reviewTextInput = document.getElementById("review-text");
  reviewMessage = document.getElementById("reviewMessage");
  reviewStars = document.querySelectorAll(".stars-container .star");
  reviewRating = document.getElementById("review-rating");
  reviewImage = document.getElementById("review-image");

  postForm = document.getElementById("post-form");
  postContent = document.getElementById("post-content");
  postMessage = document.getElementById("postMessage");

  // Inicializar estrelas com 5 ativas por padrão
  if (reviewStars && reviewStars.length > 0) {
    reviewStars.forEach((star) => {
      const value = parseInt(star.getAttribute("data-value") || 0);
      if (value <= 5) {
        star.classList.add("active");
      } else {
        star.classList.remove("active");
      }
    });
    if (reviewRating) {
      reviewRating.value = 5;
    }
  }
}

// Anexa listeners aos elementos
function attachEventListeners() {
  // Login
  if (loginBtn) {
    loginBtn.addEventListener("click", handleLogin);
  }

  // Registro
  if (registerBtn) {
    registerBtn.addEventListener("click", handleRegister);
  }

  // Toggle
  if (toggleRegister) {
    toggleRegister.addEventListener("click", (e) => {
      e.preventDefault();
      toggleLoginView();
    });
  }

  if (toggleLogin) {
    toggleLogin.addEventListener("click", (e) => {
      e.preventDefault();
      toggleLoginView();
    });
  }

  // Logout
  if (logoutBtn) {
    logoutBtn.addEventListener("click", () => {
      authToken = null;
      localStorage.removeItem("token");
      document.body.classList.remove("app-active");
      appSection.classList.add("hidden");
      loginSection.classList.remove("hidden");
    });
  }

  // Navegação
  if (navButtons && navButtons.length > 0) {
    navButtons.forEach((btn) => {
      const target = btn.getAttribute("data-target");
      if (!target) return;

      btn.addEventListener("click", () => {
        navButtons.forEach((b) => b.classList.remove("nav-active"));
        btn.classList.add("nav-active");

        [booksSection, exploreSection, feedSection, reviewsSection, profileSection].forEach((sec) => {
          if (sec) sec.classList.add("hidden");
        });
        const targetElement = document.getElementById(target);
        if (targetElement) targetElement.classList.remove("hidden");

        if (target === "books-section") carregarMeusLivros();
        if (target === "explore-section") carregarComunidade();
        if (target === "feed-section") carregarFeed();
        if (target === "reviews-section") carregarResenhas();
        if (target === "profile-section") carregarPerfil();
      });
    });
  }

  // Forms
  if (postForm) {
    postForm.addEventListener("submit", handlePostSubmit);
  }

  if (reviewForm) {
    reviewForm.addEventListener("submit", handleReviewSubmit);
  }

  // Estrelas de classificação
  if (reviewStars && reviewStars.length > 0) {
    reviewStars.forEach((star) => {
      star.addEventListener("click", () => {
        const value = star.getAttribute("data-value");
        if (reviewRating) {
          reviewRating.value = value;
        }
        reviewStars.forEach((s) => {
          if (s.getAttribute("data-value") <= value) {
            s.classList.add("active");
          } else {
            s.classList.remove("active");
          }
        });
      });
    });
  }

  // Abas de livros
  if (tabBtns && tabBtns.length > 0) {
    tabBtns.forEach((btn) => {
      btn.addEventListener("click", () => {
        const tabName = btn.getAttribute("data-tab");
        tabBtns.forEach((b) => b.classList.remove("active"));
        btn.classList.add("active");

        const tabs = document.querySelectorAll(".tab-content");
        tabs.forEach((tab) => tab.classList.add("hidden"));
        const activeTab = document.getElementById(tabName);
        if (activeTab) activeTab.classList.remove("hidden");

        if (tabName === "my-books") carregarMeusLivros();
        if (tabName === "all-books") carregarTodosOsLivros();
      });
    });
  }

  // Busca de usuários
  if (searchUsersBtn) {
    searchUsersBtn.addEventListener("click", buscarUsuarios);
  }
  if (searchUsersInput) {
    searchUsersInput.addEventListener("keypress", (e) => {
      if (e.key === "Enter") buscarUsuarios();
    });
  }
}

// ---------- Handlers ----------
async function handleLogin() {
  const email = document.getElementById("email").value.trim();
  const password = document.getElementById("password").value.trim();

  loginError.textContent = "";

  if (!email || !password) {
    loginError.textContent = "Preencha email e senha.";
    return;
  }

  try {
    const result = await apiLogin(email, password);
    if (result.token) {
      authToken = result.token;
      localStorage.setItem("token", authToken);
      document.body.classList.add("app-active");
      mostrarApp();
      carregarTudo();
    } else {
      loginError.textContent = "Erro ao autenticar. Verifique suas credenciais.";
    }
  } catch (err) {
    loginError.style.color = "#e49d9d";
    loginError.textContent = err.message || "Não foi possível fazer login.";
    console.error(err);
  }
}

async function handleRegister() {
  const name = document.getElementById("register-name").value.trim();
  const email = document.getElementById("register-email").value.trim();
  const password = document.getElementById("register-password").value.trim();
  const confirmPassword = document.getElementById("register-password-confirm").value.trim();

  registerError.textContent = "";

  if (!name || !email || !password || !confirmPassword) {
    registerError.textContent = "Preencha todos os campos.";
    return;
  }

  if (password !== confirmPassword) {
    registerError.textContent = "As senhas não coincidem.";
    return;
  }

  if (password.length < 6) {
    registerError.textContent = "A senha deve ter pelo menos 6 caracteres.";
    return;
  }

  console.log("=== REGISTRO INICIADO ===");
  console.log("Nome:", name);
  console.log("Email:", email);
  console.log("Password length:", password.length);

  try {
    console.log("Chamando apiRegister...");
    const result = await apiRegister(name, email, password);
    console.log("Resposta do registro:", result);
    
    registerError.textContent = "Conta criada com sucesso! Faça login para continuar.";
    registerError.style.color = "#2ecc71";

    setTimeout(() => {
      document.getElementById("register-name").value = "";
      document.getElementById("register-email").value = "";
      document.getElementById("register-password").value = "";
      document.getElementById("register-password-confirm").value = "";
      toggleLoginView();
    }, 2000);
  } catch (err) {
    console.error("Erro no registro:", err);
    console.error("Stack:", err.stack);
    registerError.style.color = "#e49d9d";
    registerError.textContent = err.message || "Erro ao criar conta. Tente novamente.";
  }
}

async function handlePostSubmit(e) {
  e.preventDefault();
  if (!authToken) return;

  postMessage.textContent = "";
  const content = postContent.value.trim();

  if (!content) {
    postMessage.textContent = "Escreva algo para compartilhar.";
    return;
  }

  try {
    await apiCreatePost(authToken, content);
    postMessage.textContent = "Post publicado com sucesso!";
    postForm.reset();
    carregarFeed();
  } catch (err) {
    console.error(err);
    postMessage.textContent = "Erro ao publicar post. Tente novamente.";
  }
}

async function handleReviewSubmit(e) {
  e.preventDefault();
  if (!authToken) return;

  if (!reviewMessage) return;
  reviewMessage.textContent = "";

  const bookId = reviewBookIdInput ? reviewBookIdInput.value.trim() : "";
  const title = reviewTitleInput ? reviewTitleInput.value.trim() : "";
  const text = reviewTextInput ? reviewTextInput.value.trim() : "";
  const rating = reviewRating ? parseInt(reviewRating.value) || 5 : 5;
  const imageFile = reviewImage ? reviewImage.files[0] : null;

  if (!bookId || !title || !text) {
    reviewMessage.textContent = "Preencha todos os campos da resenha.";
    return;
  }

  try {
    let imageUrl = null;
    if (imageFile) {
      // Converter imagem para base64 para envio
      const reader = new FileReader();
      imageUrl = await new Promise((resolve, reject) => {
        reader.onload = () => resolve(reader.result);
        reader.onerror = reject;
        reader.readAsDataURL(imageFile);
      });
    }

    await apiCreateReview(authToken, {
      bookId: Number(bookId),
      title,
      text,
      rating,
      imageUrl
    });
    reviewMessage.textContent = "Resenha publicada com sucesso!";
    if (reviewForm) {
      reviewForm.reset();
    }
    if (reviewRating) {
      reviewRating.value = 5;
    }
    if (reviewStars && reviewStars.length > 0) {
      reviewStars.forEach((s) => {
        if (parseInt(s.getAttribute("data-value") || 0) <= 5) {
          s.classList.add("active");
        } else {
          s.classList.remove("active");
        }
      });
    }
    carregarResenhas();
  } catch (err) {
    console.error(err);
    reviewMessage.textContent = "Erro ao publicar resenha. Verifique os dados e tente novamente.";
  }
}

// ---------- Funções auxiliares ----------
function toggleLoginView() {
  if (loginSection.classList.contains("hidden")) {
    loginSection.classList.remove("hidden");
    registerSection.classList.add("hidden");
  } else {
    loginSection.classList.add("hidden");
    registerSection.classList.remove("hidden");
  }
  loginError.textContent = "";
  registerError.textContent = "";
}

function mostrarApp() {
  appSection.classList.remove("hidden");
  loginSection.classList.add("hidden");
  registerSection.classList.add("hidden");
}

async function carregarTudo() {
  carregarMeusLivros();
  carregarFeed();
  carregarResenhas();
  carregarPerfil();
}

// ---------- Livros ----------
async function carregarMeusLivros() {
  if (!authToken) return;
  if (!myBooksList) return;
  myBooksList.innerHTML = "<p>Carregando seus livros...</p>";

  try {
    const biblioteca = await apiGetMyLibrary(authToken);

    if (!Array.isArray(biblioteca) || biblioteca.length === 0) {
      myBooksList.innerHTML =
        "<p class='info'>Você ainda não adicionou livros à sua biblioteca.</p>";
      return;
    }

    myBooksList.innerHTML = "";
    biblioteca.forEach((item) => {
      const livro = item.book || item;
      const card = document.createElement("div");
      card.className = "item-card";

      const titulo = livro.title || livro.nome || "Título desconhecido";
      const autor = livro.author || livro.autor || "Autor desconhecido";
      const status = item.status || item.readingStatus || "—";
      
      // Pegamos os valores de progresso (caso existam no banco, senão iniciam em 0)
      const pagAtual = item.paginasLidas || 0;  
const pagTotal = item.paginasTotais || 0; 
      const porcentagem = pagTotal > 0 ? Math.round((pagAtual / pagTotal) * 100) : 0;

      // Montamos o HTML do Card
      let cardHTML = `
        <div class="item-header">
          <h3 class="item-title">${titulo}</h3>
          <span class="item-meta">de ${autor}</span>
        </div>
        <div class="item-body">
          Status: <strong>${status}</strong>
        </div>`;

      // SE o status for LENDO, adicionamos o Diário de Leitura (Barra de Tinta)
      if (status === "LENDO") {
        cardHTML += `
          <div class="reading-progress-container">
              <div class="progress-info">
                  <span>Progresso da Leitura</span>
                  <span class="percentage">${porcentagem}%</span>
              </div>
              <div class="ink-progress-bar">
                  <div class="ink-fill" style="width: ${porcentagem}%;"></div>
              </div>
              <div class="progress-inputs">
                  <input type="number" class="p-input curr-page" value="${pagAtual}" placeholder="Pág.">
                  <span>/</span>
                  <input type="number" class="p-input total-pages" value="${pagTotal}" placeholder="Total">
                  <button class="save-progress-btn" title="Salvar Progresso" 
                    onclick="salvarProgressoLivro(${livro.id}, this)">✒️</button>
              </div>
          </div>
        `;
      }

      // Adicionamos os botões de ação que você já tinha
      cardHTML += `
        <div class="item-actions">
          <button class="button small-btn" data-book="${livro.id}" data-status="QUERO_LER">Quero ler</button>
          <button class="button small-btn" data-book="${livro.id}" data-status="LENDO">Lendo</button>
          <button class="button small-btn" data-book="${livro.id}" data-status="LIDO">Lido</button>
        </div>
      `;

      card.innerHTML = cardHTML;
      myBooksList.appendChild(card);
    });
  } catch (err) {
    console.error(err);
    myBooksList.innerHTML =
      "<p class='error'>Erro ao carregar sua biblioteca. Tente novamente.</p>";
  }
}

// NOVA FUNÇÃO: Captura os dados dos inputs e salva o progresso
async function salvarProgressoLivro(bookId, btn) {
  const container = btn.closest('.reading-progress-container');
  const pagAtual = container.querySelector('.curr-page').value;
const pagTotal = container.querySelector('.total-pages').value;

await apiUpdateBookProgress(authToken, bookId, pagAtual, pagTotal);

  try {
    // Chamada para a API (vamos criar essa função no api.js no próximo passo)
    await apiUpdateBookProgress(authToken, bookId, pagAtual, pagTotal);
    alert("Progresso registrado no diário! ✒️");
    carregarMeusLivros(); // Recarrega para atualizar a barra
  } catch (err) {
    alert("Erro ao salvar progresso: " + err.message);
  }
}

async function carregarTodosOsLivros() {
  if (!authToken) return;
  if (!allBooksList) return;
  allBooksList.innerHTML = "<p>Carregando livros...</p>";

  try {
    const livros = await apiGetBooks(authToken);

    if (!Array.isArray(livros) || livros.length === 0) {
      allBooksList.innerHTML =
        "<p class='info'>Nenhum livro encontrado.</p>";
      return;
    }

    allBooksList.innerHTML = "";
    livros.forEach((livro) => {
      const card = document.createElement("div");
      card.className = "item-card";

      const titulo = livro.title || livro.nome || "Título desconhecido";
      const autor = livro.author || livro.autor || "Autor desconhecido";
      const desc =
        livro.description || livro.descricao || "Sem descrição disponível.";

      card.innerHTML = `
        <div class="item-header">
          <h3 class="item-title">${titulo}</h3>
          <span class="item-meta">de ${autor}</span>
        </div>
        <div class="item-body">
          ${desc}
        </div>
          <div class="item-actions">
        <button class="button small-btn" data-book="${livro.id}" data-status="QUERO_LER">Quero ler</button>
        <button class="button small-btn" data-book="${livro.id}" data-status="LENDO">Lendo</button>
        <button class="button small-btn" data-book="${livro.id}" data-status="LIDO">Lido</button>
      </div>

      `;
      allBooksList.appendChild(card);
    });
  } catch (err) {
    console.error(err);
    allBooksList.innerHTML =
      "<p class='error'>Erro ao carregar livros. Tente novamente.</p>";
  }
}

// ---------- Comunidade ----------
async function carregarComunidade() {
  if (!authToken) return;
  if (!usersList) return;
  usersList.innerHTML = "<p>Carregando comunidade...</p>";

  try {
    const usuarios = await apiGetAllUsers(authToken);

    if (!Array.isArray(usuarios) || usuarios.length === 0) {
      usersList.innerHTML = "<p class='info'>Nenhum outro leitor encontrado.</p>";
      return;
    }

    usersList.innerHTML = "";
    usuarios.forEach((user) => {
      const card = document.createElement("div");
      card.className = "user-card";

      const nome = user.name || "Leitor anônimo";
      const bio = user.bio || "Um apreciador de histórias.";
      const reviewCount = user.reviewsCount || 0;
      const userId = user.id;
      const following = Array.isArray(myFollowing) ? myFollowing.map(f=>f.id || f) : [];
      const isFollowing = following.includes(userId);

      card.innerHTML = `
        <div class="user-header">
          <h3 class="user-name">${nome}</h3>
          <div>
            <button class="button secondary-btn" onclick="verPerfil(${userId})">Ver Perfil</button>
            ${userId ? `<button class="button" id="follow-btn-${userId}" onclick="${isFollowing ? `unfollowUser(${userId})` : `followUser(${userId})`}">${isFollowing ? 'Seguindo' : 'Seguir'}</button>` : ''}
          </div>
        </div>
        <div class="user-bio">${bio}</div>
        <div class="user-stats">
          <span>📚 ${reviewCount} resenhas</span>
        </div>
      `;
      usersList.appendChild(card);
    });
  } catch (err) {
    console.error(err);
    usersList.innerHTML = "<p class='error'>Erro ao carregar comunidade.</p>";
  }
}

async function buscarUsuarios() {
  if (!authToken) return;
  if (!usersList) return;

  const query = searchUsersInput ? searchUsersInput.value.trim() : "";

  if (!query) {
    carregarComunidade();
    return;
  }

  usersList.innerHTML = "<p>Buscando...</p>";

  try {
    const usuarios = await apiSearchUsers(authToken, query);

    if (!Array.isArray(usuarios) || usuarios.length === 0) {
      usersList.innerHTML = `<p class='info'>Nenhum leitor encontrado com "${query}".</p>`;
      return;
    }

    usersList.innerHTML = "";
    usuarios.forEach((user) => {
      const card = document.createElement("div");
      card.className = "user-card";

      const nome = user.name || "Leitor anônimo";
      const bio = user.bio || "Um apreciador de histórias.";
      const reviewCount = user.reviewsCount || 0;
      const userId = user.id;

      const following = Array.isArray(myFollowing) ? myFollowing.map(f=>f.id || f) : [];
      const isFollowing = following.includes(userId);

      card.innerHTML = `
        <div class="user-header">
          <h3 class="user-name">${nome}</h3>
          <div>
            <button class="button secondary-btn" onclick="verPerfil(${userId})">Ver Perfil</button>
            ${userId ? `<button class="button" id="follow-btn-${userId}" onclick="${isFollowing ? `unfollowUser(${userId})` : `followUser(${userId})`}">${isFollowing ? 'Seguindo' : 'Seguir'}</button>` : ''}
          </div>
        </div>
        <div class="user-bio">${bio}</div>
        <div class="user-stats">
          <span>📚 ${reviewCount} resenhas</span>
        </div>
      `;
      usersList.appendChild(card);
    });
  } catch (err) {
    console.error(err);
    usersList.innerHTML = "<p class='error'>Erro ao buscar usuários.</p>";
  }
}

// Seguir / deixar de seguir (expostos globalmente via onclick)
window.followUser = async function(userId) {
  if (!authToken) return;
  try {
    await apiFollowUser(authToken, userId);
    // Atualiza estado local e botão
    if (!Array.isArray(myFollowing)) myFollowing = [];
    if (!myFollowing.find(u => (u.id || u) === userId)) myFollowing.push({ id: userId });
    const btn = document.getElementById(`follow-btn-${userId}`);
    if (btn) {
      btn.textContent = 'Seguindo';
      btn.onclick = () => window.unfollowUser(userId);
    }
  } catch (err) {
    console.error(err);
    alert('Erro ao seguir usuário');
  }
}

window.unfollowUser = async function(userId) {
  if (!authToken) return;
  try {
    await apiUnfollowUser(authToken, userId);
    // Atualiza estado local e botão
    if (Array.isArray(myFollowing)) myFollowing = myFollowing.filter(u => (u.id || u) !== userId);
    const btn = document.getElementById(`follow-btn-${userId}`);
    if (btn) {
      btn.textContent = 'Seguir';
      btn.onclick = () => window.followUser(userId);
    }
  } catch (err) {
    console.error(err);
    alert('Erro ao deixar de seguir usuário');
  }
}

async function verPerfil(userId) {
  if (!authToken) return;
  if (!profileContent) return;

  currentViewingUserId = userId;
  profileContent.innerHTML = "<p>Carregando perfil...</p>";

  try {
    const user = await apiGetUserProfile(authToken, userId);
    const reviews = await apiGetUserReviews(authToken, userId);

    const nome = user.name || "Leitor anônimo";
    const bio = user.bio || "Um apreciador de histórias.";
    const reviewCount = reviews.length;

    let reviewsHtml = "<div class='divider'></div><h3>Resenhas deste leitor:</h3>";

    if (Array.isArray(reviews) && reviews.length > 0) {
      reviews.forEach((rev) => {
        const titulo = rev.title || "Resenha sem título";
        const texto = rev.text || rev.comment || "";
        const rating = rev.rating || 0;
        const livro = (rev.book && (rev.book.title || rev.book.nome)) || rev.bookTitle || "";

        let starsHtml = rating > 0 ? `<div class="item-rating">★${rating.toFixed(1)}</div>` : "";

        reviewsHtml += `
          <div class="review-card">
            <h4>${titulo}</h4>
            ${starsHtml}
            <p>${texto}</p>
            ${livro ? `<em>Sobre: ${livro}</em>` : ""}
          </div>
        `;
      });
    } else {
      reviewsHtml += "<p class='info'>Este leitor ainda não escreveu resenhas.</p>";
    }

    profileContent.innerHTML = `
      <div class="profile-header">
        <h2>${nome}</h2>
        <p class="profile-bio">${bio}</p>
      </div>
      <div class="profile-stats">
        <div class="stat">
          <span class="stat-number">${reviewCount}</span>
          <span class="stat-label">Resenhas</span>
        </div>
      </div>
      ${reviewsHtml}
      <button class="button secondary-btn" onclick="voltarAoMeuPerfil()" style="margin-top: 20px;">Voltar ao meu perfil</button>
    `;

    // Mudar para seção de perfil
    if (profileSection) {
      profileSection.classList.remove("hidden");
      [booksSection, exploreSection, feedSection, reviewsSection].forEach((sec) => {
        if (sec) sec.classList.add("hidden");
      });
      navButtons.forEach((b) => b.classList.remove("nav-active"));
      const profileBtn = document.querySelector('[data-target="profile-section"]');
      if (profileBtn) profileBtn.classList.add("nav-active");
    }
  } catch (err) {
    console.error(err);
    profileContent.innerHTML = "<p class='error'>Erro ao carregar perfil do usuário.</p>";
  }
}

async function voltarAoMeuPerfil() {
  currentViewingUserId = null;
  carregarPerfil();
}

// ---------- Feed ----------
async function carregarFeed() {
  if (!authToken) return;
  feedList.innerHTML = "<p>Carregando feed...</p>";

  try {
    const reviews = await apiGetReviews(authToken);

    if (!Array.isArray(reviews) || reviews.length === 0) {
      feedList.innerHTML = "<p class='info'>O salão está silencioso... nenhuma resenha ainda.</p>";
      return;
    }

    feedList.innerHTML = "";
    reviews.forEach((rev) => {
      const card = document.createElement("div");
      card.className = "feed-card";

      const titulo = rev.title || "Resenha sem título";
      const texto = rev.text || rev.comment || "";
      const rating = rev.rating || 0;
      const imageUrl = rev.imageUrl || "";
      const livro = (rev.book && (rev.book.title || rev.book.nome)) || rev.bookTitle || "";
      const autorReview = (rev.user && (rev.user.name || rev.user.nome)) || "Leitor misterioso";
      const authorId = rev.user && rev.user.id;

      let starsHtml = rating > 0 ? `<div class="item-rating">★${rating.toFixed(1)}</div>` : "";
      let imageHtml = imageUrl ? `<img src="${imageUrl}" alt="Foto da resenha" style="max-width: 100%; height: auto; border-radius: 6px; margin: 8px 0;">` : "";
      let authorLink = authorId ? `<span class="author-link" onclick="verPerfil(${authorId})" style="cursor: pointer; color: #9b7ebd; text-decoration: underline;">👤 ${autorReview}</span>` : `<span>👤 ${autorReview}</span>`;

      card.innerHTML = `
        <div class="item-header">
          <h3 class="item-title">${titulo}</h3>
          <div class="item-meta">${authorLink}</div>
        </div>
        ${starsHtml}
        <div class="item-body">
          ${texto}
        </div>
        ${imageHtml}
        ${livro ? `<div class="item-footer">📖 ${livro}</div>` : ""}
      `;
      feedList.appendChild(card);
    });
  } catch (err) {
    console.error(err);
    feedList.innerHTML = "<p class='error'>Erro ao carregar feed. Tente novamente.</p>";
  }
}

// ---------- Resenhas ----------
async function carregarResenhas() {
  if (!authToken) return;
  reviewsList.innerHTML = "<p>Carregando resenhas...</p>";

  try {
    const reviews = await apiGetReviews(authToken);

    if (!Array.isArray(reviews) || reviews.length === 0) {
      reviewsList.innerHTML =
        "<p class='info'>Ainda não há resenhas. Seja o primeiro a escrever.</p>";
      return;
    }

    reviewsList.innerHTML = "";
    reviews.forEach((rev) => {
      const card = document.createElement("div");
      card.className = "item-card";

      const titulo = rev.title || rev.titulo || "Resenha sem título";
      const texto = rev.text || rev.conteudo || rev.descricao || "";
      const rating = rev.rating || 0;
      const imageUrl = rev.imageUrl || "";
      const livro =
        (rev.book && (rev.book.title || rev.book.nome)) ||
        rev.bookTitle ||
        "";
      const autorReview =
        (rev.user && (rev.user.name || rev.user.nome)) || "Leitor misterioso";

      let starsHtml = "";
      if (rating > 0) {
        starsHtml = `<div class="item-rating">★${rating.toFixed(1)}</div>`;
      }

      let imageHtml = "";
      if (imageUrl) {
        imageHtml = `<img src="${imageUrl}" alt="Foto da resenha" style="max-width: 100%; height: auto; border-radius: 6px; margin: 8px 0;">`;
      }

      card.innerHTML = `
        <div class="item-header">
          <h3 class="item-title">${titulo}</h3>
          <span class="item-meta">${autorReview}</span>
        </div>
        ${starsHtml}
        <div class="item-body">
          ${texto}
        </div>
        ${imageHtml}
        <div class="item-footer">
          ${livro ? "Sobre: " + livro : ""}
        </div>
      `;
      reviewsList.appendChild(card);
    });
  } catch (err) {
    console.error(err);
    reviewsList.innerHTML =
      "<p class='error'>Erro ao carregar resenhas. Tente novamente.</p>";
  }
}

// ---------- Perfil ----------
async function carregarPerfil() {
  if (!authToken) return;
  profileContent.innerHTML = "<p>Carregando perfil...</p>";

  try {
    const profile = await apiGetProfile(authToken);
    const reviews = await apiGetReviews(authToken);

    const nome = profile.name || profile.nome || "Leitor sem nome";
    const email = profile.email || "Email não informado";
    const bio = profile.bio || "Um apreciador de páginas amareladas e noites chuvosas.";

    const livrosLidos = profile.readCount || profile.livrosLidos || 0;
    const reviewsCount = reviews.length;

    profileContent.innerHTML = `
      <div class="profile-header">
        <h2>${nome}</h2>
        <p class="profile-bio">${bio}</p>
      </div>
      <div class="profile-field">
        <span class="profile-label">📧 Email: </span>${email}
      </div>
      <div class="divider"></div>
      <div class="profile-stats">
        <div class="stat">
          <span class="stat-number">${livrosLidos}</span>
          <span class="stat-label">Livros Lidos</span>
        </div>
        <div class="stat">
          <span class="stat-number">${reviewsCount}</span>
          <span class="stat-label">Resenhas</span>
        </div>
      </div>
    `;
    // Atualiza lista de seguimentos local para marcação de botões
    myFollowing = profile.following || [];
  } catch (err) {
    console.error(err);
    profileContent.innerHTML = "<p class='error'>Erro ao carregar perfil do leitor.</p>";
  }
}

// ---------- Service Worker ----------
function registrarServiceWorker() {
  if ("serviceWorker" in navigator) {
    navigator.serviceWorker
      .register("service-worker.js")
      .catch((err) => console.error("Erro ao registrar SW:", err));
  }
}

// ---------- Funções wrapper para atualizar status de livro ----------
async function setBookStatusToRead(bookId) {
  try {
    console.log(`Marcando livro ${bookId} como QUERO_LER`);
    // backend espera os valores: QUERO_LER, LENDO, LIDO
    await apiSetBookStatus(authToken, bookId, 'QUERO_LER');
    console.log("Status atualizado com sucesso para QUERO_LER");
    await carregarMeusLivros();
  } catch (err) {
    console.error("Erro ao atualizar status para QUERO_LER:", err);
    alert(`Erro ao marcar como "Quero ler": ${err.message}`);
  }
}

async function setBookStatusReading(bookId) {
  try {
    console.log(`Marcando livro ${bookId} como LENDO`);
    await apiSetBookStatus(authToken, bookId, 'LENDO');
    console.log("Status atualizado com sucesso para LENDO");
    await carregarMeusLivros();
  } catch (err) {
    console.error("Erro ao atualizar status para LENDO:", err);
    alert(`Erro ao marcar como "Lendo": ${err.message}`);
  }
}

async function setBookStatusRead(bookId) {
  try {
    console.log(`Marcando livro ${bookId} como LIDO`);
    await apiSetBookStatus(authToken, bookId, 'LIDO');
    console.log("Status atualizado com sucesso para LIDO");
    await carregarMeusLivros();
  } catch (err) {
    console.error("Erro ao atualizar status para LIDO:", err);
    alert(`Erro ao marcar como "Lido": ${err.message}`);
  }
}

// ---------- Inicialização da aplicação ----------
document.addEventListener("DOMContentLoaded", () => {
  console.log("DOMContentLoaded - Inicializando app...");
  
  // Restaurar token do localStorage
  const savedToken = localStorage.getItem("token");
  if (savedToken) {
    authToken = savedToken;
    console.log("Token restaurado de localStorage");
  }
  
  // Inicializar elementos e listeners
  initializeElements();
  attachEventListeners();
  
  // Registrar Service Worker
  registrarServiceWorker();
  
  // Se já está autenticado, mostrar app
  if (authToken) {
    document.body.classList.add("app-active");
    mostrarApp();
    carregarTudo();
  }
  
  console.log("Inicialização completa");
});
