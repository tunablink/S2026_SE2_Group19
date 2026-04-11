(function () {
  if (window.__siteSearchReady) {
    return;
  }
  window.__siteSearchReady = true;

  const modal = document.querySelector("[data-search-modal]");
  const input = document.querySelector("[data-search-input]");
  const results = document.querySelector("[data-search-results]");
  const openButtons = document.querySelectorAll("[data-search-open]");
  const closeButtons = document.querySelectorAll("[data-search-close]");
  let debounceTimer;
  let activeController;

  if (!modal || !input || !results || openButtons.length === 0) {
    return;
  }

  function openSearch(event) {
    if (event) {
      event.preventDefault();
    }
    modal.classList.add("site-search--open");
    modal.setAttribute("aria-hidden", "false");
    document.body.classList.add("site-search-lock");
    window.setTimeout(function () {
      input.focus();
    }, 30);
  }

  function closeSearch() {
    modal.classList.remove("site-search--open");
    modal.setAttribute("aria-hidden", "true");
    document.body.classList.remove("site-search-lock");
  }

  function setMessage(message) {
    results.innerHTML = '<p class="site-search__empty">' + escapeHtml(message) + "</p>";
  }

  function escapeHtml(value) {
    return String(value)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }

  function renderResults(items) {
    if (!items.length) {
      setMessage("No results found.");
      return;
    }

    results.innerHTML = items.map(function (item) {
      const badge = item.requiresLogin ? '<span class="site-search__login">Login required</span>' : "";
      return [
        '<a class="site-search__item" href="' + escapeHtml(item.url) + '">',
        '<span class="site-search__type">' + escapeHtml(item.type) + "</span>",
        "<strong>" + escapeHtml(item.title) + "</strong>",
        "<small>" + escapeHtml(item.description) + "</small>",
        badge,
        "</a>"
      ].join("");
    }).join("");
  }

  async function runSearch() {
    const query = input.value.trim();
    if (query.length < 2) {
      setMessage("Type at least 2 characters to search.");
      return;
    }

    if (activeController) {
      activeController.abort();
    }
    activeController = new AbortController();
    setMessage("Searching...");

    try {
      const response = await fetch("/api/search?q=" + encodeURIComponent(query), {
        signal: activeController.signal,
        credentials: "same-origin"
      });
      if (!response.ok) {
        throw new Error("Search failed with status " + response.status);
      }
      renderResults(await response.json());
    } catch (error) {
      if (error.name !== "AbortError") {
        setMessage(error.message);
      }
    }
  }

  openButtons.forEach(function (button) {
    button.addEventListener("click", openSearch);
  });

  closeButtons.forEach(function (button) {
    button.addEventListener("click", closeSearch);
  });

  input.addEventListener("input", function () {
    window.clearTimeout(debounceTimer);
    debounceTimer = window.setTimeout(runSearch, 220);
  });

  document.addEventListener("keydown", function (event) {
    if (event.key === "Escape" && modal.classList.contains("site-search--open")) {
      closeSearch();
    }
    if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === "k") {
      openSearch(event);
    }
  });
})();
