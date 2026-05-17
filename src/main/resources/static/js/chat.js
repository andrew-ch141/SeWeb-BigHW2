const widget = document.querySelector("#chat-widget");
const toggle = document.querySelector("#chat-toggle");
const panel = document.querySelector("#chat-panel");
const starters = document.querySelector("#chat-starters");
const messages = document.querySelector("#chat-messages");
const form = document.querySelector("#chat-form");
const input = document.querySelector("#chat-input");

const starterMap = {
    home: [
        "Which user has beginner reading level?",
        "Which themes are preferred by the users?",
        "Recommend a book for Bob."
    ],
    books: [
        "What is a book that I am most likely to enjoy from this list?",
        "Which books are Science Fiction?",
        "What book has the author Frank Herbert and the theme Science Fiction?"
    ],
    book: [
        "Who wrote this book?",
        "What themes does this book have?",
        "Which user would enjoy this book?"
    ],
    graph: [
        "What facts are present in the RDF graph?",
        "Which books and users are represented?",
        "How is reading level used for recommendations?"
    ]
};

function addMessage(text, role) {
    const element = document.createElement("div");
    element.className = `chat-message ${role}`;
    element.textContent = text;
    messages.appendChild(element);
    messages.scrollTop = messages.scrollHeight;
}

function renderStarters() {
    const pageType = widget.dataset.pageType || "home";
    const questions = starterMap[pageType] || starterMap.home;
    starters.innerHTML = "";
    questions.forEach((question) => {
        const button = document.createElement("button");
        button.type = "button";
        button.className = "starter";
        button.textContent = question;
        button.addEventListener("click", () => {
            input.value = question;
            form.requestSubmit();
        });
        starters.appendChild(button);
    });
}

async function askChat(message) {
    const response = await fetch("/api/chat", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            message,
            pageType: widget.dataset.pageType || "home",
            bookId: widget.dataset.bookId || ""
        })
    });
    return response.json();
}

toggle.addEventListener("click", () => {
    panel.classList.toggle("hidden");
});

form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const message = input.value.trim();
    if (!message) {
        return;
    }
    addMessage(message, "user");
    input.value = "";
    addMessage("Searching the vector database...", "assistant");
    const response = await askChat(message);
    messages.lastChild.textContent = response.answer;
});

renderStarters();
