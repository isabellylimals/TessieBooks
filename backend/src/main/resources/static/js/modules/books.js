// Módulo de Livros e Biblioteca

async function carregarMeusLivros() {
  const token = getToken();
  if (!token) return;

  const container = document.getElementById("my-books-list");
  if (!container) return;

  container.innerHTML = "<p>Carregando seus livros...</p>";

  try {
    const biblioteca = await apiGetMyLibrary(token);

    if (!biblioteca || biblioteca.length === 0) {
      container.innerHTML = "<p class='info'>Você ainda não adicionou livros à sua biblioteca.</p>";
      return;
    }

    container.innerHTML = "";
    biblioteca.forEach((item) => {
      const livro = item.book || item;
      const pagAtual = item.paginasLidas || 0;
      const pagTotal = item.paginasTotais || 0;
      const porcentagem = pagTotal > 0 ? Math.round((pagAtual / pagTotal) * 100) : 0;

      let cardHTML = `
        <div class="item-card">
          <div class="item-header">
            <h3 class="item-title">${escapeHtml(livro.title || "Título desconhecido")}</h3>
            <span class="item-meta">de ${escapeHtml(livro.author || "Autor desconhecido")}</span>
          </div>
          <div class="item-body">Status: <strong>${item.status || "—"}</strong></div>`;

      if (item.status === "LENDO") {
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
              <button class="save-progress-btn" onclick="salvarProgressoLivro(${livro.id}, this)">✒️</button>
            </div>
          </div>`;
      }

      cardHTML += `
          <div class="item-actions">
            <button class="button small-btn" data-book="${livro.id}" data-status="QUERO_LER">Quero ler</button>
            <button class="button small-btn" data-book="${livro.id}" data-status="LENDO">Lendo</button>
            <button class="button small-btn" data-book="${livro.id}" data-status="LIDO">Lido</button>
          </div>
        </div>`;

      container.innerHTML += cardHTML;
    });
  } catch (err) {
    console.error(err);
    container.innerHTML = "<p class='error'>Erro ao carregar sua biblioteca.</p>";
  }
}

async function carregarTodosOsLivros() {
  const token = getToken();
  if (!token) return;

  const container = document.getElementById("all-books-list");
  if (!container) return;

  container.innerHTML = "<p>Carregando livros...</p>";

  try {
    const livros = await apiGetBooks(token);

    if (!livros || livros.length === 0) {
      container.innerHTML = "<p class='info'>Nenhum livro encontrado.</p>";
      return;
    }

    container.innerHTML = "";
    livros.forEach((livro) => {
      container.innerHTML += `
        <div class="item-card">
          <div class="item-header">
            <h3 class="item-title">${escapeHtml(livro.title || "Título desconhecido")}</h3>
            <span class="item-meta">de ${escapeHtml(livro.author || "Autor desconhecido")}</span>
          </div>
          <div class="item-body">${escapeHtml(livro.description || "Sem descrição disponível.")}</div>
          <div class="item-actions">
            <button class="button small-btn" data-book="${livro.id}" data-status="QUERO_LER">Quero ler</button>
            <button class="button small-btn" data-book="${livro.id}" data-status="LENDO">Lendo</button>
            <button class="button small-btn" data-book="${livro.id}" data-status="LIDO">Lido</button>
          </div>
        </div>`;
    });
  } catch (err) {
    console.error(err);
    container.innerHTML = "<p class='error'>Erro ao carregar livros.</p>";
  }
}

async function salvarProgressoLivro(bookId, btn) {
  const token = getToken();
  const container = btn.closest(".reading-progress-container");
  const pagAtual = container.querySelector(".curr-page").value;
  const pagTotal = container.querySelector(".total-pages").value;

  try {
    await apiUpdateBookProgress(token, bookId, pagAtual, pagTotal);
    alert("Progresso registrado no diário! ✒️");
    await carregarMeusLivros();
  } catch (err) {
    alert("Erro ao salvar progresso: " + err.message);
  }
}

function setupBookStatusButtons() {
  document.body.addEventListener("click", async (e) => {
    const btn = e.target.closest("[data-book][data-status]");
    if (!btn) return;

    const bookId = btn.dataset.book;
    const status = btn.dataset.status;

    try {
      await apiSetBookStatus(getToken(), bookId, status);
      await carregarMeusLivros();
    } catch (err) {
      alert("Erro ao atualizar status: " + err.message);
    }
  });
}