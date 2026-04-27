// app.js - Arquivo principal que orquestra todos os módulos

// Aguarda o DOM carregar
document.addEventListener("DOMContentLoaded", () => {
  console.log("Inicializando TessieVerse...");

  // Configurar autenticação
  const loginBtn = document.getElementById("loginBtn");
  const registerBtn = document.getElementById("registerBtn");
  const toggleRegister = document.getElementById("toggleRegister");
  const toggleLogin = document.getElementById("toggleLogin");

  loginBtn?.addEventListener("click", async () => {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const errorEl = document.getElementById("loginError");

    const result = await handleLogin(email, password, () => {
      inicializarApp();
    });

    if (!result.success && errorEl) {
      errorEl.textContent = result.error;
    }
  });

  registerBtn?.addEventListener("click", async () => {
    const name = document.getElementById("register-name").value;
    const email = document.getElementById("register-email").value;
    const password = document.getElementById("register-password").value;
    const confirm = document.getElementById("register-password-confirm").value;
    const errorEl = document.getElementById("registerError");

    if (password !== confirm) {
      errorEl.textContent = "As senhas não coincidem.";
      return;
    }

    if (password.length < 6) {
      errorEl.textContent = "A senha deve ter pelo menos 6 caracteres.";
      return;
    }

    try {
      await apiRegister(name, email, password);
      errorEl.style.color = "#2ecc71";
      errorEl.textContent = "Conta criada! Faça login.";
      setTimeout(() => {
        document.getElementById("register-name").value = "";
        document.getElementById("register-email").value = "";
        document.getElementById("register-password").value = "";
        document.getElementById("register-password-confirm").value = "";
        document.getElementById("login-section").classList.remove("hidden");
        document.getElementById("register-section").classList.add("hidden");
      }, 2000);
    } catch (err) {
      errorEl.style.color = "#e49d9d";
      errorEl.textContent = err.message;
    }
  });

  toggleRegister?.addEventListener("click", (e) => {
    e.preventDefault();
    document.getElementById("login-section").classList.add("hidden");
    document.getElementById("register-section").classList.remove("hidden");
  });

  toggleLogin?.addEventListener("click", (e) => {
    e.preventDefault();
    document.getElementById("login-section").classList.remove("hidden");
    document.getElementById("register-section").classList.add("hidden");
  });

  // Configurar módulos
  setupLogout();
  setupNavigation();
  setupTabs();
  setupBookStatusButtons();
  setupPostForm();
  setupReviewStars();
  setupReviewForm();
  setupProfileImageUpload();
  setupSearchUsers();

  // Inicializar se já estiver logado
  inicializarApp();
});

// Variáveis globais acessíveis
window.verPerfil = verPerfil;
window.showFollowers = showFollowers;
window.showFollowing = showFollowing;
window.closeModal = closeModal;
window.showUserReviews = showUserReviews;
window.showUserLibrary = showUserLibrary;
window.salvarProgressoLivro = salvarProgressoLivro;
window.deletarPost = deletarPost;