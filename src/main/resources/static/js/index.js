const rebuildButton = document.querySelector("#rebuild-index");
const indexStatus = document.querySelector("#index-status");

if (rebuildButton) {
    rebuildButton.addEventListener("click", async () => {
        indexStatus.textContent = "Rebuilding vector index...";
        const response = await fetch("/api/index/rebuild", { method: "POST" });
        const result = await response.json();
        indexStatus.textContent = result.message;
    });
}
