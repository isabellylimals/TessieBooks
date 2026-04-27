// Módulo de Navegação

function setupNavigation() {
  const navButtons = document.querySelectorAll(".nav-btn:not(#logoutBtn)");
  const sections = {
    "books-section": document.getElementById("books-section"),
    "explore-section": document.getElementById("explore-section"),
    "feed-section": document.getElementById("feed-section"),
    "reviews-section": document.getElementById("reviews-section"),
    "profile-section": document.getElementById("profile-section"),
  };

  navButtons.forEach((btn) => {
    const target = btn.dataset.target;
    if (!target) return;

    btn.addEventListener("click", () => {
      navButtons.forEach((b) => b.classList.remove("nav-active"));
      btn.classList.add("nav-active");

      Object.values(sections).forEach((sec) => {
        if (sec) sec.classList.add("hidden");
      });

      const targetSection = sections[target];
      if (targetSection) targetSection.classList.remove("hidden");

      // Carregar conteúdo dinâmico
      if (target === "books-section") {
        carregarMeusLivros();
        carregarTodosOsLivros();
      }
      if (target === "explore-section") carregarComunidade();
      if (target === "feed-section") carregarFeed();
      if (target === "reviews-section") carregarResenhas();
      if (target === "profile-section") carregarPerfil();
    });
  });
}

function setupTabs() {
  const tabBtns = document.querySelectorAll(".tab-btn");
  tabBtns.forEach((btn) => {
    btn.addEventListener("click", () => {
      const tabName = btn.dataset.tab;
      tabBtns.forEach((b) => b.classList.remove("active"));
      btn.classList.add("active");

      document.querySelectorAll(".tab-content").forEach((tab) => {
        tab.classList.add("hidden");
      });

      const activeTab = document.getElementById(tabName);
      if (activeTab) activeTab.classList.remove("hidden");

      if (tabName === "my-books") carregarMeusLivros();
      if (tabName === "all-books") carregarTodosOsLivros();
    });
  });
}

function setupLogout() {
  const logoutBtn = document.getElementById("logoutBtn");
  logoutBtn?.addEventListener("click", () => {
    handleLogout();
  });
}

function inicializarApp() {
  const savedToken = localStorage.getItem("token");
  if (savedToken) {
    authToken = savedToken;
    document.body.classList.add("app-active");
    document.getElementById("app-section").classList.remove("hidden");
    document.getElementById("login-section").classList.add("hidden");
    document.getElementById("register-section").classList.add("hidden");
    carregarTudo();
  }
}

async function carregarTudo() {
  await carregarMeusLivros();
  await carregarTodosOsLivros();
  await carregarComunidade();
  await carregarFeed();
  await carregarResenhas();
  await carregarPerfil();
}