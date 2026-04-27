// Módulo de Autenticação
let authToken = null;

function getToken() {
  return authToken || localStorage.getItem("token");
}

function setToken(token) {
  authToken = token;
  if (token) {
    localStorage.setItem("token", token);
  } else {
    localStorage.removeItem("token");
  }
}

async function handleLogin(email, password, onSuccess) {
  try {
    const result = await apiLogin(email, password);
    if (result.token) {
      setToken(result.token);
      document.body.classList.add("app-active");
      if (onSuccess) onSuccess();
      return { success: true };
    }
    return { success: false, error: "Credenciais inválidas" };
  } catch (err) {
    return { success: false, error: err.message };
  }
}

function handleLogout() {
  setToken(null);
  document.body.classList.remove("app-active");
  document.getElementById("app-section").classList.add("hidden");
  document.getElementById("login-section").classList.remove("hidden");
}