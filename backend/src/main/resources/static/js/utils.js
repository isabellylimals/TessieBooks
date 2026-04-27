// Funções auxiliares

function formatDate(dateString) {
  if (!dateString) return "Recentemente";
  const date = new Date(dateString);
  return date.toLocaleDateString("pt-BR", { month: "long", year: "numeric" });
}

function escapeHtml(text) {
  const div = document.createElement("div");
  div.textContent = text;
  return div.innerHTML;
}